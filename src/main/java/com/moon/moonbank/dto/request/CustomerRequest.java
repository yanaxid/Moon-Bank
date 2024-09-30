package com.moon.moonbank.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {

   private String customerName;
   private String customerAddress;
   private String customerPhone;
   private String pic;

}
