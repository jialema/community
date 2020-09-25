package com.majiale.community.dto;

import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;        // 通知的id
    private Long gmtCreate; // 通知的创建时间
    private Integer status; // 通知的状态，1表示已读，0表示未读
    private Long notifier;  // 通知的人而不是被通知的人
    private String notifierName; // 通知人的名字
    private String outerTitle; // 被回复的问题题目
    private Long outerid; // 被回复的问题id
    private String typeName; // 回复的类型名
    private Integer type; // 回复的类型
}
