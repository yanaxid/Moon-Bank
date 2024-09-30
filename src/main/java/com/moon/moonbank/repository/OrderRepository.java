package com.moon.moonbank.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.moon.moonbank.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

   @Query("SELECT COUNT(o) FROM Order o WHERE DATE_FORMAT(o.createdDate, '%Y-%m') = :currentMonth")
   int countByMonth(String currentMonth);

   @Query("SELECT o FROM Order o WHERE o.orderCode = :orderCode")
   List<Order> findOrderByOrderCode(String orderCode);

   // @Query(value = """
   //       SELECT
   //           o.order_code AS orderCode,
   //           JSON_OBJECT(
   //               'customer_id', BIN_TO_UUID(c.customer_id),
   //               'customer_code', c.customer_code,
   //               'customer_name', c.customer_name,
   //               'customer_address', c.customer_address,
   //               'customer_phone', c.customer_phone,
   //               'pic', c.pic
   //           ) AS customer,
   //           JSON_ARRAYAGG(
   //               JSON_OBJECT(
   //                   'item_id', BIN_TO_UUID(i.item_id),
   //                   'item_code', i.item_code,
   //                   'item_name', i.item_name,
   //                   'quantity', o.quantity,
   //                   'price', i.price
   //               )
   //           ) AS items,
   //           COUNT(o.item_id) AS totalItem,
   //           SUM(o.total_price) AS totalOrderPrice,
   //           o.order_date AS orderDate,
   //           o.is_active AS isActive,
   //           o.created_date AS createdDate,
   //           o.modified_date AS modifiedDate
   //       FROM
   //           orders o
   //       JOIN
   //           customers c ON o.customer_id = c.customer_id
   //       JOIN
   //           items i ON o.item_id = i.item_id
   //       WHERE
   //           o.is_active = TRUE
   //           AND o.order_code = :orderCode
   //       GROUP BY
   //           o.order_code
   //       """, nativeQuery = true)
   // OrderDetailsDTO getOrderDetailsByCode(@Param("orderCode") String orderCode);

   

  
  

}
