package com.moon.moonbank.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.moon.moonbank.model.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {

   @Query("SELECT COUNT(i) FROM Item i WHERE DATE_FORMAT(i.createdDate, '%Y-%m') = :currentMonth")
   int countByMonth(String currentMonth);

   @Query("SELECT i FROM Item i WHERE i.itemCode = :itemCode")
   Optional<Item> findItemByItemCode(String itemCode);

}
