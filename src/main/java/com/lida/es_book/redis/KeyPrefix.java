package com.lida.es_book.redis;

/*
 *Created by LidaDu on 2018/1/8.  
 */
public interface KeyPrefix {

     int expireSeconds();

     String getPrefix();

}
