package com.gzh.blog.vo.params;

import lombok.Data;

/**
 * @author 高梓航
 */
@Data
public class CommentParams {

    private Long articleId;

    private String content;

    private Long parent;

    private Long toUserId;

}
