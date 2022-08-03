package com.example.reggie.Filter;

import com.alibaba.fastjson.JSON;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.R;
import com.sun.org.apache.bcel.internal.generic.RET;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @date 2022/7/10- 13:30
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher MATCHER=new AntPathMatcher();

    //过滤请求
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse) servletResponse;
        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("本次请求的路径：{}",requestURI);
        //定义不需要处理的路径
        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //3.若不需要，则直接放行
        if(check){
            log.info("本次请求不需要处理的路径：{}",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //4-1.如果已登录，也直接放行
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录");
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }
        //4-2,
        if(request.getSession().getAttribute("user")!=null){
            log.info("用户已登录");
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }
        //5.如果未登录则返回未登录结果，通过输出流方式向客户端发送消息
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }
    public boolean check(String[] urls,String URI){
        for (String url : urls) {
            boolean match = MATCHER.match(url, URI);
            if(match) {
                return true;
            }
        }
        return false;
    }
}
