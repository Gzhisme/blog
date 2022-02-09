package com.gzh.blog.dao.pojo;

import lombok.Data;

/**
 * @author 高梓航
 */
// 文章与Tag关联的Pojo
@Data
public class ArticleTag {
    private Long id;
    private Long articleId;
    private Long tagId;
}
