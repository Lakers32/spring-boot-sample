package com.cn.boot.sample.security.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Chen Nan
 */
@Data
@Component
@ConfigurationProperties(prefix = "boot.sample.security")
public class SecurityProperties {
    /**
     * 浏览器配置
     */
    private BrowserProperties browser = new BrowserProperties();

    /**
     * 验证码配置
     */
    private ValidateCodeProperties code = new ValidateCodeProperties();
}
