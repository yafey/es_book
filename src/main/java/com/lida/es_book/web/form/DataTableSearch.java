package com.lida.es_book.web.form;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/*
 *dataTables搜索固定格式
 *Created by LidaDu on 2018/2/28.  
 */
@Data
public class DataTableSearch {
    /**
     * dataTable要求回显字段  dataTable请求数据时会加上该参数
     */
    private int draw;
    /**
     * dataTable分页字段    dataTable请求数据时会加上该参数
     */
    private int start;
    private int length;

    private String name;
    private String author;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String categoryId;//类别
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date minTime;//出版时间
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date maxTime;//出版时间

    /*排序*/
    private String direction;
    private String orderBy;
}
