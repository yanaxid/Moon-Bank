package com.moon.moonbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moon.moonbank.dto.request.CustomPageRequest;
import com.moon.moonbank.dto.request.FilterDTO;
import com.moon.moonbank.dto.request.OrderRequest;
import com.moon.moonbank.dto.request.OrderUpdateRequest;
import com.moon.moonbank.dto.response.MessageResponse;
import com.moon.moonbank.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {

   @Autowired
   private OrderService orderService;

   // DISPLAY ORDER LIST
   @GetMapping("/all-orders")
   public ResponseEntity<MessageResponse> getAllOrder(FilterDTO filter, CustomPageRequest customPageRequest) {
      return orderService.getAllOrders(filter, customPageRequest.getPage("order_date,desc"));
   }

   // ADD ORDER
   @PostMapping("/add-order")
   public ResponseEntity<MessageResponse> postOrder(@RequestBody OrderRequest request) {
      return orderService.addOrder(request);
   }

   // DELETE ORDER
   @PutMapping("/delete-order/{orderCode}")
   public ResponseEntity<MessageResponse> delCustomer(@PathVariable String orderCode) {
      return orderService.deleteOrder(orderCode);
   }

   // UPDATE ORDER
   @PutMapping("/update-order/{orderCode}")
   public ResponseEntity<MessageResponse> updateOrder(@PathVariable String orderCode, @RequestBody OrderUpdateRequest orderRequest) {
      return orderService.updateOrder(orderCode, orderRequest);
   }

   // VIEW ORDER DETAILS
   @GetMapping("/detail-order/{orderCode}")
   public ResponseEntity<MessageResponse> getCustomer(@PathVariable String orderCode) {
      return orderService.getOrder(orderCode);
   }

   // DOWNLOAD THE EVAILABLE ORDER DATA LIST REPORT
}
