package com.gzh.blog.controller;

import com.gzh.blog.utils.QiniuUtils;
import com.gzh.blog.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author 高梓航
 */
@RestController
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private QiniuUtils qiniuUtils;

    @PostMapping
    public Result upload(@RequestParam("image") MultipartFile file){
        // 上传时的原始文件名称，比如a.png
        String originalFilename = file.getOriginalFilename();
        // 存储时实际需要的唯一的文件名称
        // 通过StringUtils的方法拿到扩展名称(这里是png)
        String fileName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(originalFilename, ".");
        // 上传到七牛云服务器
        // 优势：按量付费、速度快、把图片发放到离用户最近的服务器上、降低自身服务器的带宽消耗
        boolean upload = qiniuUtils.upload(file, fileName);
        if (upload){
            return Result.success(QiniuUtils.url + fileName);
        }
        return Result.fail(20001,"上传失败");
    }
}

