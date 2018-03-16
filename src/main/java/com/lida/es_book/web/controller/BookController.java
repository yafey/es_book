package com.lida.es_book.web.controller;

import com.lida.es_book.base.ApiDataTableResponse;
import com.lida.es_book.base.ApiResponse;
import com.lida.es_book.entity.Book;
import com.lida.es_book.service.book.BookService;
import com.lida.es_book.service.book.RandomBookService;
import com.lida.es_book.web.dto.LoginUser;
import com.lida.es_book.web.form.DataTableSearch;
import com.lida.es_book.web.form.PageSearch;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/*
 *Created by LidaDu on 2018/2/5.  
 */
@Controller
@RequestMapping(value = "/book")
public class BookController {

    @Resource
    private BookService bookService;
    @Resource
    private RandomBookService randomBookService;

    @GetMapping(value = "/test")
    @ResponseBody
    public ApiResponse test() {
        //test if not login ajax request
        return ApiResponse.ofSuccess(null);
    }

    @RequestMapping("/list1")
    public String list(Model model, LoginUser loginUser, @ModelAttribute PageSearch searchBody) {
        Long now = System.currentTimeMillis();
        if (searchBody.getOrderBy() == null) {
            searchBody.setOrderBy("price");
        }
        model.addAttribute("loginUser", loginUser);
        Page<Book> books = bookService.findForPage(searchBody);
        model.addAttribute("books", books);
        model.addAttribute("searchBody", searchBody);
        model.addAttribute("categories", bookService.findAllCategory());
        System.out.println(System.currentTimeMillis() - now + "-------------------------------");
        return "/book/list1";
    }

    @GetMapping("/list")
    public String toList(Model model, LoginUser loginUser) {
        model.addAttribute("loginUser", loginUser);
        model.addAttribute("categories", bookService.findAllCategory());
        return "/book/list";
    }

    @PostMapping("/list")
    @ResponseBody
    public ApiDataTableResponse dataTableList(@ModelAttribute DataTableSearch searchBody) {
        Long now = System.currentTimeMillis();
        ApiDataTableResponse response = bookService.findForDataTable(searchBody);
        //ApiDataTableResponse response = bookService.findFromElastic(searchBody);
        System.out.println(System.currentTimeMillis() - now + "-------------------------------");
        response.setDraw(searchBody.getDraw());
        return response;
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

    @GetMapping("/addRandom")
    public String addRandom() {
        /*for (int i = 0; i < 10000; i++) {
            Book book = randomBookService.getRandomBook();
            bookService.add(book);
        }*/
        return "redirect:/book/list";
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
    @ResponseBody
    public ApiResponse add(@Valid Book book,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
        }
        book = bookService.add(book);
        return ApiResponse.ofSuccess(book);
    }

    @PostMapping(value = "/update")
    @ResponseBody
    public ApiResponse update(@Valid @ModelAttribute("book")Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
        }
        book = bookService.update(book);
        return ApiResponse.ofSuccess(book);
    }

    @GetMapping("/delete/{id}")//这里其实做成异步删除比较好
    public String delete(@PathVariable("id")String id, LoginUser loginUser) {
        bookService.deleteBook(id);
        return "redirect:/book/list";
        //return "redirect:/book/list1";
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
