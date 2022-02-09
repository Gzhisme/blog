package com.gzh.blog.service.mq;

import com.alibaba.fastjson.JSON;
import com.gzh.blog.service.ArticleService;
import com.gzh.blog.vo.Result;
import com.gzh.blog.vo.params.MQMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;

/**
 * @author 高梓航
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "blog-update-article", consumerGroup = "blog-update-article-group")
public class MQListener implements RocketMQListener<MQMessage> {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void onMessage(MQMessage message) {
        log.info("收到的消息:{}", message);
        //1. 更新查看文章详情的缓存
        Long articleId = message.getArticleId();
        if (articleId != null) {
            String params = DigestUtils.md5Hex(articleId.toString());
            String redisKey = "view_article::ArticleController::findArticleById::" + params;
            Result articleResult = articleService.findArticleById(articleId);
            redisTemplate.opsForValue().set(redisKey, JSON.toJSONString(articleResult), Duration.ofMillis(5 * 60 * 1000));
            log.info("更新了缓存:{}", redisKey);
        }

        //2. 文章列表的缓存，直接删除缓存，使得程序从数据库重新读取
        Set<String> keys = redisTemplate.keys("listArticle*");
        keys.forEach(s -> {
            redisTemplate.delete(s);
            log.info("删除了文章列表的缓存:{}", s);
        });
    }
}
