package com.gzh.blog.common.cache;

import java.lang.annotation.*;

/**
 * @author 高梓航
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {

    // 持续时间
    long expire() default 1 * 60 * 1000;
    // 缓存名称
    String name() default "";

}
