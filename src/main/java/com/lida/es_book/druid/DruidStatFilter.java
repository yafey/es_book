package com.lida.es_book.druid;

import com.alibaba.druid.support.http.WebStatFilter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

/*
 *Created by LidaDu on 2017/12/27.  
 */
@WebFilter(filterName="druidWebStatFilter",urlPatterns="/*",

        initParams={

                @WebInitParam(name="exclusions",value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")//忽略资源

        }

)
public class DruidStatFilter extends WebStatFilter {
}
