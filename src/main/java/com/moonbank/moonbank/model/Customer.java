package com.moonbank.moonbank.model;

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
@Table(name = "customers")
public class Customer {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   @Column(name = "customer_id", updatable = false)
   private UUID customerId;

   @Column(name = "customer_name", nullable = false)
   private String customerName;

   @Column(name = "customer_address")
   private String customerAddress;

   @Column(name = "customer_code", nullable = false, unique = true)
   private String customerCode;

   @Column(name = "customer_phone", nullable = false)
   private String customerPhone;

   @Column(name = "is_active")
   private Boolean isActive;

   @Column(name = "last_order_date")
   private LocalDate lastOrderDate;

   @Column(name = "pic")
   private String pic;
}
