package com.lida.es_book.web.form;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/*
 *Created by LidaDu on 2018/2/27.  
 */
@Data
public class SearchForm {

    private String name;
    private String author;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String categoryId;//类别
    private String keyWord;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date mixTime;//出版时间
    private Date maxTime;//出版时间

}
