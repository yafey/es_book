package com.lida.es_book.config;

import com.lida.es_book.base.ESConstants;
import com.lida.es_book.service.user.UserService;
import com.lida.es_book.util.CookieUtil;
import com.lida.es_book.web.dto.LoginUser;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 *Created by LidaDu on 2018/1/8.  
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver{

    @Resource
    private UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz==LoginUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);

        String paramToken = request.getParameter(ESConstants.COOKIE_NAME_TOKEN);//为了兼容手机客户端，有时候手机端会吧cookie存到param
        String cookieToken = getCookieValue(request, ESConstants.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
        return userService.getByToken(response, token);
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        return CookieUtil.get(request, cookieName) == null ? null : CookieUtil.get(request, cookieName).getValue();
    }
}
