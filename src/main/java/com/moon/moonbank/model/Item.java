package com.moon.moonbank.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "items")
public class Item {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   @Column(name = "item_id", updatable = false)
   private UUID itemId;

   @Column(name = "item_name", nullable = false)
   private String itemName;

   @Column(name = "item_code", nullable = false, unique = true)
   private String itemCode;

   @Column(name = "stock", nullable = false)
   private Integer stock;

   @Column(name = "price", nullable = false)
   private Double price;

   @Column(name = "is_available")
   private Boolean isAvailable;

   @Column(name = "last_re_stock")
   private LocalDate lastReStock;
}
