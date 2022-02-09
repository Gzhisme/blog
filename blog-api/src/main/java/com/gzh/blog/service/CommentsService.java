package com.gzh.blog.service;

import com.gzh.blog.vo.Result;
import com.gzh.blog.vo.params.CommentParams;

/**
 * @author 高梓航
 */
public interface CommentsService {

    Result getCommentsByArticleId(Long id);

    Result makeComment(CommentParams commentParams);

}
