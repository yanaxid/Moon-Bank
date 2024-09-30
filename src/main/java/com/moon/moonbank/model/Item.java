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
@Table(name = "items")
@EntityListeners(AuditingEntityListener.class)
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

   @Column(name = "pic")
   private String  pic;

   @Column(name = "is_available")
   private Boolean isAvailable;

   @Column(name = "last_re_stock")
   private LocalDateTime lastReStock;

   @Column(name = "created_date", nullable = false)
   @CreatedDate
   private LocalDateTime  createdDate;

   @Column(name = "modified_Date", nullable = false)
   @LastModifiedDate
   private LocalDateTime modiefiedDate;

   @Column(name = "is_active", nullable = false)
   private Boolean isActive;
}
