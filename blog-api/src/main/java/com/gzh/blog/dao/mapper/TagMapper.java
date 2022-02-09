package com.gzh.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gzh.blog.dao.pojo.Tag;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 高梓航
 */
@Repository
public interface TagMapper extends BaseMapper<Tag> {
    List<Tag> findTagsByArticleId (Long articleId);
    /**
     * 查询最热的标签 前n条
     * @param limit
     * @return
     */
    List<Long> findHotsTagIds(int limit);

    List<Tag> findTagsByTagIds(List<Long> tagIds);
}
