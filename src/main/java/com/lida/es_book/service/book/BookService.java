package com.lida.es_book.service.book;

import com.lida.es_book.base.ESConstants;
import com.lida.es_book.entity.Book;
import com.lida.es_book.entity.Category;
import com.lida.es_book.repository.BookDao;
import com.lida.es_book.repository.CategoryDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public void deleteBook(String id) {
        bookDao.delete(id);
    }

    public Page<Book> findBooks() {
        //TODO 从es中查询
        Sort sort = new Sort(Sort.Direction.ASC, "price");
        Pageable pageable = new PageRequest(0, ESConstants.PAGE_SIZE, sort);
        return (Page<Book>) bookDao.findAll(pageable);
    }

    @Transactional
    public Book add(Book book) {
        book.setCategoryName(categoryDao.findOne(Integer.valueOf(book.getCategoryId())).getName());
        book = bookDao.save(book);
        return book;
    }

    @Transactional
    public Book update(Book book) {
        book.setCategoryName(categoryDao.findOne(Integer.valueOf(book.getCategoryId())).getName());
        book = bookDao.save(book);
        return book;
    }

}
