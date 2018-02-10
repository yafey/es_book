package com.lida.es_book.service.book;

import com.lida.es_book.entity.Book;
import com.lida.es_book.entity.Category;
import com.lida.es_book.repository.BookDao;
import com.lida.es_book.repository.CategoryDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/*
 *Created by LidaDu on 2018/2/6.  
 */
@Service
public class BookService {

    @Resource
    private CategoryDao categoryDao;
    @Resource
    private BookDao bookDao;

    public List<Category> findAllCategory () {
        return (List<Category>) categoryDao.findAll();
    }

    public Book finOneBook(String id) {
        return bookDao.findOne(id);
    }

}
