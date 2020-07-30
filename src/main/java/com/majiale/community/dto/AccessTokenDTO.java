package com.majiale.community.dto;

import lombok.Data;

/**
 * AccessTokenDTO就是用于数据传输，没别的吊用，
 * 主要用在用户登录和github互动过程中的数据传输
 */
@Data
public class AccessTokenDTO {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;
}
