package com.moon.moonbank.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
import com.moon.moonbank.dto.request.OrderRequest;
import com.moon.moonbank.dto.request.OrderRequest.Items;
import com.moon.moonbank.dto.request.OrderUpdateRequest;
import com.moon.moonbank.dto.response.MessageResponse;
import com.moon.moonbank.dto.response.MessageResponse.Meta;
import com.moon.moonbank.model.Customer;
import com.moon.moonbank.model.Item;
import com.moon.moonbank.model.Order;
import com.moon.moonbank.model_elastic.OrderElastic;
import com.moon.moonbank.repository.CustomerRepository;
import com.moon.moonbank.repository.ItemRepository;
import com.moon.moonbank.repository.OrderRepository;
import com.moon.moonbank.repository_elastic.OrdersRepositoryElastic;
import com.moon.moonbank.util.ResponseUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService {

   @Autowired
   private ResponseUtil responseUtil;

   @Autowired
   private OrderRepository orderRepository;

   @Autowired
   private CustomerRepository customerRepository;

   @Autowired
   private ItemRepository itemRepository;

   @Autowired
   private ItemService itemService;

   @Autowired
   private CustomerService customerService;

   @Autowired
   private OrdersRepositoryElastic ordersRepositoryElastic;

   @Autowired
   private MinioService minioService;

   // DISPLAY ORDER LIST
   // get with elastic for best performance
   public ResponseEntity<MessageResponse> getAllOrders(String keyword, Pageable pageable) {

      // validate keyword
      String search = keyword;
      if (search == null || search.isEmpty()) {
         search = "";
      }

      try {

         Page<OrderElastic> resultPage = ordersRepositoryElastic.searchByKeyword(search, pageable);

         log.info(resultPage.getContent().toString());

         Meta meta = Meta.builder()
               .total(resultPage.getTotalElements())
               .perPage(pageable.getPageSize())
               .currentPage(pageable.getPageNumber() + 1)
               .lastPage(resultPage.getTotalPages())
               .build();

         List<OrderElastic> modifiedResultPage = resultPage.stream()
               .map(result -> {

                  // Set URL customer pic
                  result.getCustomer().setPicUrl(minioService.getPublicLink(result.getCustomer().getPic()));

                  // Cek apakah semua item memiliki is_active == false
                  boolean allItemsInactive = result.getItems().stream()
                        .allMatch(item -> !item.getIsActive());

                  // Jika semua item inactive, set isOrderActive pada order menjadi false
                  result.setIsOrderActive(!allItemsInactive);

                  // Filter item yang hanya is_active == true
                  List<OrderElastic.Item> activeItems = result.getItems().stream()
                        .filter(OrderElastic.Item::getIsActive) // get item hanya item yang aktif
                        .collect(Collectors.toList());

                  // Set jumlah item aktif (total_item) ke jumlah dari activeItems
                  result.setTotalItem(activeItems.size());

                  double totalOrderPrice = activeItems.stream()
                        .mapToDouble(OrderElastic.Item::getTotalItemPrice) // Mengubah stream ke double stream
                        .sum(); // Menjumlahkan semua nilai total_item_price dari item aktif

                  result.setTotalOrderPrice(totalOrderPrice);

                  result.setItems(activeItems); // Mengupdate daftar items di order

                  return result;
               })
               .filter(OrderElastic::getIsOrderActive) // Filter hanya order yang aktif
               .collect(Collectors.toList());

         return responseUtil.okWithDataAndMeta("Successfully get data", modifiedResultPage, meta);

      } catch (Exception e) {

         e.printStackTrace();
         return responseUtil.internalServerError("error.server: " + e.getMessage());
      }
   }

   // ADD ORDER
   @Transactional
   public ResponseEntity<MessageResponse> addOrder(OrderRequest request) {

      // validate request TODO

      // validate itemCode
      for (Items i : request.getItems()) {
         ResponseEntity<MessageResponse> validateItemCodeResult = itemService.validateItemCode(i.getItemCode());
         if (validateItemCodeResult != null) {
            return validateItemCodeResult;
         }
      }

      // validate customerCode
      ResponseEntity<MessageResponse> validateCustomerCodeResult = customerService
            .validateCustomerCode(request.getCustomerCode());
      if (validateCustomerCodeResult != null) {
         return validateCustomerCodeResult;
      }

      // validate when created order code
      if (generateOrderCode() == null) {
         return responseUtil.internalServerError("error.server");
      }

      try {

         // validate quantity
         for (OrderRequest.Items i : request.getItems()) {
            Item item = itemRepository.findItemByItemCode(i.getItemCode()).get();

            if (i.getQuantity() > item.getStock()) {
               return responseUtil.internalServerError("stok terbatas, tambah lagi stock");
            }
         }

         Optional<Customer> customer = customerRepository.findCustomerByCustomerCode(request.getCustomerCode());
         List<Item> items = new ArrayList<>();
         List<Order> orders = new ArrayList<>();

         String orderCode = generateOrderCode();

         for (Items i : request.getItems()) {
            Optional<Item> item = itemRepository.findItemByItemCode(i.getItemCode());
            items.add(item.get());

            Order order = Order.builder()

                  .orderCode(orderCode)
                  .customer(customer.get())
                  .item(item.get())
                  .quantity(i.getQuantity())
                  .totalPrice(item.get().getPrice() * i.getQuantity())
                  .orderDate(LocalDateTime.now())
                  .isActive(true)
                  .build();
            orders.add(order);

            item.get().setStock(item.get().getStock() - i.getQuantity());

         }
         orderRepository.saveAll(orders);
         customer.get().setLastOrderDate(orders.get(0).getOrderDate());

         return responseUtil.ok("success.save.data");
      } catch (Exception e) {
         return responseUtil.notFound("error.server");
      }
   }

   // DELETE ORDER
   @Transactional
   public ResponseEntity<MessageResponse> deleteOrder(String orderCode) {

      // validate itemCode
      ResponseEntity<MessageResponse> validateOrderCodeResult = validateOrderCode(orderCode);
      if (validateOrderCodeResult != null) {
         return validateOrderCodeResult;
      }

      try {

         List<Order> listOrder = orderRepository.findOrderByOrderCode(orderCode);

         // soft delete
         // set modified time
         LocalDateTime modifiedTime = LocalDateTime.now();
         for (Order o : listOrder) {
            o.setIsActive(false);
            o.setModiefiedDate(modifiedTime);
         }

         return responseUtil.ok("Order successfuly deleted");
      } catch (Exception e) {
         return responseUtil.notFound("error.server");
      }
   }

   // UPDATE ORDER
   // todo JIKA ITEMNYA BERUBAH
   @Transactional
   public ResponseEntity<MessageResponse> updateOrder(String orderCode, OrderUpdateRequest request) {

      // validate itemCode
      ResponseEntity<MessageResponse> validateOrderCodeResult = validateOrderCode(orderCode);
      if (validateOrderCodeResult != null) {
         return validateOrderCodeResult;
      }

      // validate customerCode
      ResponseEntity<MessageResponse> validateCustomerCodeResult = customerService
            .validateCustomerCode(request.getCustomerCode());
      if (validateCustomerCodeResult != null) {
         return validateCustomerCodeResult;
      }

      // validate orderId
      ResponseEntity<MessageResponse> validateOrderIdResult = validateOrderId(request);
      if (validateOrderIdResult != null) {
         return validateOrderIdResult;
      }

      // validate when created order code
      if (generateOrderCode() == null) {
         return responseUtil.internalServerError("error.server");
      }

      try {

         List<Order> orders = orderRepository.findOrderByOrderCode(orderCode);

         LocalDateTime lastModified = LocalDateTime.now();

         // validate qty

         for (OrderUpdateRequest.Items item : request.getItems()) {

            Optional<Order> order = orderRepository.findById(item.getOrderId());
            Item itemOriginal = itemRepository.findItemByItemCode(item.getItemCode()).get();

            // jika qty req < qty curent -> tambahkan qty selisih ke stoc item master
            if (item.getQuantity() < order.get().getQuantity()) {
               int selisih = order.get().getQuantity() - item.getQuantity();
               itemOriginal.setStock(itemOriginal.getStock() + selisih);
            }

            // jika qty req > qty curent -> validasi dan lakukan update
            if (item.getQuantity() > order.get().getQuantity()) {

               int selisih = item.getQuantity() - order.get().getQuantity();

               ResponseEntity<MessageResponse> vq = validateQuantity(selisih, item);
               if (vq != null) {
                  return vq;
               }
               itemOriginal.setStock(itemOriginal.getStock() - (item.getQuantity() - order.get().getQuantity()));
            }
         }

         // update item :: item ,quantity, active
         for (OrderUpdateRequest.Items item : request.getItems()) {

            Optional<Order> order = orderRepository.findById(item.getOrderId());

            order.get().setItem(itemRepository.findItemByItemCode(item.getItemCode()).get());
            order.get().setQuantity(item.getQuantity());
            order.get().setTotalPrice(item.getQuantity() * order.get().getItem().getPrice());
            order.get().setIsActive(item.getIsActive());

         }

         // update all :: customer and all
         // update customer last_order_date
         orders.stream()
               .map(order -> {

                  Customer customer = customerRepository.findCustomerByCustomerCode(request.getCustomerCode()).get();
                  customer.setLastOrderDate(lastModified);

                  order.setCustomer(customer);
                  order.setModiefiedDate(lastModified);
                  return order;
               })
               .filter(Order::getIsActive)
               .collect(Collectors.toList());

         return responseUtil.ok("success.load.data");

      } catch (Exception e) {
         return responseUtil.internalServerError("error.server");
      }
   }

   // VIEW ORDER DETAILS
   @Transactional
   public ResponseEntity<MessageResponse> getOrder(String orderCode) {

      // validate itemCode
      ResponseEntity<MessageResponse> validateOrderCodeResult = validateOrderCode(orderCode);
      if (validateOrderCodeResult != null) {
         return validateOrderCodeResult;
      }

      try {

         Optional<OrderElastic> orderElastic = ordersRepositoryElastic.findById(orderCode);

         orderElastic.ifPresent(result -> {

            // Set URL customer pic
            result.getCustomer().setPicUrl(minioService.getPublicLink(result.getCustomer().getPic()));

            // Cek apakah semua item memiliki is_active == false
            boolean allItemsInactive = result.getItems().stream()
                  .allMatch(item -> !item.getIsActive());

            // Jika semua item inactive, set isOrderActive pada order menjadi false
            result.setIsOrderActive(!allItemsInactive);

            // Filter item yang hanya is_active == true
            List<OrderElastic.Item> activeItems = result.getItems().stream()
                  .filter(OrderElastic.Item::getIsActive) // get item hanya item yang aktif
                  .collect(Collectors.toList());

            // Set jumlah item aktif (total_item) ke jumlah dari activeItems
            result.setTotalItem(activeItems.size());

            // Jumlahkan total harga item yang aktif
            double totalOrderPrice = activeItems.stream()
                  .mapToDouble(OrderElastic.Item::getTotalItemPrice) // Mengubah stream ke double stream
                  .sum(); // Menjumlahkan semua nilai total_item_price dari item aktif

            result.setTotalOrderPrice(totalOrderPrice);

            result.setItems(activeItems); // Mengupdate daftar items di order
         });

         return responseUtil.okWithData("success.load.data", orderElastic);
      } catch (Exception e) {
         return responseUtil.internalServerError("error.server");
      }
   }

   // DOWNLOAD THE EVAILABLE ORDER DATA LIST REPORT

   // GENERATE ORDER CODE
   public String generateOrderCode() {

      // Dapatkan tanggal saat ini
      LocalDate today = LocalDate.now();
      LocalTime now = LocalTime.now(); // Dapatkan waktu saat ini (jam, menit, detik)

      int year = today.getYear() % 100; // Ambil 2 digit terakhir tahun
      int month = today.getMonthValue(); // Ambil 2 digit bulan
      int day = today.getDayOfMonth(); // Ambil 2 digit hari

      // Format tahun, bulan, dan hari menjadi 2 digit
      String formattedYear = String.format("%02d", year);
      String formattedMonth = String.format("%02d", month);
      String formattedDay = String.format("%02d", day);

      // Format jam, menit, dan detik menjadi 2 digit
      String hour = String.format("%02d", now.getHour());
      String minute = String.format("%02d", now.getMinute());
      String second = String.format("%02d", now.getSecond());

      // Dapatkan mili detik
      long milliseconds = System.currentTimeMillis() % 1000; // Ambil 3 digit terakhir dari mili detik

      try {
         // Gabungkan hari, waktu (HHmmss), dan mili detik sebagai bagian unik
         String uniquePart = formattedDay + "_" + hour + minute + second + "_" + String.format("%03d", milliseconds);

         // Gabungkan semuanya untuk menghasilkan kode pesanan
         return "ORDER" + formattedYear + formattedMonth + uniquePart;

      } catch (Exception e) {
         return null;
      }

   }

   // VALIDATE ORDER CODE
   private boolean isValidOrderCode(String orderCode) {
      return orderCode != null
            && orderCode.startsWith("ORDER")
            && orderCode.length() == 22;
   }

   public ResponseEntity<MessageResponse> validateOrderCode(String orderCode) {

      if (!isValidOrderCode(orderCode)) {
         return responseUtil.badRequest("Invalid Order Code");
      }

      try {
         // is customer exist
         List<Order> listOrder = orderRepository.findOrderByOrderCode(orderCode);
         if (listOrder.size() <= 0) {
            return responseUtil.notFound("Order is not exist");
         }

         int totalOrders = listOrder.size();
         int totalOrderDeleted = 0;
         for (Order lo : listOrder) {
            if (lo.getIsActive() == false) {
               totalOrderDeleted += 1;
            }
         }

         if (totalOrderDeleted == totalOrders) {
            return responseUtil.badRequest("Order has deleted");
         }

      } catch (Exception e) {
         return responseUtil.notFound("error.server");
      }

      return null; // valid

   }

   public ResponseEntity<MessageResponse> validateOrderId(OrderUpdateRequest request) {

      try {
         // is customer exist

         for (OrderUpdateRequest.Items item : request.getItems()) {

            Optional<Order> order = orderRepository.findById(item.getOrderId());

            // if (order.isEmpty()) {
            // return responseUtil.notFound("Order is not exist");
            // }

            if (order.get().getIsActive() == false) {
               return responseUtil.notFound("order has been deleted");
            }

         }

      } catch (Exception e) {
         return responseUtil.notFound("error.server");
      }

      return null; // valid

   }

   public ResponseEntity<MessageResponse> validateQuantity(int qty, OrderUpdateRequest.Items itemCode) {

      Item itemOpt = itemRepository.findItemByItemCode(itemCode.getItemCode()).get();

      if (qty > itemOpt.getStock()) {
         return responseUtil
               .internalServerError("Stok terbatas, stoc tersisa " + itemOpt.getStock() + " req anda " + qty);
      }
      return null;
   }

}
