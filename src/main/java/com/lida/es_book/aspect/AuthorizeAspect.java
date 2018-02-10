package com.lida.es_book.aspect;

import com.lida.es_book.base.ESConstants;
import com.lida.es_book.exception.ESAuthorizeException;
import com.lida.es_book.redis.LoginUserKey;
import com.lida.es_book.redis.RedisService;
import com.lida.es_book.util.CookieUtil;
import com.lida.es_book.web.dto.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 *Created by LidaDu on 2018/1/9.  
 */
@Aspect
@Component
@Slf4j
public class AuthorizeAspect {

    @Resource
    private RedisService redisService;

    /*切入点    帶ModelAttribute注解的controller会导致doVerify执行两遍   ModelAttribute会拦截所有进入该controller的请求*/
    @Pointcut("execution(* com.lida.es_book.web.controller.*.*(..)) && !@annotation(org.springframework.web.bind.annotation.ModelAttribute)"
            + "&& !execution(* com.lida.es_book.web.controller.LoginController.*(..))"
            + "&& !execution(* com.lida.es_book.web.controller.HomeController.*(..))")
    public void verify(){};

    /*在切入点之前要做的事*/
    @Before("verify()")
    public void doVerify() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        /*   没登陆重定向到登录页 ---> 这个方法不行，sendRedirect后依然会走后边controller逻辑，
        String path = request.getContextPath();
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int port = request.getServerPort();
        String basePath = scheme + "://" + serverName + ":" + port + path;
        //查询cookie
        Cookie cookie = CookieUtil.get(request, ESConstants.COOKIE_NAME_TOKEN);
        if (cookie == null) {
            try {
                response.sendRedirect(basePath + "/");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String token = cookie.getValue();
        LoginUser user = redisService.get(LoginUserKey.session, token, LoginUser.class);
        if (user == null) {
            try {
                response.sendRedirect(basePath + "/");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        Cookie cookie = CookieUtil.get(request, ESConstants.COOKIE_NAME_TOKEN);
        if (cookie == null) {
            throw new ESAuthorizeException();
        }

        String token = cookie.getValue();
        LoginUser user = redisService.get(LoginUserKey.session, token, LoginUser.class);
        if (user == null) {
            throw new ESAuthorizeException();
        }
    }
}
