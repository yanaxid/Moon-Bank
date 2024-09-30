package com.moon.moonbank.repository_elastic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.moon.moonbank.model_elastic.CustomerElastic;

public interface CustomerRepositoryElastic extends ElasticsearchRepository<CustomerElastic, String> {

   @Query("""
         {
             "query_string": {
                 "query": "*?0*",
                 "default_operator": "AND",
                 "default_field": "customer_name"
             }
         }
         """)
   Page<CustomerElastic> searchByKeyword(String keyword, Pageable pageable);

}
