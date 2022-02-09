package com.gzh.blog.vo.params;

import com.gzh.blog.vo.CategoryVo;
import com.gzh.blog.vo.TagVo;
import lombok.Data;

import java.util.List;

/**
 * @author 高梓航
 */
@Data
public class ArticleParams {

    private Long id;

    private ArticleBodyParams body;

    private CategoryVo category;

    private String summary;

    private List<TagVo> tags;

    private String title;
}
