package com.itheima.reggie.file;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.common.ThreadLocalImpi;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Configuration
@WebFilter(filterName = "webfile",urlPatterns = "/*")//webfilter注解,里面注明外检的名字,注明拦截的请求
@Slf4j
public class WebFile implements Filter {
    public static final AntPathMatcher annotation=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
       //向下转型
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //业务逻辑1,得到url,
//        看看url是否需要处理
        String requestURI = request.getRequestURI();
        //设定一个不需要处理的url数组
        String[] urls=new String[]{
          "/employee/login",
          "/employee/logout",
          "/backend/**",
          "/front/**",
                "/user/login",
                "/user/sendMsg"

        };
        //判断是否匹配
        Boolean aBoolean = urlPatterns(urls, requestURI);
        if(aBoolean){
            //如果通过就方形
            filterChain.doFilter(request, response);
            return;
        }
        //判断是否登录
        //得到session对象

        if(request.getSession().getAttribute("employee")!=null) {
            Long employee =(Long)  request.getSession().getAttribute("employee");
            //如何得到线程的id
            Long id =Thread.currentThread().getId();
            log.info("webfile线程id{}",id);
            ThreadLocalImpi.set(employee);
            filterChain.doFilter(request, response);
            return;
        }
        if(request.getSession().getAttribute("user")!=null) {
            Long employee =(Long)  request.getSession().getAttribute("user");
            //如何得到线程的id
            Long id =Thread.currentThread().getId();
            log.info("webfile线程id{}",id);
            ThreadLocalImpi.set(employee);
            filterChain.doFilter(request, response);
            return;
        }
        //否则就需要处理,处理的逻辑是返回json数据给到前端
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;




    }
    //这个方法是匹配url的方法,url只能单个比较,但又是一个集合,所以只要里面有一个能够通过就是true;
    public Boolean urlPatterns(String[] urls,String url) {
        for (String url1:urls) {
            boolean match = annotation.match(url1, url);
            if(match){
                return true;
            }


        }
        return false;
    }
}
