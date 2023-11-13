package com.cs_liudi.community.entity;

public interface CommunityConstant {
    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 1;
    /**
     * 激活重复
     */
    int ACTIVATION_REPEAT=2;
    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 3;

    /**
     *默认状态的登陆凭证超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     *记住状态的登录凭证超时时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 12;
}
