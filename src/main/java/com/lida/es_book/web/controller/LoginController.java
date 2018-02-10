package com.lida.es_book.web.controller;

import com.lida.es_book.base.ApiResponse;
import com.lida.es_book.service.user.UserService;
import com.lida.es_book.web.form.LoginForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/*
 *Created by LidaDu on 2018/2/6.  
 */
@Controller
@Slf4j
public class LoginController {

    @Resource
    private UserService userService;

    @RequestMapping(value="/login",method= RequestMethod.POST)
    @ResponseBody
    public ApiResponse login(@Valid LoginForm loginForm, BindingResult bindingResult, HttpServletResponse response){
        if (bindingResult.hasErrors()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(),null);
        }
        return userService.login(loginForm, response);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        userService.logout(request,response);
        return "redirect:/";
    }

}
