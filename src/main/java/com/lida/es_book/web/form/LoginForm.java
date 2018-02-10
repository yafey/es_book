package com.lida.es_book.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

/*
 *Created by LidaDu on 2018/2/6.  
 */
@Data
public class LoginForm {

    @NotBlank(message = "登录名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
