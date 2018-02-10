package com.lida.es_book.entity;

import lombok.Data;

import javax.persistence.*;

/*
 *Created by LidaDu on 2018/2/6.  
 */
@Entity
@Table
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
}
