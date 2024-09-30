package com.moon.moonbank.dto.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomPageRequest {

   private String sort;
   private Integer size;
   private Integer page;

   // sort default
   public Pageable getPage(String defaultSort) {
      // max page = 10 max size = 100 (for better performance)
      int pageNumberValue = (page != null) ? (page < 1 ? 1 : (page > 10 ? 10 : page)) : 1;
      int pageSizeValue = (size != null) ? (size < 1 ? 1 : (size > 100 ? 100 : size)) : 10;
      Sort sortBy = Sort.unsorted();

      if (sort != null && !sort.isEmpty()) {
         sortBy = singgleSort(sortBy, sort);
      } else {
         sortBy = multipleSort(sortBy, defaultSort);
      }
      return PageRequest.of(pageNumberValue - 1, pageSizeValue, sortBy);
   }

   // multiple sort
   public Sort multipleSort(Sort sortBy, String defaultSort) {
      String[] defaultSorts = defaultSort.split(";");
      for (String defaultSortOrder : defaultSorts) {
         String[] parts = defaultSortOrder.trim().split(",");
         String sortField = parts[0];
         String sortDirection = parts.length > 1 ? parts[1] : "ASC";
         sortBy = sortBy.and(Sort.by(Sort.Direction.fromString(sortDirection), sortField));
      }
      return sortBy;
   }

   // singgle sort
   public Sort singgleSort(Sort sortBy, String sort) {
      String[] parts = sort.split(",");
      String sortField = parts[0];
      String sortOrder = parts.length > 1 ? parts[1] : "ASC";
      sortBy = Sort.by(Sort.Direction.fromString(sortOrder), sortField);

      return sortBy;
   }
}
