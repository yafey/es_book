package com.lida.es_book.repository;

import com.lida.es_book.entity.Book;
import org.springframework.data.repository.CrudRepository;

/*
 *Created by LidaDu on 2018/2/6.  
 */
public interface BookDao extends CrudRepository<Book, String>{
}
