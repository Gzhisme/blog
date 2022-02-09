package com.gzh.blog.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.gzh.blog.dao.mapper.ArticleMapper;
import com.gzh.blog.dao.pojo.Article;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author 高梓航
 */
@Service
public class ThreadService {

    @Async("taskExecutor")
    public void updateArticleViewCount(ArticleMapper articleMapper, Article article) {
        int viewCounts = article.getViewCounts();
        Article articleUpdate = new Article();
        articleUpdate.setViewCounts(viewCounts + 1);
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId, article.getId());
        // 为了多线程下的线程安全
        updateWrapper.eq(Article::getViewCounts, viewCounts);
        // update article set view_count=100 where view_count=99 and id=11
        articleMapper.update(articleUpdate, updateWrapper);
    }

}
