package com.sauron.eye.config;

import com.sauron.eye.util.SignatureUtils;
import com.tigerbrokers.stock.openapi.client.config.ClientConfig;
import com.tigerbrokers.stock.openapi.client.struct.enums.Language;

public class BasicConfig {

    public static final String tigerId = "20181166";

    /**
     * 环球账户U码
     */
    public static final String account = null;

    /**
     * 标准账户
     */
    public static final String standard_account = null;

    /**
     * 模拟账户
     */
    public static final String paper_account = "2020030118595868";

    /**
     * 开放平台网关地址
     */
    public static final String SERVER_URL = "https://openapi.itiger.com/gateway";

    static {
        ClientConfig clientConfig = ClientConfig.DEFAULT_CONFIG;
        clientConfig.tigerId = tigerId;
        clientConfig.defaultAccount = paper_account;
        clientConfig.privateKey = SignatureUtils.getPrivateKey();
    }

    public static ClientConfig getDefaultClientConfig() {
        return ClientConfig.DEFAULT_CONFIG;
    }

    public static final String SSL = "ssl";

    public static final String SOCKET_HOST = "openapi.itiger.com";

    public static final int SOCKET_PORT = 8883;

    public static final String TIGER_PUBLIC_KEY = "";

    public static final String SIGN_TYPE = "RSA";

    public static final String CHARSET = "UTF-8";

    public static final String LANGUAGE = Language.zh_CN.name();

    public static final int TIMEOUT = 15;

    /**
     * sandbox 环境配置
     */
    public static final String SANDBOX_SERVER_URL = "https://openapi-sandbox.itiger.com/gateway";

    public static final String SANDBOX_SOCKET_HOST = "openapi-sandbox.itiger.com";

    public static final int SANDBOX_SOCKET_PORT = 8885;

    public static final String SANDBOX_TIGER_PUBLIC_KEY = "";

}
