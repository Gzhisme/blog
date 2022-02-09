package com.gzh.blog.service;

import com.gzh.blog.vo.Result;
import com.gzh.blog.vo.TagVo;

import java.util.List;

/**
 * @author 高梓航
 */
public interface TagService {
    /**
     * 根据文章ID查标签
     * @param articleId
     * @return
     */
    List<TagVo> findTagsByArticleId(Long articleId);

    /**
     * 首页 - 最热标签
     * @param limit
     * @return
     */
    Result hots(int limit);

    Result findAll();

    Result findAllDetail();

    Result findDetailById(Long id);
}
