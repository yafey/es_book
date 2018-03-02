package com.lida.es_book.web.form;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/*
 *Created by LidaDu on 2018/3/1.  
 */
@Data
public class PageSearch {

    private String keyWord;

    private int pageNumber = 1;

    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String categoryId;//类别
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    private Date minTime;//出版时间
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
    private Date maxTime;//出版时间

    /*排序*/
    private String direction;
    private String orderBy;
}
