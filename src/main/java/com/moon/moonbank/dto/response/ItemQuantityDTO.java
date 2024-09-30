package com.moon.moonbank.dto.response;

import com.moon.moonbank.model.Item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemQuantityDTO {

   private Item item;
   private int quantity;
   private double price;
}