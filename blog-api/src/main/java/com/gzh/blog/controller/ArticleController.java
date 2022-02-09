package com.gzh.blog.controller;

import com.gzh.blog.common.aop.LogAnnotation;
import com.gzh.blog.common.cache.Cache;
import com.gzh.blog.service.ArticleService;
import com.gzh.blog.vo.ArticleVo;
import com.gzh.blog.vo.Result;
import com.gzh.blog.vo.params.ArticleParams;
import com.gzh.blog.vo.params.PageParams;
import org.aspectj.asm.IModelFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 高梓航
 */
@RestController
@RequestMapping("articles")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    /**
     * 首页 - 文章列表
     * @param pageParams
     * @return
     */
    @PostMapping
    @LogAnnotation(module = "文章列表", operator = "获取文章列表")
    @Cache(expire = 5 * 60 * 1000, name = "listArticle")
    public Result articles(@RequestBody PageParams pageParams) {
        return articleService.listArticlesPage(pageParams);
    }

    /**
     * 首页 - 最热文章
     * @return
     */
    @PostMapping("hot")
    public Result hotArticle(){
        int limit = 5;
        return articleService.hotArticle(limit);
    }

    /**
     * 首页 - 最新文章
     * @return
     */
    @PostMapping("new")
    public Result newArticle(){
        int limit = 5;
        return articleService.newArticle(limit);
    }

    /**
     * 首页 - 文章归档
     * @return
     */
    @PostMapping("listArchives")
    public Result listArchives(){
        return articleService.listArchives();
    }

    /**
     * 文章详情
     * @param articleId
     * @return
     */
    @PostMapping("view/{id}")
    public Result findArticleById(@PathVariable("id") Long articleId){
        return articleService.findArticleById(articleId);
    }

    /**
     * 发布文章
     * @param articleParams
     * @return
     */
    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParams articleParams){

        return articleService.publish(articleParams);
    }

    /**
     * 修改文章
     * @param articleId
     * @return
     */
    @PostMapping("write/{id}")
    public Result editArticleById(@PathVariable("id") Long articleId){
        return articleService.findArticleById(articleId);
    }
}
