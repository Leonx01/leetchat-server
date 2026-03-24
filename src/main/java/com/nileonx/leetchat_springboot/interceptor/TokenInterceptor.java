package com.nileonx.leetchat_springboot.interceptor;

import com.nileonx.leetchat_springboot.common.ErrorCode;
import com.nileonx.leetchat_springboot.common.TokenRequired;
import com.nileonx.leetchat_springboot.exception.BusinessException;
import com.nileonx.leetchat_springboot.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {

//        System.out.println("preHandle方法处理控制器（controller）方法调用之前");
        log.info("Interceptor works: preHandle before（controller） method call");
        if(!(handler instanceof HandlerMethod)){
            return true; //如果不是映射到方法直接通过
        }

        Method method =((HandlerMethod) handler).getMethod();
        if(method.isAnnotationPresent(TokenRequired.class)){
            //获取方法上的注解
            TokenRequired tokenRequired = method.getAnnotation(TokenRequired.class);
            if(tokenRequired.required()){//判断是否需要拦截
                //获取请求头中的token
                String token=request.getHeader("token");
                log.info("token:{}",token);
                if(token==null) {
                    //如果token为空或者token过期
                    throw new BusinessException(ErrorCode.NO_AUTH, "token is valid");
                    //return false;
                }
                String uname = JwtUtil.decodeToken(token).getSubject();
                request.setAttribute("uname",uname);
                //如果token验证成功，将token中的数据取出放入request中，方便之后的方法使用
                return true;
            }
        }

        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {

//        log.info("postHandle方法处理控制器（controller）方法调用之后，在解析视图之前执行");
//        System.out.println("preHandle方法处理控制器（controller）方法调用之后，在解析视图之前执行");
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
//        log.info("afterCompletion在视图渲染之后执行");
//        System.out.println("afterCompletion在视图渲染之后执行");
    }
}