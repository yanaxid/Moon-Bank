package com.moon.moonbank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moon.moonbank.dto.request.CustomPageRequest;
import com.moon.moonbank.dto.request.FilterDTO;
import com.moon.moonbank.dto.request.ItemRequest;
import com.moon.moonbank.dto.response.MessageResponse;
import com.moon.moonbank.service.ItemService;

@RestController
@RequestMapping("/item")
public class ItemController {

   @Autowired
   private ItemService itemService;

   // DISPLAY ITEM LIST
   // @GetMapping("/all-items")
   // public ResponseEntity<MessageResponse> getAllItems() {
   //    return itemService.getAllItems();
   // }

    @GetMapping("/all-items")
   public ResponseEntity<MessageResponse> getAllCustomers(FilterDTO filter, CustomPageRequest customPageRequest) {
      return itemService.getAllItems(filter, customPageRequest.getPage("item_name.keyword,asc"));
   }

   // ADD ITEM
   @PostMapping("/add-item")
   public ResponseEntity<MessageResponse> postItem(@RequestBody ItemRequest request) {
      return itemService.addItem(request);
   }

   // DELETE ITEM
   @PutMapping("/delete-item/{itemCode}")
   public ResponseEntity<MessageResponse> delItem(@PathVariable String itemCode) {
      return itemService.deleteItem(itemCode);
   }

   // UPDATE ITEM
   @PutMapping("/update-item/{itemCode}")
   public ResponseEntity<MessageResponse> updateItem(@PathVariable String itemCode, @RequestBody ItemRequest request) {
      return itemService.updateItem(itemCode, request);
   }

   // VIEW ITEM DETAILS
   @GetMapping("/detail-item/{itemCode}")
   public ResponseEntity<MessageResponse> getItem(@PathVariable String itemCode) {
      return itemService.getItem(itemCode);
   }
}
