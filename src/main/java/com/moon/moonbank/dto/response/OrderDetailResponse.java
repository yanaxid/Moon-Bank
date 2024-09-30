package com.moon.moonbank.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponse {

   private String orderCode;

   private LocalDateTime orderDate;

   private Integer totalItem;

   private Customer customer;

   private Boolean isOrderActive;

   private LocalDateTime createdDate;

   private LocalDateTime modifiedDate;

   List<Item> items;

   private Double totalOrderPrice;

   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @Builder
   public static class Customer {

      private UUID customerId;

      private String customerCode;

      private String customerName;

      private String customerPhone;

      private String customerAddress;

      private String pic;

      private String picUrl;

   }

   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @Builder
   public static class Item {

      private UUID itemId;

      private String itemCode;

      private String itemName;

      private Double price;

      private String pic;

      private String picUrl;

      private Integer quantity;

      private Boolean isActive;

      private Double totalItemPrice;

   }

}
