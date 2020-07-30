package com.majiale.community.dto;

import lombok.Data;

/**
 * GithubUser类也是使用GitHub进行登录时，交互需要的数据
 */
@Data
public class GithubUser {
    private String name;
    private Long id;
    private String bio;
    private String avatarUrl;
}
