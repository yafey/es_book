package com.lida.es_book.esSearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lida.es_book.entity.Book;
import com.lida.es_book.repository.BookDao;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.index.reindex.DeleteByQueryRequestBuilder;
import org.elasticsearch.rest.RestStatus;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

@Service
@Slf4j
public class SearchService {

    private static final String INDEX_NAME = "esbook";
    private static final String INDEX_TYPE = "book";
    private static final String INDEX_QUEUE = "booktopic";//rabbitmq队列不能自动创建，需手动创建

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TransportClient esClient;
    @Resource
    private BookDao bookDao;
    @Resource
    private ObjectMapper objectMapper;

    @RabbitListener(queues = INDEX_QUEUE)//异步监听
    public void handleMessage(String content) throws InterruptedException {
        //Thread.sleep(10000l);   //页面并非sleep后才完成请求，异步监听
        System.out.println("TreadName : " + Thread.currentThread().getName());
        log.info("Received message : " + content);
        try {
            BookIndexMessage message = objectMapper.readValue(content, BookIndexMessage.class);
            switch (message.getOperation()) {
                case BookIndexMessage.INDEX:
                    this.createOrUpdateIndex(message);
                    break;
                case BookIndexMessage.REMOVE:
                    this.removeIndex(message);
                    break;
                default:
                    log.warn("Not support message : " + message);
                    break;

            }
        } catch (IOException e) {
            log.error("Cannot parse json for : " + content, e);
        }
    }

    private void createOrUpdateIndex(BookIndexMessage message) {
        String bookId = message.getBookId();
        Book book = bookDao.findOne(bookId);
        if (book == null ) {
            log.error("Index book {} dose not exist!", bookId);
        }
        BookIndexTemplate bookIndexTemplate = new BookIndexTemplate();
        modelMapper.map(book, bookIndexTemplate);

        SearchRequestBuilder builder = this.esClient.prepareSearch(INDEX_NAME).setTypes(INDEX_TYPE)
                .setQuery(QueryBuilders.termQuery(BookIndexKey.BOOK_ID, book.getId()));
        log.info(builder.toString());
        SearchResponse response =builder.get();
        boolean success;
        long totalHit = response.getHits().getTotalHits();
        if (totalHit == 0) {
            //create
            success = create(bookIndexTemplate);
        } else if (totalHit == 1) {
            //update
            String esId = response.getHits().getAt(0).getId();
            success = update(esId, bookIndexTemplate);
        } else {
            success = deleteAndCreate(totalHit, bookIndexTemplate);
        }

        if (!success) {
            this.index(message.getBookId(), message.getRetry() + 1);
            log.info("Index success with book" + book.getId());
        }

    }

    public void removeIndex(BookIndexMessage message) {
        String bookId = message.getBookId();
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(BookIndexKey.BOOK_ID,bookId))
                .source(INDEX_NAME);
        log.info("Delete by query for book: " + builder);
        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        if (deleted <= 0) {
            log.warn("Did not remove data from es for response : " + response);
            this.remove(bookId, message.getRetry() + 1);
        }
    }

    @Async
    public void index(String bookId) {
        this.index(bookId, 0);
    }

    public void index(String bookId, int retry) {
        if (retry > BookIndexMessage.MAX_RETRY) {
            log.error("Retry index times over 3 for book : " + bookId + " Please check it!");
            return;
        }
        BookIndexMessage message = new BookIndexMessage(bookId, BookIndexMessage.INDEX, retry);
        try {
            System.out.println("TreadName : " + Thread.currentThread().getName());
            rabbitTemplate.convertAndSend(INDEX_QUEUE, objectMapper.writeValueAsString(message));
        }catch (Exception e) {
            e.printStackTrace();
            log.error("Json encode error for : " + message);
        }

    }

    private boolean create(BookIndexTemplate bookIndexTemplate) {
        try {
            IndexResponse response = this.esClient.prepareIndex(INDEX_NAME, INDEX_TYPE)
                    .setSource(objectMapper.writeValueAsBytes(bookIndexTemplate), XContentType.JSON).get();
            log.info("Create index with book: " + bookIndexTemplate.getBookId());
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
            log.info("Update index with book: " + bookIndexTemplate.getBookId());
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
    private boolean deleteAndCreate(long totalHit, BookIndexTemplate bookIndexTemplate) {
        DeleteByQueryRequestBuilder builder = DeleteByQueryAction.INSTANCE
                .newRequestBuilder(esClient)
                .filter(QueryBuilders.termQuery(BookIndexKey.BOOK_ID,bookIndexTemplate.getBookId()))
                .source(INDEX_NAME);
        log.info("Delete by query for book: " + builder);
        BulkByScrollResponse response = builder.get();
        long deleted = response.getDeleted();
        if(deleted != totalHit) {
            log.warn("Need delete{},but {} was deleted", totalHit, deleted);
            return false;
        } else {
            return create(bookIndexTemplate);
        }
    }

    public void remove(String bookId, int retry) {
        if (retry > BookIndexMessage.MAX_RETRY) {
            log.error("Retry remove times over 3 for book : " + bookId + " Please check it");
            return;
        }
        BookIndexMessage message = new BookIndexMessage(bookId, BookIndexMessage.REMOVE, retry);
        try {
            this.rabbitTemplate.convertAndSend(INDEX_QUEUE, objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.error("Cannot encode json for " + message, e);
        }
    }

    @Async
    public void remove(String bookId) {
        this.remove(bookId, 0);
    }

}
