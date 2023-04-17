package com.csu.mypetstore.filter;


import com.csu.mypetstore.shiro.JwtToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
* @Description: 自定义一个Filter，用来拦截所有的请求判断是否携带Token
* @Author: LZY
* @Date: 2021/4/19 22:09
*/
@Slf4j
public class JwtFilter extends AccessControlFilter {

    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        httpResponse.setHeader("Access-Control-Allow-Origin", httpRequest.getHeader("Origin"));
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Content-Type","application/json;charset=UTF-8");
        if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpResponse.setHeader("Access-Control-Allow-Methods", httpRequest.getMethod());
            httpResponse.setHeader("Access-Control-Allow-Headers", httpRequest.getHeader("Access-Control-Request-Headers"));
            return true;
        }
        httpResponse.setStatus(HttpStatus.OK.value());
        return super.preHandle(request,response);
    }

    /*
    * @Description:判断是否携带了有效的JwtToken,
    * 1. 返回true，shiro就直接允许访问url
    * 2. 返回false，shiro才会根据onAccessDenied的方法的返回值决定是否允许访问url
    * @Author: LZY
    * @Date: 2021/4/19 22:12
    * @Params:
    * @Return:
    */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws AuthenticationException {
        log.warn("isAccessAllowed 方法被调用");
        String jwt=((HttpServletRequest) request).getHeader("Authorization");
        JwtToken jwtToken = new JwtToken(jwt);
        try {
            //委托 realm 进行登录认证
            //所以这个地方最终还是调用JwtRealm进行的认证
            getSubject(request, response).login(jwtToken);
            //也就是subject.login(token)
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return false;
    }
}
