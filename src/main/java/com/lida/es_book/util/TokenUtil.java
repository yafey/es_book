package com.lida.es_book.util;

import java.util.UUID;

/*
 *Created by LidaDu on 2018/1/8.  
 */
public class TokenUtil {
    
    public static String getToken() {
        return UUID.randomUUID().toString();
    }

}
