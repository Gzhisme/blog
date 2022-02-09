package com.gzh.blog.service;

import com.gzh.blog.vo.ArticleVo;
import com.gzh.blog.vo.Result;
import com.gzh.blog.vo.params.ArticleParams;
import com.gzh.blog.vo.params.PageParams;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 高梓航
 */
public interface ArticleService {
    /**
     * 首页 - 文章列表
     * @param pageParams
     * @return
     */
    Result listArticlesPage(PageParams pageParams);

    /**
     * 首页 - 最热文章
     * @param limit
     * @return
     */
    Result hotArticle(int limit);

    /**
     * 首页 - 最新文章
     * @param limit
     * @return
     */
    Result newArticle(int limit);

    /**
     * 首页 - 文章归档
     * @return
     */
    Result listArchives();

    /**
     * 文章详情
     * @param articleId
     * @return
     */
    Result findArticleById(Long articleId);

    /**
     * 文章发布服务
     * @param articleParams
     * @return
     */
    Result publish(ArticleParams articleParams);
}
