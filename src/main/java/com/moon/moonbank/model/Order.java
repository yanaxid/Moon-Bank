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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order {

   @Id
   @GeneratedValue(strategy = GenerationType.UUID)
   @Column(name = "order_id", updatable = false)
   private UUID orderId;

   @Column(name = "order_code", nullable = false)
   private String orderCode;

   @Column(name = "order_date", nullable = false)
   private LocalDateTime orderDate;

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

   @Column(name = "created_date", nullable = false)
   @CreatedDate
   private LocalDateTime createdDate;

   @Column(name = "modified_Date", nullable = false)
   @LastModifiedDate
   private LocalDateTime modiefiedDate;

   @Column(name = "is_active", nullable = false)
   private Boolean isActive;

}
