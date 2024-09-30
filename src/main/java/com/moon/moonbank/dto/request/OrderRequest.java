package com.moon.moonbank.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

   private String customerCode;
   private List<Items> items;

   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @Builder
   public static class Items {

      private String itemCode;
      private Integer quantity;
      
   }
}
