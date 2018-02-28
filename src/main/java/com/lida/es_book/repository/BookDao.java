package com.lida.es_book.repository;

import com.lida.es_book.entity.Book;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/*
 *Created by LidaDu on 2018/2/6.  
 */
public interface BookDao extends PagingAndSortingRepository<Book, String>, JpaSpecificationExecutor<Book> {
}
