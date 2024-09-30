package com.moon.moonbank.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
   
   private String itemName;
   private Integer stock;
   private Double price;
   private String pic;

}
