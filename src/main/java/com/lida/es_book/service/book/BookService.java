package com.lida.es_book.service.book;

import com.lida.es_book.base.ESConstants;
import com.lida.es_book.entity.Book;
import com.lida.es_book.entity.Category;
import com.lida.es_book.repository.BookDao;
import com.lida.es_book.repository.CategoryDao;
import com.lida.es_book.web.form.DataTableSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
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
        Sort sort = new Sort(Sort.Direction.ASC, "price");
        Pageable pageable = new PageRequest(0, ESConstants.PAGE_SIZE, sort);
        return (Page<Book>) bookDao.findAll(pageable);
    }

    public Page<Book> findForDataTable(DataTableSearch searchBody) {
        Sort sort = new Sort(Sort.Direction.fromString(searchBody.getDirection()), searchBody.getOrderBy());
        int page = searchBody.getStart() / searchBody.getLength();
        Pageable pageable = new PageRequest(page, searchBody.getLength(), sort);
        /*Specification<Book> specification = new Specification<Book>() {
            @Override
            public Predicate toPredicate(Root<Book> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return null;
            }
        };*/
        Specification<Book> specification = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<Predicate>();

            if(searchBody.getName()!=null ){
                list.add(cb.like(root.get("name"), "%"+searchBody.getName()+"%"));
            }
            if(searchBody.getAuthor()!=null ){
                list.add(cb.like(root.get("author"), "%"+searchBody.getAuthor()+"%"));
            }
            if(searchBody.getMinPrice()!=null ){
                list.add(cb.ge(root.get("price"),searchBody.getMinPrice()));
            }
            if(searchBody.getMaxPrice()!=null ){
                list.add(cb.le(root.get("price"),searchBody.getMaxPrice()));
            }
            if(searchBody.getCategoryId()!=null ){
                list.add(cb.equal(root.get("categoryId"), searchBody.getCategoryId()));
            }
            if(searchBody.getKeyWord()!=null ){
                list.add(cb.like(root.get("keyWord"), "%"+searchBody.getKeyWord()+"%"));
            }
            if(searchBody.getMinTime()!=null ){
                list.add(cb.greaterThanOrEqualTo(root.get("publishingTime"),searchBody.getMinTime()));
            }
            if(searchBody.getMaxTime()!=null ){
                list.add(cb.lessThanOrEqualTo(root.get("publishingTime"),searchBody.getMaxTime()));
            }
            Predicate[] p = new Predicate[list.size()];
            return cb.and(list.toArray(p));
        };
        Page<Book> books = bookDao.findAll(specification, pageable);
        return books;
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
