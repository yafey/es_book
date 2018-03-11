package com.lida.es_book.esSearch;

import lombok.Data;

/**
 * Kafka消息结构体
 */
@Data
public class BookIndexMessage {

    public static final String INDEX = "index";
    public static final String REMOVE = "remove";
    public static final int MAX_RETRY = 3;

    private String bookId;
    private String operation;//操作  index及remove
    private int retry = 0;//kafka消息可以重复消费，异常情况下需再次尝试


    public BookIndexMessage() {
    }

    public BookIndexMessage(String bookId, String operation, int retry) {
        this.bookId = bookId;
        this.operation = operation;
        this.retry = retry;
    }
}
