package com.moon.moonbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moon.moonbank.dto.request.CustomPageRequest;
import com.moon.moonbank.dto.request.CustomerRequest;
import com.moon.moonbank.dto.request.FilterDTO;
import com.moon.moonbank.dto.response.MessageResponse;
import com.moon.moonbank.service.CustomerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/customer")
public class CustomerController {

   @Autowired
   private CustomerService customerService;

   // DISPLAY CUSTOMER
   @GetMapping("/all-customers")
   public ResponseEntity<MessageResponse> getAllCustomers(FilterDTO filter, CustomPageRequest customPageRequest) {
      return customerService.getAllCustomers(filter, customPageRequest.getPage("customer_name.keyword,asc"));
   }

   // ADD CUSTOMER
   @PostMapping("/add-customer")
   public ResponseEntity<MessageResponse> postCustomer(@RequestBody CustomerRequest request) {
      return customerService.addCustomer(request);
   }

   // DELETE CUSTOMER
   @PutMapping("/delete-customer/{customerCode}")
   public ResponseEntity<MessageResponse> delCustomer(@PathVariable String customerCode) {
      return customerService.deleteCustomer(customerCode);
   }

   // UPDATE CUSTOMER
   @PutMapping("/update-customer/{customerCode}")
   public ResponseEntity<MessageResponse> putCustomer(@PathVariable String customerCode,
         @RequestBody CustomerRequest customerRequest) {
      return customerService.updateCustomer(customerCode, customerRequest);
   }

   // VIEW CUSTOMER DETAIL
   @GetMapping("/detail-customer/{customerCode}")
   public ResponseEntity<MessageResponse> getCustomer(@PathVariable String customerCode) {
      return customerService.getCustomer(customerCode);
   }


   // REACTIVE CUSTOMER DETAIL
   @PutMapping("/reactive-customer/{customerCode}")
   public ResponseEntity<MessageResponse> reactivCustomer(@PathVariable String customerCode) {
      return customerService.reactiveCustomer(customerCode);
   }

}
