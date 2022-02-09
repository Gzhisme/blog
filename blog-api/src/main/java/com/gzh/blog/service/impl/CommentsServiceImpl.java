package com.gzh.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gzh.blog.dao.mapper.CommentsMapper;
import com.gzh.blog.dao.pojo.Comment;
import com.gzh.blog.dao.pojo.SysUser;
import com.gzh.blog.service.CommentsService;
import com.gzh.blog.service.SysUserService;
import com.gzh.blog.utils.UserThreadLocal;
import com.gzh.blog.vo.CommentVo;
import com.gzh.blog.vo.Result;
import com.gzh.blog.vo.UserVo;
import com.gzh.blog.vo.params.CommentParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 高梓航
 */
@Service
public class CommentsServiceImpl implements CommentsService {
    @Autowired
    private CommentsMapper commentsMapper;
    @Autowired
    private SysUserService sysUserService;

    @Override
    public Result getCommentsByArticleId(Long id) {
        /**
         * 1. 根据文章id从comment表查询评论列表
         * 2. 根据作者的id查询作者的信息
         * 3. 判断，如果level=1要去查询它有没有子评论
         * 4. 如果有，根据评论id(parent_id)进行查询
         * 以上内容在copyList方法中完成
         */
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId, id);
        // 只需要查出第一层(level=1)的评论
        queryWrapper.eq(Comment::getLevel, 1);
        queryWrapper.orderByDesc(Comment::getCreateDate);
        List<Comment> comments = commentsMapper.selectList(queryWrapper);
        List<CommentVo> commentVoList = copyList(comments);
        return Result.success(commentVoList);
    }

    @Override
    public Result makeComment(CommentParams commentParams) {
        // 登录后用户信息保存在ThreadLocal中
        SysUser sysUser = UserThreadLocal.get();
        Comment comment = new Comment();
        comment.setArticleId(commentParams.getArticleId());
        comment.setAuthorId(sysUser.getId());
        comment.setContent(commentParams.getContent());
        comment.setCreateDate(System.currentTimeMillis());
        Long parent = commentParams.getParent();
        if (parent == null || parent == 0) {
            comment.setLevel(1);
        } else {
            comment.setLevel(2);
        }
        comment.setParentId(parent == null ? 0 : parent);
        Long toUserId = commentParams.getToUserId();
        comment.setToUid(toUserId == null ? 0 : toUserId);
        this.commentsMapper.insert(comment);
        return Result.success(null);
    }

    private List<CommentVo> copyList(List<Comment> comments) {
        List<CommentVo> commentVoList = new ArrayList<>();
        for (Comment comment : comments) {
            commentVoList.add(copy(comment));
        }
        return commentVoList;
    }

    private CommentVo copy(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment, commentVo);
        // 这里是为了防止前端用number接收Long类型的数据时造成精度缺失
        // 在Vo对象中用String取代Long表示Id属性
        commentVo.setId(String.valueOf(comment.getId()));
        // 作者信息
        Long authorId = comment.getAuthorId();
        UserVo userVo = this.sysUserService.findUserVoById(authorId);
        commentVo.setAuthor(userVo);
        // 子评论
        Integer level = comment.getLevel();
        if (1 == level) {
            Long id = comment.getId();
            List<CommentVo> commentVoList = findCommentsByParentId(id);
            commentVo.setChildrens(commentVoList);
        }
        // toUser(给谁评论)
        if (level > 1) {
            Long toUid = comment.getToUid();
            UserVo toUserVo = this.sysUserService.findUserVoById(toUid);
            commentVo.setToUser(toUserVo);
        }
        return commentVo;
    }

    private List<CommentVo> findCommentsByParentId(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId, id);
        queryWrapper.eq(Comment::getLevel, 2);
        return copyList(commentsMapper.selectList(queryWrapper));
    }
}
