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
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "customer_idx")
public class CustomerElastic {

   @Id
   private String customerCode;

   @Field(type = FieldType.Text, name = "customer_id")
   private UUID customerId;

   @Field(type = FieldType.Text, name = "customer_name")
   private String customerName;

   @Field(type = FieldType.Text, name = "customer_address")
   private String customerAddress;

   @Field(type = FieldType.Text, name = "customer_phone")
   private String customerPhone;

   @Field(type = FieldType.Text, name = "pic_url")
   private String picUrl;

   @Field(name = "last_order_date")
   private LocalDateTime lastOrderDate;

   @Field(type = FieldType.Text, name = "pic")
   private String pic;

   @Field(type = FieldType.Boolean, name = "is_active")
   private Boolean isActive;

   @Field(name = "created_date")
   private LocalDateTime createdDate;

   @Field(name = "modified_date")
   private LocalDateTime modiefiedDate;

}
