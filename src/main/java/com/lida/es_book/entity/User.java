package com.lida.es_book.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/*
 *Created by LidaDu on 2018/2/6.  
 */
@Entity
@Table
@Data
public class User extends IdEntity{
    private String nickname;
    private String telephone;
    private String password;
    private String salt;
}
