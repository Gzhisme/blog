package com.gzh.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gzh.blog.dao.dos.Archives;
import com.gzh.blog.dao.mapper.ArticleBodyMapper;
import com.gzh.blog.dao.mapper.ArticleMapper;
import com.gzh.blog.dao.mapper.ArticleTagMapper;
import com.gzh.blog.dao.pojo.Article;
import com.gzh.blog.dao.pojo.ArticleBody;
import com.gzh.blog.dao.pojo.ArticleTag;
import com.gzh.blog.dao.pojo.SysUser;
import com.gzh.blog.service.*;
import com.gzh.blog.utils.UserThreadLocal;
import com.gzh.blog.vo.ArticleBodyVo;
import com.gzh.blog.vo.ArticleVo;
import com.gzh.blog.vo.Result;
import com.gzh.blog.vo.TagVo;
import com.gzh.blog.vo.params.ArticleParams;
import com.gzh.blog.vo.params.MQMessage;
import com.gzh.blog.vo.params.PageParams;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 高梓航
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private ArticleBodyMapper articleBodyMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public Result listArticlesPage(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());

        IPage<Article> articleIPage = articleMapper.listArticle(
                page,
                pageParams.getCategoryId(),
                pageParams.getTagId(),
                pageParams.getYear(),
                pageParams.getMonth()
        );
        List<Article> records = articleIPage.getRecords();
        return Result.success(copyList(records, true, true));
    }
//    @Override
//    public Result listArticlesPage(PageParams pageParams) {
//        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
//        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
//        if (pageParams.getCategoryId() != null) {
//            // and category_id=#{categoryId}
//            queryWrapper.eq(Article::getCategoryId, pageParams.getCategoryId());
//        }
//
//        if (pageParams.getTagId() != null) {
//            // article表中并没有tag字段，因为一篇文章有多个标签
//            // 先根据tagId查出这个Id下的所有ArticleId，再查询Article满足Article的Id在查出的ArticleId范围中
//            // 因为这里是针对Article表的查询
//            LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
//            articleTagLambdaQueryWrapper.eq(ArticleTag::getTagId, pageParams.getTagId());
//            List<ArticleTag> articleTags = articleTagMapper.selectList(articleTagLambdaQueryWrapper);
//
//            List<Long> articleIdList = new ArrayList<>();
//            for (ArticleTag articleTag : articleTags) {
//                articleIdList.add(articleTag.getArticleId());
//            }
//            if (articleIdList.size() > 0) {
//                // and id in(1,2,3)
//                queryWrapper.in(Article::getId, articleIdList);
//            }
//        }
//        //是否置顶 & 时间倒序
//        queryWrapper.orderByDesc(Article::getWeight, Article::getCreateDate);
//        //分页查询
//        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
//        List<Article> records = articlePage.getRecords();
//        //将List<Article>转换为前端需要的List<ArticleVo>
//        List<ArticleVo> articleVoList = copyList(records, true, true);
//        return Result.success(articleVoList);
//    }

    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor, false, false));
        }
        return articleVoList;
    }

    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor, isBody, isCategory));
        }
        return articleVoList;
    }

    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        // 不是所有接口都需要tag和author，比如最热文章就不需要，因此设置isTag和isAuthor两个参数
        ArticleVo articleVo = new ArticleVo();
        // 将ArticleVo中的Id属性由Long改为String后，通过BeanUtils进行属性复制时Id属性就无法复制了
        // 因为在Article中Id是Long类型，ArticleVo中Id是String类型
        // 因此需要单独写一下Id的复制
        articleVo.setId(String.valueOf(article.getId()));
        BeanUtils.copyProperties(article, articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));
        // articleVo比article多了tag和author属性，这两个属性要从sys_user表和tag表中查询
        if (isTag) {
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if (isAuthor) {
            //拿到作者id
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        if (isBody) {
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if (isCategory) {
            Long categoryId = article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }
        return articleVo;
    }

    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.last("limit " + limit);
        //select id,title from article order by view_counts desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles, false, false));
    }

    @Override
    public Result newArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.last("limit " + limit);
        //select id,title from article order by create_date desc desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles, false, false));
    }

    @Override
    public Result listArchives() {
        List<Archives> archivesList = articleMapper.listArchives();
        return Result.success(archivesList);
    }

    @Override
    public Result findArticleById(Long articleId) {
        /**
         * 1. 根据id查询文章信息
         * 2. 根据bodyId和categoryId去做关联查询
         */
        Article article = this.articleMapper.selectById(articleId);
        ArticleVo articleVo = copy(article, true, true, true, true);
        // 当前方法发生在前端点入文章详情页前
        // 如果直接更新文章阅读数，因为向数据库写数据时，是要加写锁的，会阻塞其他的读操作，性能比较低
        // 反映到用户层面来说，用户点入文章详情页会停顿比较久的时间，带来很差的用户体验
        // 采用线程池的方法，可以把更新操作丢给线程池中去执行，与当前线程异步执行
        threadService.updateArticleViewCount(articleMapper, article);
        // 点入文章详情页就给消息队列发消息，更新首页文章列表(当然包括阅读数)
        MQMessage mqMessage = new MQMessage();
        rocketMQTemplate.convertAndSend("blog-update-article", mqMessage);
        return Result.success(articleVo);
    }

    @Override
    public Result publish(ArticleParams articleParams) {
        // 发布文章时需要设置作者ID，需要从ThreadLocal中获得用户信息
        // 因此这个接口服务要加入到登录拦截当中，即发布文章前需要先登录
        SysUser sysUser = UserThreadLocal.get();
        /**
         * 1. 发布文章目的：构建Article对象
         * 2. 作者id：当前的登录用户
         * 3. 标签：要将标签加入到关联列表当中[根据文章和标签关联的数据表]
         * 4. body 内容存储 article bodyId
         */
        Article article = new Article();
        // 判断是否是编辑文章
        boolean isEdit = false;
        if (articleParams.getId() != null) {
            article.setId(articleParams.getId());
            article.setTitle(articleParams.getTitle());
            article.setSummary(articleParams.getSummary());
            article.setCategoryId(Long.parseLong(articleParams.getCategory().getId()));
            articleMapper.updateById(article);
            isEdit = true;
        } else {
            article.setAuthorId(sysUser.getId());
            article.setWeight(Article.Article_Common);
            article.setViewCounts(0);
            article.setTitle(articleParams.getTitle());
            article.setSummary(articleParams.getSummary());
            article.setCommentCounts(0);
            article.setCreateDate(System.currentTimeMillis());
            article.setCategoryId(Long.parseLong(articleParams.getCategory().getId()));
            // 插入之后会生成一个文章id
            this.articleMapper.insert(article);
        }
        // 保存tag
        List<TagVo> tags = articleParams.getTags();
        if (tags != null) {
            // 227行执行insert后会生成文章Id
            Long articleId = article.getId();
            if (isEdit) {
                LambdaQueryWrapper<ArticleTag> queryWrapper = Wrappers.lambdaQuery();
                queryWrapper.eq(ArticleTag::getArticleId, articleId);
                articleTagMapper.delete(queryWrapper);
            }
            for (TagVo tag : tags) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(Long.parseLong(tag.getId()));
                articleTag.setArticleId(articleId);
                articleTagMapper.insert(articleTag);
            }
        }
        // 保存body
        ArticleBody articleBody = new ArticleBody();
        if (isEdit) {
            articleBody.setArticleId(article.getId());
            articleBody.setContent(articleParams.getBody().getContent());
            articleBody.setContentHtml(articleParams.getBody().getContentHtml());
            LambdaQueryWrapper<ArticleBody> updateWrapper = Wrappers.lambdaQuery();
            updateWrapper.eq(ArticleBody::getArticleId, article.getId());
            articleBodyMapper.update(articleBody, updateWrapper);
        } else {
            articleBody.setArticleId(article.getId());
            articleBody.setContent(articleParams.getBody().getContent());
            articleBody.setContentHtml(articleParams.getBody().getContentHtml());
            articleBodyMapper.insert(articleBody);
            article.setBodyId(articleBody.getId());
            // article对象的属性中包含bodyId，bodyId在259行插入后才出现，因此需要更新一下article对象
            articleMapper.updateById(article);
        }
        Map<String, String> map = new HashMap<>();
        // 放String考虑到精度损失问题，也可以返回ArticleVo对象
        map.put("id", article.getId().toString());

        // 给消息队列发送通知
        if (isEdit) {
            MQMessage mqMessage = new MQMessage();
            mqMessage.setArticleId(article.getId());
            rocketMQTemplate.convertAndSend("blog-update-article", mqMessage);
        }
        return Result.success(map);
    }

    private ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }
}
