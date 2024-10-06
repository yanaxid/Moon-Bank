package com.moon.moonbank.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moon.lib.minio.MinioService;
import com.moon.moonbank.dto.request.FilterDTO;
import com.moon.moonbank.dto.request.ItemRequest;
import com.moon.moonbank.dto.response.CustomerResponse.Pic;
import com.moon.moonbank.dto.response.ItemResponse;
import com.moon.moonbank.dto.response.MessageResponse;
import com.moon.moonbank.dto.response.MessageResponse.Meta;
import com.moon.moonbank.model.Item;
import com.moon.moonbank.model_elastic.CustomerElastic;
import com.moon.moonbank.model_elastic.ItemElastic;
import com.moon.moonbank.repository.ItemRepository;
import com.moon.moonbank.repository_elastic.ItemRepositoryElastic;
import com.moon.moonbank.util.ResponseUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemService {

   @Autowired
   private ResponseUtil responseUtil;

   @Autowired
   private ItemRepository itemRepository;

   @Autowired
   private ItemRepositoryElastic itemRepositoryElastic;

   @Autowired
   private MinioService minioService;

   // DISPLAY ITEM LIST
   public ResponseEntity<MessageResponse> getAllItems(FilterDTO filter, Pageable pageable) {

      // validate keyword
      String search = filter.getKeyword();
      if (search == null || search.isEmpty()) {
         search = "";
      }

      try {

      

          Page<ItemElastic> resultPage = null;

         if(filter.getStatus() != null){
            resultPage = itemRepositoryElastic.searchByKeywordAndStatus(search, filter.getStatus(), pageable);

         }else{
            resultPage = itemRepositoryElastic.searchByKeyword(search, pageable);
         }


         

         Meta meta = Meta.builder()
               .total(resultPage.getTotalElements())
               .perPage(pageable.getPageSize())
               .currentPage(pageable.getPageNumber() + 1)
               .lastPage(resultPage.getTotalPages())
               .build();

         List<ItemElastic> modifiedResultPage = resultPage.stream()
               // .filter(result -> result.getIsActive()) // Filter untuk is_active = true
               .map(result -> {


                  if(!result.getPic().equalsIgnoreCase("")){
                     result.setPicUrl(minioService.getPublicLink(result.getPic())); // Set picUrl dengan public link
                  }


                  // result.setPicUrl(minioService.getPublicLink(result.getPic())); // Set picUrl dengan public link
                  return result;
               })
               .collect(Collectors.toList());

         return responseUtil.okWithDataAndMeta("Successfully get data", modifiedResultPage, meta);

      } catch (Exception e) {
         e.printStackTrace();
         return responseUtil.internalServerError("error.server: " + e.getMessage() + " - " + e.getCause());
      }
   }

   // ADD ITEM
   @Transactional
   public ResponseEntity<MessageResponse> addItem(ItemRequest request) {

      // validate request TODO

      // validate when created item code
      if (generateItemCode() == null) {
         return responseUtil.internalServerError("error.server");
      }

      try {

         Item item = Item.builder()
               .itemCode(generateItemCode())
               .itemName(request.getItemName())
               .price(request.getPrice())
               .pic(request.getPic())
               .stock(request.getStock())
               .isAvailable(true)
               .isActive(true)
               .modiefiedDate(LocalDateTime.now())
               .build();

         itemRepository.save(item);

         ItemResponse itemResponse = buildItemResponse(item);

         return responseUtil.okWithData("success.save.data", itemResponse);
      } catch (Exception e) {
         return responseUtil.notFound("error.server");
      }
   }

   // DELETE ITEM
   @Transactional
   public ResponseEntity<MessageResponse> deleteItem(String itemCode) {

      // validate itemCode
      ResponseEntity<MessageResponse> validateItemCodeResult = validateItemCode(itemCode);
      if (validateItemCodeResult != null) {
         return validateItemCodeResult;
      }

      try {

         Optional<Item> itemOpt = itemRepository.findItemByItemCode(itemCode);

         // remove item
         Item item = itemOpt.get();
         item.setIsActive(false);

         return responseUtil.ok("Item deleted successfully");
      } catch (Exception e) {
         return responseUtil.notFound("error.server");
      }
   }

   // UPDATE ITEM
   @Transactional
   public ResponseEntity<MessageResponse> updateItem(String itemCode, ItemRequest request) {

      // validate request TODO

      // validate itemCode
      ResponseEntity<MessageResponse> validateItemCodeResult = validateItemCode(itemCode);
      if (validateItemCodeResult != null) {
         return validateItemCodeResult;
      }

      try {

         Optional<Item> itemOpt = itemRepository.findItemByItemCode(itemCode);

         // create item
         Item item = itemOpt.get();
         item.setItemName(request.getItemName());
         item.setPrice(request.getPrice());
         item.setPic(request.getPic());

         // update last re-stock
         if (request.getStock() > itemOpt.get().getStock()) {
            item.setLastReStock(LocalDateTime.now());
         }

         item.setStock(request.getStock());
         item.setModiefiedDate(LocalDateTime.now());

         ItemResponse itemResponse = buildItemResponse(item);

         return responseUtil.okWithData("success.save.data", itemResponse);
      } catch (Exception e) {
         return responseUtil.notFound("error.server");
      }
   }

   // VIEW ITEM DETAILS
   @Transactional
   public ResponseEntity<MessageResponse> getItem(String itemCode) {

      // validate itemCode
      ResponseEntity<MessageResponse> validateItemCodeResult = validateItemCode(itemCode);
      if (validateItemCodeResult != null) {
         return validateItemCodeResult;
      }

      try {

         Optional<ItemElastic> itemOpt = itemRepositoryElastic.findById(itemCode);

         if(!itemOpt.get().getPic().equalsIgnoreCase("")){
            itemOpt.get().setPicUrl(minioService.getPublicLink(itemOpt.get().getPic()));
         }

         

         return responseUtil.okWithData("success.load.data", itemOpt.get());
      } catch (Exception e) {
         return responseUtil.notFound("error.server");
      }
   }

   // GENERATE ITEM CODE
   // item code = ITE <2d tahun> <2d bulan> <4 digit no urut berdasarkan bulan,
   // jika bulan baru reset ke 1>
   public String generateItemCode() {

      // get current dete
      LocalDate today = LocalDate.now();
      int year = today.getYear() % 100; // get 2 digit, of the last year number
      int month = today.getMonthValue(); // get 2 digit of month

      // cek
      log.info("today " + today);
      log.info("year " + year);
      log.info("month " + month);

      // format yaer and mont to 2 digit
      String formattedYear = String.format("%02d", year);
      String formattedMonth = String.format("%02d", month);

      try {
         // get count/total item for this month
         String currentMonth = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));
         int itemCountThisMonth = itemRepository.countByMonth(currentMonth);

         // add 1 to "nomor urut" for new item
         int newItemNumber = itemCountThisMonth + 1;

         // format number to 4 digit
         String formattedItemNumber = String.format("%04d", newItemNumber);

         // join all for item code
         return "ITEM" + formattedYear + formattedMonth + formattedItemNumber;

      } catch (Exception e) {
         return null;
      }

   }

   // VALIDATE ITEM CODE
   private boolean isValidItemCode(String itemCode) {
      return itemCode != null
            && itemCode.startsWith("ITEM")
            && itemCode.length() == 12;
   }

   private ItemResponse buildItemResponse(Item item) {
      ItemResponse itemResponse = ItemResponse.builder()
            .itemCode(item.getItemCode())
            .itemName(item.getItemName())
            .price(item.getPrice())
            .stock(item.getStock())
            .isAvailable(item.getIsAvailable())
            .lastReStock(item.getLastReStock())
            .build();

      return itemResponse;
   }

   public ResponseEntity<MessageResponse> validateItemCode(String itemCode) {

      if (!isValidItemCode(itemCode)) {
         return responseUtil.badRequest("Invalid Item Code");
      }

      try {
         // is customer exist
         Optional<Item> itemOpt = itemRepository.findItemByItemCode(itemCode);
         if (itemOpt.isEmpty()) {
            return responseUtil.notFound("Item is not exist");
         }
         // is item active
         if (!itemOpt.get().getIsActive()) {
            return responseUtil.badRequest("Item has deleted");
         }

      } catch (Exception e) {
         return responseUtil.notFound("error.server");
      }

      return null; // valid

   }
}
