package com.moonbank.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "orders")
public class Order {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   @Column(name = "order_id", updatable = false)
   private UUID orderId;

   @Column(name = "order_code", nullable = false, unique = true)
   private String orderCode;

   @Column(name = "order_date", nullable = false)
   private LocalDate orderDate;

   @Column(name = "total_price")
   private Double totalPrice;

   @ManyToOne
   @JoinColumn(name = "customer_id", nullable = false)
   private Customer customer;

   @ManyToOne
   @JoinColumn(name = "item_id", nullable = false)
   private Item item;

   @Column(name = "quantity", nullable = false)
   private Integer quantity;

}
