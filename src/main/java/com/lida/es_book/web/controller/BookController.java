package com.lida.es_book.web.controller;

import com.lida.es_book.base.ApiResponse;
import com.lida.es_book.entity.Book;
import com.lida.es_book.service.book.BookService;
import com.lida.es_book.web.dto.LoginUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/*
 *Created by LidaDu on 2018/2/5.  
 */
@Controller
@RequestMapping(value = "/book")
public class BookController {

    @Resource
    private BookService bookService;

    @GetMapping(value = "/test")
    @ResponseBody
    public ApiResponse test() {
        //test if not login ajax request
        return ApiResponse.ofSuccess(null);
    }

    @GetMapping("/list")
    public String list(Model model, LoginUser loginUser) {
        model.addAttribute("loginUser", loginUser);
        return "/book/list";
    }

    @GetMapping("/add")
    public String toAdd(Model model, LoginUser loginUser) {
        Book book = new Book();
        model.addAttribute("book", book);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("action", "add");
        model.addAttribute("categories", bookService.findAllCategory());
        return "/book/form";
    }

    @GetMapping("/update/{id}")
    public String toUpdate(@PathVariable("id")String id, Model model, LoginUser loginUser) {
        Book book = bookService.finOneBook(id);
        model.addAttribute("book", book);
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("action", "update");
        model.addAttribute("categories", bookService.findAllCategory());
        return "/book/form";
    }

    @PostMapping(value = "/add")
    public String add(@Valid Book book) {
        //TODO
        return "redirect:/book/list";
    }

    @PostMapping(value = "/update")
    public String update(@ModelAttribute("book")Book book) {
        //TODO
        return "redirect:/book/list";
    }



    /**
     * 所有RequestMapping方法调用前的Model准备方法, 实现Struts2 Preparable二次部分绑定的效果,先根据form的id从数据库查出User对象,再把Form提交的内容绑定到该对象上。
     * 因为仅update()方法的form中有id属性，因此仅在update时实际执行.
     *
     * @since 0.1
     */
    @ModelAttribute
    public void getBook(@RequestParam(value = "id", required = false) String id, Model model) {
        if (!StringUtils.isEmpty(id)) {
            model.addAttribute("book", bookService.finOneBook(id));
        }
    }

}
