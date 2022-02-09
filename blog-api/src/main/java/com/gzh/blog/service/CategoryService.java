package com.gzh.blog.service;

import com.gzh.blog.vo.CategoryVo;
import com.gzh.blog.vo.Result;

/**
 * @author 高梓航
 */
public interface CategoryService {

    CategoryVo findCategoryById(Long categoryId);

    Result findAll();

    Result findAllDetail();

    Result categoryDetailById(Long id);

}
