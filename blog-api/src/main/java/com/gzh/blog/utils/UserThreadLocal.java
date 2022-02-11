package com.gzh.blog.utils;

import com.gzh.blog.dao.pojo.SysUser;

/**
 * @author 高梓航
 */
public class UserThreadLocal {

    private UserThreadLocal(){}

    // 作用：线程变量隔离
    private static final ThreadLocal<SysUser> threadLocal = new ThreadLocal<>();

    public static void put(SysUser sysUser){
        threadLocal.set(sysUser);
    }

    public static SysUser get(){
        return threadLocal.get();
    }

    public static void remove(){
        threadLocal.remove();
    }
}
