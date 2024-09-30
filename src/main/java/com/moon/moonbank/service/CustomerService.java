package com.moon.moonbank.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moon.lib.minio.MinioService;
import com.moon.moonbank.dto.request.CustomerRequest;
import com.moon.moonbank.dto.response.CustomerResponse;
import com.moon.moonbank.dto.response.MessageResponse;
import com.moon.moonbank.dto.response.MessageResponse.Meta;
import com.moon.moonbank.dto.response.CustomerResponse.Pic;
import com.moon.moonbank.model.Customer;
import com.moon.moonbank.model_elastic.CustomerElastic;
import com.moon.moonbank.repository.CustomerRepository;
import com.moon.moonbank.repository_elastic.CustomerRepositoryElastic;
import com.moon.moonbank.util.ResponseUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerService {

   @Autowired
   private ResponseUtil responseUtil;

   @Autowired
   private CustomerRepository customerRepository;

   @Autowired
   private MinioService minioService;

   @Autowired
   private CustomerRepositoryElastic customerRepositoryElastic;

   // DISPLAY CUSTOMER

   public ResponseEntity<MessageResponse> getAllCustomers(String keyword, Pageable pageable) {

      // validate keyword
      String search = keyword;
      if (search == null || search.isEmpty()) {
         search = "";
      }

      try {

         Page<CustomerElastic> resultPage = customerRepositoryElastic.searchByKeyword(search, pageable);

         Meta meta = Meta.builder()
               .total(resultPage.getTotalElements())
               .perPage(pageable.getPageSize())
               .currentPage(pageable.getPageNumber() + 1)
               .lastPage(resultPage.getTotalPages())
               .build();

         List<CustomerElastic> modifiedResultPage = resultPage.stream()
               .filter(result -> result.getIsActive()) // Filter untuk is_active = true
               .map(result -> {
                  result.setPicUrl(minioService.getPublicLink(result.getPic())); // Set picUrl dengan public link
                  return result;
               })
               .collect(Collectors.toList());

         return responseUtil.okWithDataAndMeta("Successfully get data", modifiedResultPage, meta);

      }catch (Exception e) {
         e.printStackTrace();
         return responseUtil.internalServerError("error.server: " + e.getMessage() + " - " + e.getCause());
      }
   }

   // ADD CUSTOMER
   @Transactional
   public ResponseEntity<MessageResponse> addCustomer(CustomerRequest request) {

      // validate requesrt TODO

      // validate when created customer code
      if (generateCustomerCode() == null) {
         return responseUtil.internalServerError("error.server");
      }

      // validate eksistensi of pic
      Boolean isFileExist = minioService.doesFileExist(request.getPic());
      if (!isFileExist) {
         return responseUtil.notFound(request.getPic() + " is not exist");
      }

      try {

         // create customer
         Customer customer = Customer.builder()
               .customerName(request.getCustomerName())
               .customerAddress(request.getCustomerAddress())
               .customerPhone(request.getCustomerPhone())
               .pic(request.getPic())

               // hidden
               .isActive(true)
               .customerCode(generateCustomerCode())
               .build();

         customerRepository.save(customer);

         // create response
         CustomerResponse customerResponse = buildCustomerResponse(customer);

         return responseUtil.okWithData("success.save.data", customerResponse);

      } catch (Exception e) {
         return responseUtil.notFound("error.server");
      }

   }

   // DELETE CUSTOMER
   @Transactional
   public ResponseEntity<MessageResponse> deleteCustomer(String customerCode) {

      // validate customerCode
      ResponseEntity<MessageResponse> validateCustomerCodeResult = validateCustomerCode(customerCode);
      if (validateCustomerCodeResult != null) {
         return validateCustomerCodeResult;
      }

      try {

         Optional<Customer> customerOpt = customerRepository.findCustomerByCustomerCode(customerCode);

         // remove customer
         Customer customer = customerOpt.get();
         customer.setIsActive(false);

         return responseUtil.ok("Customer deleted successfully");
      } catch (Exception e) {
         return responseUtil.notFound("error.server");
      }
   }

   // UPDATE CUSTOMER
   @Transactional
   public ResponseEntity<MessageResponse> updateCustomer(String customerCode, CustomerRequest request) {

      // validate customerCode
      ResponseEntity<MessageResponse> validateCustomerCodeResult = validateCustomerCode(customerCode);
      if (validateCustomerCodeResult != null) {
         return validateCustomerCodeResult;
      }

      try {

         // is customer exist
         Optional<Customer> customerOpt = customerRepository.findCustomerByCustomerCode(customerCode);

         // remove customer
         Customer customer = customerOpt.get();
         customer.setCustomerName(request.getCustomerName());
         customer.setCustomerAddress(request.getCustomerAddress());
         customer.setCustomerPhone(request.getCustomerPhone());
         customer.setPic(request.getPic());

         CustomerResponse customerResponse = buildCustomerResponse(customer);

         return responseUtil.okWithData("success.update.data", customerResponse);
      } catch (Exception e) {
         return responseUtil.notFound("error.server");
      }
   }

   // VIEW CUSTOMER DETAIL
   public ResponseEntity<MessageResponse> getCustomer(String customerCode) {

      // validate customerCode
      ResponseEntity<MessageResponse> validateCustomerCodeResult = validateCustomerCode(customerCode);
      if (validateCustomerCodeResult != null) {
         return validateCustomerCodeResult;
      }

      try {

         Optional<Customer> customerOpt = customerRepository.findCustomerByCustomerCode(customerCode);

         // remove customer
         CustomerResponse customerResponse = buildCustomerResponse(customerOpt.get());

         return responseUtil.okWithData("success.load.data", customerResponse);
      } catch (Exception e) {
         return responseUtil.notFound("error.server");
      }
   }

   // GENERATE CUSTOMR CODE
   // customer code = CUS <2d tahun> <2d bulan> <4 digit no urut berdasarkan bulan,
   // jika bulan baru reset ke 1>
   public String generateCustomerCode() {

      // get current dete
      LocalDate today = LocalDate.now();
      int year = today.getYear() % 100; // get 2 digit, of the last year number
      int month = today.getMonthValue(); // get 2 digit of month

      // cek
      log.info("today " + today);
      log.info("year " + year);
      log.info("month " + month);

      // format yaer and mont to 2 digit
      String formattedYear = String.format("%02d", year);
      String formattedMonth = String.format("%02d", month);

      try {
         // get count/total customer for this month
         String currentMonth = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));
         int customerCountThisMonth = customerRepository.countByMonth(currentMonth);

         // add 1 to "nomor urut" for new customer
         int newCustomerNumber = customerCountThisMonth + 1;

         // format number to 4 digit
         String formattedCustomerNumber = String.format("%04d", newCustomerNumber);

         // join all for customer code
         return "CUS" + formattedYear + formattedMonth + formattedCustomerNumber;

      } catch (Exception e) {
         return null;
      }

   }

   // VALIDATE CUSTOMER CODE
   private boolean isValidCustomerCode(String customerCode) {
      return customerCode != null && customerCode.startsWith("CUS") && customerCode.length() == 11;
   }

   private CustomerResponse buildCustomerResponse(Customer customer) {

      CustomerResponse customerResponse = CustomerResponse.builder()
            .customerCode(customer.getCustomerCode())
            .customerName(customer.getCustomerName())
            .customerAddress(customer.getCustomerAddress())
            .customerPhone(customer.getCustomerPhone())
            .pic(new Pic(customer.getPic(), minioService.getPublicLink(customer.getPic())))
            .lastOrderDate(customer.getLastOrderDate())
            .build();
      return customerResponse;
   }

   public ResponseEntity<MessageResponse> validateCustomerCode(String customerCode) {

      if (!isValidCustomerCode(customerCode)) {
         return responseUtil.badRequest("Invalid Customer Code");
      }

      try {
         // is customer exist
         Optional<Customer> customerOpt = customerRepository.findCustomerByCustomerCode(customerCode);
         if (customerOpt.isEmpty()) {
            return responseUtil.notFound("Customer is not exist");
         }
         // is item active
         if (!customerOpt.get().getIsActive()) {
            return responseUtil.badRequest("Customer has deleted");
         }

      } catch (Exception e) {
         return responseUtil.notFound("error.server");
      }

      return null; // valid

   }
}
