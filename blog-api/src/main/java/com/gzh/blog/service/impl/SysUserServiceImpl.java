package com.gzh.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gzh.blog.dao.mapper.SysUserMapper;
import com.gzh.blog.dao.pojo.SysUser;
import com.gzh.blog.service.LoginService;
import com.gzh.blog.service.SysUserService;
import com.gzh.blog.vo.ErrorCode;
import com.gzh.blog.vo.LoginUserVo;
import com.gzh.blog.vo.Result;
import com.gzh.blog.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 高梓航
 */
@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;
//    @Autowired
//    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private LoginService loginService;

    /**
     * 根据ID查询用户
     * @param userId
     * @return
     */
    @Override
    public SysUser findUserById(Long userId) {
        //根据id查询
        //为防止sysUser为空增加一个判断
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            sysUser = new SysUser();
            sysUser.setNickname("Gzh");
        }
        return sysUser;
    }

    /**
     * 根据账户密码查询用户
     * @param account
     * @param password
     * @return
     */
    @Override
    public SysUser findUser(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account);
        queryWrapper.eq(SysUser::getPassword, password);
        queryWrapper.select(SysUser::getAccount, SysUser::getId, SysUser::getAvatar, SysUser::getNickname);
        queryWrapper.last("limit 1");
        return sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 根据token查询用户
     * @param token
     * @return
     */
    @Override
    public Result findUserByToken(String token) {
        /**
         * 验证token，
         * 1. token合法性校验
         *    是否为空，解析是否成功 redis是否存在
         * 2. 如果校验失败 返回错误
         * 3. 如果成功，返回对应的结果 LoginUserVo
         */
        SysUser sysUser = loginService.checkToken(token);
        if (sysUser == null) {
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
        }
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setId(String.valueOf(sysUser.getId()));
        loginUserVo.setNickname(sysUser.getNickname());
        loginUserVo.setAvatar(sysUser.getAvatar());
        loginUserVo.setAccount(sysUser.getAccount());
        return Result.success(loginUserVo);
    }

    @Override
    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account);
        queryWrapper.last("limit 1");
        return this.sysUserMapper.selectOne(queryWrapper);
    }

    @Override
    public void save(SysUser sysUser) {
        //自动生成id，默认生成的id是根据雪花算法生成的分布式id
        this.sysUserMapper.insert(sysUser);
    }

    @Override
    public UserVo findUserVoById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser == null){
            sysUser = new SysUser();
            sysUser.setId(1L);
            sysUser.setAvatar("/static/img/logo.b3a48c0.png");
            sysUser.setNickname("Gzh");
        }
        UserVo userVo  = new UserVo();
        BeanUtils.copyProperties(sysUser,userVo);
        userVo.setId(String.valueOf(sysUser.getId()));
        return userVo;
    }
}
