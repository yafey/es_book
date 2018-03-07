package com.lida.es_book.service.book;

import com.lida.es_book.base.ESConstants;
import com.lida.es_book.entity.Book;
import com.lida.es_book.entity.Category;
import com.lida.es_book.esSearch.SearchService;
import com.lida.es_book.repository.BookDao;
import com.lida.es_book.repository.CategoryDao;
import com.lida.es_book.web.form.DataTableSearch;
import com.lida.es_book.web.form.PageSearch;
import org.apache.commons.lang3.StringUtils;
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
    @Resource
    private SearchService searchService;

    public List<Category> findAllCategory () {
        return (List<Category>) categoryDao.findAll();
    }

    public Book finOneBook(String id) {
        return bookDao.findOne(id);
    }

    @Transactional
    public void deleteBook(String id) {
        bookDao.delete(id);
        searchService.remove(id);
    }

    public Page<Book> findForPage(PageSearch pageSearch) {
        Sort sort = new Sort(Sort.Direction.fromStringOrNull(pageSearch.getDirection()), pageSearch.getOrderBy());
        Pageable pageable = new PageRequest(pageSearch.getPageNumber() - 1, ESConstants.PAGE_SIZE, sort);
        Specification<Book> specification = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<Predicate>();
            Predicate p ;

            if(pageSearch.getMinPrice()!=null ){
                list.add(cb.ge(root.get("price"),pageSearch.getMinPrice()));
            }
            if(pageSearch.getMaxPrice()!=null ){
                list.add(cb.le(root.get("price"),pageSearch.getMaxPrice()));
            }
            if(StringUtils.isNotEmpty(pageSearch.getCategoryId())){
                list.add(cb.equal(root.get("categoryId"), pageSearch.getCategoryId()));
            }
            if(pageSearch.getMinTime()!=null ){
                list.add(cb.greaterThanOrEqualTo(root.get("publishingTime"),pageSearch.getMinTime()));
            }
            if(pageSearch.getMaxTime()!=null ){
                list.add(cb.lessThanOrEqualTo(root.get("publishingTime"),pageSearch.getMaxTime()));
            }
            Predicate[] ps = new Predicate[list.size()];
            p = cb.and(list.toArray(ps));
            if (StringUtils.isNotEmpty(pageSearch.getKeyWord())) {
                return cb.and(p,
                        cb.or(
                                cb.like(root.get("name"), "%"+pageSearch.getKeyWord()+"%"),cb.like(root.get("author"), "%"+pageSearch.getKeyWord()+"%")
                        )
                );
            }

            return p;
        };
        Page<Book> books = bookDao.findAll(specification, pageable);
        return books;
    }

    public Page<Book> findForDataTable(DataTableSearch searchBody) {
        Sort sort = new Sort(Sort.Direction.fromString(searchBody.getDirection()), searchBody.getOrderBy());
        int page = searchBody.getStart() / searchBody.getLength();
        Pageable pageable = new PageRequest(page, searchBody.getLength(), sort);
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
        searchService.index(book);
        return book;
    }

    @Transactional
    public Book update(Book book) {
        book.setCategoryName(categoryDao.findOne(Integer.valueOf(book.getCategoryId())).getName());
        book = bookDao.save(book);
        searchService.index(book);
        return book;
    }

}
