package com.moon.moonbank.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemResponse {


   private String itemCode;
   private String itemName;
   private Integer stock;
   private Double price;
   private Boolean isAvailable;
   private LocalDateTime lastReStock;
}
