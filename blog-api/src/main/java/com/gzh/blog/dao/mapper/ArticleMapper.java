package com.gzh.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzh.blog.dao.dos.Archives;
import com.gzh.blog.dao.pojo.Article;
import com.gzh.blog.dao.pojo.SysUser;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 高梓航
 */
@Repository
public interface ArticleMapper extends BaseMapper<Article> {
    List<Archives> listArchives();

    IPage<Article> listArticle(Page<Article> page, Long categoryId, Long tagId, String year, String month);
}
