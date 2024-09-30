package com.moon.moonbank.repository_elastic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.moon.moonbank.model_elastic.ItemElastic;

public interface ItemRepositoryElastic extends ElasticsearchRepository<ItemElastic, String> {

   @Query("""
         {
             "query_string": {
                 "query": "*?0*",
                 "default_operator": "AND",
                 "default_field": "item_name"
             }
         }
         """)
   Page<ItemElastic> searchByKeyword(String keyword, Pageable pageable);

}
