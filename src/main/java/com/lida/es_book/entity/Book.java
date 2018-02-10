package com.lida.es_book.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/*
 *Created by LidaDu on 2018/2/6.  
 */
@Entity
@Table
@Data
public class Book extends IdEntity{
    private String name;
    private String author;
    private BigDecimal price;
    private Date publishingTime;//出版时间
    private String categoryId;//类别
    private String categoryName;//类别
    private String keyWord;
}
