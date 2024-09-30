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
@Document(indexName = "item_idx")
public class ItemElastic {

   @Id
   private String itemCode;

   @Field(type = FieldType.Text, name = "item_id")
   private UUID itemId;

   @Field(type = FieldType.Text, name = "item_name")
   private String itemName;

   @Field(type = FieldType.Integer, name = "stock")
   private Integer stock;

   @Field(type = FieldType.Double, name = "price")
   private Double price;

   @Field(type = FieldType.Text, name = "pic")
   private String pic;

   @Field(type = FieldType.Text, name = "pic_url")
   private String picUrl;

   @Field(type = FieldType.Boolean, name = "is_available")
   private Boolean isAvailable;

   @Field(name = "last_re_stock")
   private LocalDateTime lastReStock;

   @Field(type = FieldType.Boolean, name = "is_active")
   private Boolean isActive;

   @Field(name = "created_date")
   private LocalDateTime createdDate;

   @Field(name = "modified_date")
   private LocalDateTime modiefiedDate;

}
