package com.gzh.blog.service;

import com.gzh.blog.dao.pojo.SysUser;
import com.gzh.blog.vo.Result;
import com.gzh.blog.vo.params.LoginParams;

/**
 * @author 高梓航
 */
public interface LoginService {

    /**
     * 登录
     * @param loginParams
     * @return
     */
    Result login(LoginParams loginParams);

    /**
     * 根据tokenh获取用户信息
     * @param token
     * @return
     */
    SysUser checkToken(String token);

    /**
     * 注销
     * @param token
     * @return
     */
    Result logout(String token);

    /**
     * 注册
     * @param loginParams
     * @return
     */
    Result register(LoginParams loginParams);
}
