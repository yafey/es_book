package com.lida.es_book.service.book;

import com.lida.es_book.base.ApiDataTableResponse;
import com.lida.es_book.base.ApiResponse;
import com.lida.es_book.base.ESConstants;
import com.lida.es_book.entity.Book;
import com.lida.es_book.entity.Category;
import com.lida.es_book.esSearch.BookIndexKey;
import com.lida.es_book.esSearch.SearchService;
import com.lida.es_book.repository.BookDao;
import com.lida.es_book.repository.CategoryDao;
import com.lida.es_book.web.form.DataTableSearch;
import com.lida.es_book.web.form.PageSearch;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/*
 *Created by LidaDu on 2018/2/6.  
 */
@Service
@Slf4j
public class BookService {

    private static final String INDEX_NAME = "esbook";
    private static final String INDEX_TYPE = "book";

    @Resource
    private CategoryDao categoryDao;
    @Resource
    private BookDao bookDao;
    @Resource
    private SearchService searchService;
    @Resource
    private TransportClient esClient;

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

    public ApiDataTableResponse findForDataTable(DataTableSearch searchBody) {
        Sort sort = new Sort(Sort.Direction.fromString(searchBody.getDirection()), searchBody.getOrderBy());
        int page = searchBody.getStart() / searchBody.getLength();
        Pageable pageable = new PageRequest(page, searchBody.getLength(), sort);
        Specification<Book> specification = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<Predicate>();
            Predicate p ;
            if(searchBody.getCategoryId()!=null ){
                list.add(cb.equal(root.get("categoryId"), searchBody.getCategoryId()));
            }

            if(searchBody.getMinPrice()!=null ){
                list.add(cb.ge(root.get("price"),searchBody.getMinPrice()));
            }
            if(searchBody.getMaxPrice()!=null ){
                list.add(cb.le(root.get("price"),searchBody.getMaxPrice()));
            }

            if(searchBody.getMinTime()!=null ){
                list.add(cb.greaterThanOrEqualTo(root.get("publishingTime"),searchBody.getMinTime()));
            }
            if(searchBody.getMaxTime()!=null ){
                list.add(cb.lessThanOrEqualTo(root.get("publishingTime"),searchBody.getMaxTime()));
            }
            Predicate[] ps = new Predicate[list.size()];
            p = cb.and(list.toArray(ps));

            if (StringUtils.isNotEmpty(searchBody.getKeyWord())) {
                return cb.and(p,
                        cb.or(
                                cb.like(root.get("name"), "%"+searchBody.getKeyWord()+"%"),cb.like(root.get("author"), "%"+searchBody.getKeyWord()+"%")
                        )
                );
            }
            return p;
        };
        Page<Book> books = bookDao.findAll(specification, pageable);
        return wrapperForPage(books);
    }

    private ApiDataTableResponse wrapperForPage(Page<Book> bookPage) {
        ApiDataTableResponse response = new ApiDataTableResponse(ApiResponse.Status.SUCCESS);
        response.setData(bookPage.getContent());
        response.setRecordsFiltered(bookPage.getTotalElements());
        response.setRecordsTotal(bookPage.getTotalElements());
        return response;
    }

    public ApiDataTableResponse findFromElastic(DataTableSearch searchBody) {
        //match query搜索的时候，首先会解析查询字符串，进行分词，然后查询，
        //而term query,输入的查询内容是什么，就会按照什么去查询，并不会解析查询内容，对它分词。
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if(searchBody.getCategoryId()!=null && !("").equals(searchBody.getCategoryId().trim())){
            boolQuery.filter(
                    QueryBuilders.termQuery(BookIndexKey.CATEGORY_ID, searchBody.getCategoryId())
            );
        }
        RangeQueryBuilder rangePrice = QueryBuilders.rangeQuery(BookIndexKey.PRICE);
        if(searchBody.getMinPrice()!=null ){
            rangePrice.gte(searchBody.getMinPrice().doubleValue());
            boolQuery.filter(rangePrice);
        }
        if(searchBody.getMaxPrice()!=null ){
            rangePrice.lte(searchBody.getMaxPrice().doubleValue());
            boolQuery.filter(rangePrice);
        }
        RangeQueryBuilder rangeTime = QueryBuilders.rangeQuery(BookIndexKey.PUBLISHING_TIME);
        if(searchBody.getMinTime()!=null ){
            rangeTime.gte(searchBody.getMinTime().getTime());
            boolQuery.filter(rangeTime);
        }
        if(searchBody.getMaxTime()!=null ){
            rangeTime.lte(searchBody.getMaxTime().getTime());
            boolQuery.filter(rangeTime);
        }

        if (searchBody.getKeyWord() != null&& !("").equals(searchBody.getKeyWord().trim())) {
            boolQuery.must(
                    QueryBuilders.multiMatchQuery(searchBody.getKeyWord(),
                            BookIndexKey.AUTHOR,
                            BookIndexKey.NAME
                            )
            );
        }
        SearchRequestBuilder requestBuilder = this.esClient.prepareSearch(INDEX_NAME)
                .setTypes(INDEX_TYPE)
                .setQuery(boolQuery)
                .addSort(searchBody.getOrderBy(), SortOrder.fromString(searchBody.getDirection()))
                .setFrom(searchBody.getStart())
                .setSize(searchBody.getLength())
                .setFetchSource(BookIndexKey.BOOK_ID, null);
        log.info(requestBuilder.toString());

        SearchResponse response = requestBuilder.get();
        if (response.status() != RestStatus.OK) {
            log.error("Search status is not ok for " + requestBuilder);
            ApiDataTableResponse apiDataTableResponse = new ApiDataTableResponse(ApiResponse.Status.SUCCESS);
            apiDataTableResponse.setData(null);
            apiDataTableResponse.setRecordsFiltered(0);
            apiDataTableResponse.setRecordsTotal(0);
            return apiDataTableResponse;
        }
        return wrapperForEs(response);

    }

    private ApiDataTableResponse wrapperForEs(SearchResponse searchResponse) {
        List<String> bookIds = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits()){
            log.info(hit.getSource().toString());
            bookIds.add(String.valueOf(hit.getSource().get(BookIndexKey.BOOK_ID)));
        }
        //TODO findAll应该矫正顺序
        List<Book> books = (List<Book>) bookDao.findAll(bookIds);
        ApiDataTableResponse apiDataTableResponse = new ApiDataTableResponse(ApiResponse.Status.SUCCESS);
        apiDataTableResponse.setData(books);
        apiDataTableResponse.setRecordsFiltered(searchResponse.getHits().totalHits);
        apiDataTableResponse.setRecordsTotal(searchResponse.getHits().totalHits);
        return apiDataTableResponse;
    }

    @Transactional
    public Book add(Book book) {
        book.setCategoryName(categoryDao.findOne(Integer.valueOf(book.getCategoryId())).getName());
        book = bookDao.save(book);
        searchService.index(book.getId());
        return book;
    }

    @Transactional
    public Book update(Book book) {
        book.setCategoryName(categoryDao.findOne(Integer.valueOf(book.getCategoryId())).getName());
        book = bookDao.save(book);
        searchService.index(book.getId());
        return book;
    }
}
