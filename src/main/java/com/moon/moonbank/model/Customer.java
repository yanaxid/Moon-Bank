package com.moon.moonbank.model;

import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "customers")
@EntityListeners(AuditingEntityListener.class)
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

   @Column(name = "is_active", nullable = false)
   private Boolean isActive;

   @Column(name = "last_order_date")
   private LocalDateTime lastOrderDate;

   @Column(name = "pic")
   private String pic;

   @Column(name = "created_date", nullable = false)
   @CreatedDate
   private LocalDateTime createdDate;

   @Column(name = "modified_date", nullable = false)
   @LastModifiedDate
   private LocalDateTime modiefiedDate;
}
