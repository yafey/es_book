package com.lida.es_book.esSearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lida.es_book.entity.Book;
import com.lida.es_book.repository.BookDao;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class SearchService {

    private static final String INDEX_NAME = "esBook";
    private static final String INDEX_TYPE = "book";
    private static final String INDEX_TOPIC = "book_build";


    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TransportClient esClient;
    @Resource
    private BookDao bookDao;
    @Resource
    private ObjectMapper objectMapper;

    public void index(String bookId) {
        Book book = bookDao.findOne(bookId);
        if (book == null) {
            log.error("Index book {} dose not exist!", bookId);
            return;
        }
        BookIndexTemplate bookIndexTemplate = new BookIndexTemplate();
        modelMapper.map(book, bookIndexTemplate);
        //create
        //update
        //deleteAndCreate比如数据过多
    }

    private boolean create(BookIndexTemplate bookIndexTemplate) {
        try {
            IndexResponse response = this.esClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
                    .setSource(objectMapper.writeValueAsBytes(bookIndexTemplate), XContentType.JSON).get();
            log.debug("Create index with book: " + bookIndexTemplate.getBookId());
            if (response.status() == RestStatus.CREATED) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            log.error("Error to index book " + bookIndexTemplate.getBookId(), e);
            return false;
        }
    }

    private boolean update(String esId, BookIndexTemplate bookIndexTemplate) {
        try {
            UpdateResponse response = this.esClient.prepareUpdate(INDEX_NAME, INDEX_TYPE, esId)
                    .setDoc(objectMapper.writeValueAsBytes(bookIndexTemplate), XContentType.JSON).get();
            log.debug("Create index with book: " + bookIndexTemplate.getBookId());
            if (response.status() == RestStatus.OK) {
                return true;
            } else {
                return false;
            }
        } catch (JsonProcessingException e) {
            log.error("Error to index book " + bookIndexTemplate.getBookId(), e);
            return false;
        }
    }

    /**
     *
     * @param totalHit 重复数
     * @param bookIndexTemplate
     * @return
     */
    private boolean deleteAndCreate(int totalHit, BookIndexTemplate bookIndexTemplate) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(BookIndexKey.BOOK_ID,bookIndexTemplate.getBookId()))
                .source(INDEX_NAME);
        log.debug("Delete by query for book: " + builder);
        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        if(deleted != totalHit) {
            log.warn("Need delete{},but {} was deleted", totalHit, deleted);
            return false;
        } else {
            return create(bookIndexTemplate);
        }
    }
}
