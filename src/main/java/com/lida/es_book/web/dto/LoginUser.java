package com.lida.es_book.web.dto;

import lombok.Data;

import java.util.Date;

/*
 *Created by LidaDu on 2018/2/6.  
 */
@Data
public class LoginUser {
    private String id;

    private String nickname;//没有判断唯一，不能用于登录

    private String telephone;//用于登陆

    private Date onLineTime;//上线时间

    private Date activeTime;//活动时间
}
