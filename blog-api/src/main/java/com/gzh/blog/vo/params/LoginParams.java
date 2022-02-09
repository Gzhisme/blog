package com.gzh.blog.vo.params;

import lombok.Data;

/**
 * @author 高梓航
 */
@Data
public class LoginParams {
    private String account;
    private String password;
    private String nickname;
}
