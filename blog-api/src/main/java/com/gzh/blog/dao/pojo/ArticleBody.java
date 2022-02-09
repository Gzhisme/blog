package com.gzh.blog.dao.pojo;

import lombok.Data;

/**
 * @author 高梓航
 */
@Data
public class ArticleBody {
    private Long id;
    private String content;
    private String contentHtml;
    private Long articleId;
}
