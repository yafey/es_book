package com.lida.es_book.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 *Created by LidaDu on 2018/2/7.  
 */
@ResponseStatus(value= HttpStatus.UNAUTHORIZED,reason="Please Login")//返回401状态吗 表示未登录 再AppErrorController中返回/login
public class ESAuthorizeException extends RuntimeException {


}
