package com.gzh.blog.dao.pojo;

import lombok.Data;

/**
 * @author 高梓航
 */
@Data
public class Comment {

    private Long id;

    private String content;

    private Long createDate;

    private Long articleId;

    private Long authorId;

    private Long parentId; // 分布式Id需要用Long，对应数据库表中的字段需要用bigint

    private Long toUid;

    private Integer level;

}
