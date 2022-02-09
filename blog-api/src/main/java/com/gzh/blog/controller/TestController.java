package com.gzh.blog.controller;

import com.gzh.blog.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 高梓航
 */
@RestController
@RequestMapping("test")
public class TestController {

    @RequestMapping
    public Result test(){
        return Result.success(null);
    }
}
