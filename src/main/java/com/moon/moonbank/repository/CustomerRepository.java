package com.moon.moonbank.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.moon.moonbank.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

   @Query("SELECT COUNT(c) FROM Customer c WHERE DATE_FORMAT(c.createdDate, '%Y-%m') = :currentMonth")
   int countByMonth(String currentMonth);

   @Query("SELECT c FROM Customer c WHERE c.customerCode = :customerCode")
   Optional<Customer> findCustomerByCustomerCode(String customerCode);

}
