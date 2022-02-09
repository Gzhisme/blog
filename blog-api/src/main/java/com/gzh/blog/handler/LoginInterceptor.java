package com.gzh.blog.handler;

import com.alibaba.fastjson.JSON;
import com.gzh.blog.dao.pojo.SysUser;
import com.gzh.blog.service.LoginService;
import com.gzh.blog.utils.UserThreadLocal;
import com.gzh.blog.vo.ErrorCode;
import com.gzh.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 高梓航
 */
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private LoginService loginService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //在执行controller方法(Handler)之前进行执行
        /**
         * 1. 需要判断 请求的接口路径 是否为 HandlerMethod (controller方法)
         * 2. 判断 token是否为空，如果为空 未登录
         * 3. 如果token 不为空，登录验证 loginService checkToken
         * 4. 如果认证成功 放行即可
         */
        if (!(handler instanceof HandlerMethod)){
            // handler有两种情况
            // RequestResourceHandler(访问静态资源，会默认去classpath下的static目录去查询)
            // HandlerMethod(访问controller方法)
            return true;
        }
        String token = request.getHeader("Authorization");

        log.info("=================request start=================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end=================");


        if (StringUtils.isBlank(token)){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            // 将result封装为json字符串
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        SysUser sysUser = loginService.checkToken(token);
        if (sysUser == null){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }
        //登录验证成功，放行
        //通过ThreadLocal保存用户信息，可以在controller中直接获取
        UserThreadLocal.put(sysUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //如果不删除ThreadLocal中用完的信息，会有内存泄漏的风险
        UserThreadLocal.remove();
    }
}
