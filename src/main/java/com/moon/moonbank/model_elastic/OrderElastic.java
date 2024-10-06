package com.moon.moonbank.model_elastic;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "orders_idx")
public class OrderElastic {

   @Id
   @Field(name = "order_code")
   private String orderCode;

   @Field(name = "order_date")
   private LocalDateTime orderDate;

   @Field(type = FieldType.Integer, name = "total_item")
   private Integer totalItem;

   private Customer customer;

   @Field(type = FieldType.Text, name = "is_order_active")
   private Boolean isOrderActive;

   @Field(name = "created_date")
   private LocalDateTime createdDate;

   @Field(name = "modified_date")
   private LocalDateTime modifiedDate;

   List<Item> items;

   @Field(type = FieldType.Double, name = "total_order_price")
   private Double totalOrderPrice;

   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @Builder
   public static class Customer {

      @Field(type = FieldType.Text, name = "customer_id")
      private UUID customerId;

      @Field(type = FieldType.Text, name = "customer_code")
      private String customerCode;

      @Field(type = FieldType.Text, name = "customer_name")
      private String customerName;

      @Field(type = FieldType.Text, name = "customer_phone")
      private String customerPhone;

      @Field(type = FieldType.Text, name = "customer_address")
      private String customerAddress;

      @Field(type = FieldType.Text, name = "pic")
      private String pic;

      @Field(type = FieldType.Text, name = "pic_url")
      private String picUrl;

   }

   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @Builder
   public static class Item {



      @Field(type = FieldType.Text, name = "order_id")
      private UUID orderId;

      @Field(type = FieldType.Text, name = "item_id")
      private UUID itemId;

      @Field(type = FieldType.Text, name = "item_code")
      private String itemCode;

      @Field(type = FieldType.Text, name = "item_name")
      private String itemName;

      @Field(type = FieldType.Double, name = "price")
      private Double price;

      @Field(type = FieldType.Integer, name = "quantity")
      private Integer quantity;

      @Field(type = FieldType.Boolean, name = "is_active")
      private Boolean isActive;

      @Field(type = FieldType.Double, name = "total_item_price")
      private Double totalItemPrice;


      @Field(type = FieldType.Text, name = "pic")
      private String pic;

      @Field(type = FieldType.Text, name = "picUrl")
      private String picUrl;

   }

}
