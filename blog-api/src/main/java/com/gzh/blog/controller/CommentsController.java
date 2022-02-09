package com.gzh.blog.controller;

import com.gzh.blog.service.CommentsService;
import com.gzh.blog.vo.Result;
import com.gzh.blog.vo.params.CommentParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 高梓航
 */
@RestController
@RequestMapping("comments")
public class CommentsController {
    @Autowired
    private CommentsService commentsService;

    ///comments/article/{id}

    @GetMapping("article/{id}")
    public Result getComments(@PathVariable("id") Long id){
        return commentsService.getCommentsByArticleId(id);
    }

    @PostMapping("create/change")
    public Result makeComment(@RequestBody CommentParams commentParams){
        return commentsService.makeComment(commentParams);
    }
}
