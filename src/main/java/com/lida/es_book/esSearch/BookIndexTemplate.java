package com.lida.es_book.esSearch;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 索引结构
 */
@Data
public class BookIndexTemplate {
    private String bookId;
    private String name;
    private String author;
    private BigDecimal price;
    private Date publishingTime;//出版时间
    private String categoryId;//类别
}
