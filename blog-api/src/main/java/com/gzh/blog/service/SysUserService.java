package com.gzh.blog.service;

import com.gzh.blog.dao.pojo.SysUser;
import com.gzh.blog.vo.Result;
import com.gzh.blog.vo.UserVo;

/**
 * @author 高梓航
 */
public interface SysUserService {
    /**
     * 根据ID查询作者
     * @param userId
     * @return
     */
    SysUser findUserById(Long userId);

    SysUser findUser(String account, String password);

    /**
     * 根据token查询用户信息
     * @param token
     * @return
     */
    Result findUserByToken(String token);

    /**
     * 根据账户查找用户
     * @param account
     * @return
     */
    SysUser findUserByAccount(String account);

    /**
     * 保存用户
     * @param sysUser
     */
    void save(SysUser sysUser);

    UserVo findUserVoById(Long toUid);
}
