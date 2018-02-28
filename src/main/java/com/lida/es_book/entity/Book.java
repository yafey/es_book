package com.lida.es_book.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/*
 *Created by LidaDu on 2018/2/6.  
 */
@Entity
@Table
@Data
public class Book extends IdEntity{

    /**
     * @NotEmpty 用在集合类上面
     * @NotBlank 用在String上面
     * @NotNull    用在基本类型上
     */

    @NotBlank
    private String name;
    @NotBlank
    private String author;
    @NotNull
    private BigDecimal price;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")//不加时区会默认标准时区GMT所以会慢8小时
    private Date publishingTime;//出版时间
    @NotBlank
    private String categoryId;//类别
    private String categoryName;//类别

}
