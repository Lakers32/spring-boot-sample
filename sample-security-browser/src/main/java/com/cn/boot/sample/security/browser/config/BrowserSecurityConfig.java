package com.cn.boot.sample.security.browser.config;

import com.cn.boot.sample.security.browser.session.ExpiredSessionStrategy;
import com.cn.boot.sample.security.browser.session.SampleInvalidSessionStrategy;
import com.cn.boot.sample.security.core.authentication.mobile.SmsCodeAuthenticationSecurityConfig;
import com.cn.boot.sample.security.core.config.BaseWebSecurityConfig;
import com.cn.boot.sample.security.core.config.ValidateCodeSecurityConfig;
import com.cn.boot.sample.security.core.config.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Chen Nan
 */
@Configuration
public class BrowserSecurityConfig extends BaseWebSecurityConfig {

    private final SecurityProperties securityProperties;
    private final UserDetailsService userDetailsService;
    private final ValidateCodeSecurityConfig validateCodeSecurityConfig;
    private final SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    @Autowired
    public BrowserSecurityConfig(SecurityProperties securityProperties, @Qualifier("loginServiceImpl") UserDetailsService userDetailsService, ValidateCodeSecurityConfig validateCodeSecurityConfig, SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig) {
        this.securityProperties = securityProperties;
        this.userDetailsService = userDetailsService;
        this.validateCodeSecurityConfig = validateCodeSecurityConfig;
        this.smsCodeAuthenticationSecurityConfig = smsCodeAuthenticationSecurityConfig;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                securityProperties.getBrowser().getLoginPage(),
                "/authentication/code/**",
                "/favicon.ico");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        applyPasswordAuthenticationConfig(http);
        http.apply(validateCodeSecurityConfig)
                .and()
                .apply(smsCodeAuthenticationSecurityConfig)
                .and()
                // 设置记住我
                .rememberMe()
                // 设置记住我多久
                .tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSeconds())
                .and()
                .sessionManagement()
                // 设置session超时后跳转地址
                .invalidSessionStrategy(new SampleInvalidSessionStrategy(securityProperties))
                // 设置同个账号运行同时登录多少个
                .maximumSessions(1)
                // true 只能登录x个，后面的登录失败 false 后面的踢掉之前登录的
                .maxSessionsPreventsLogin(true)
                // 多账号登录处理方式
                .expiredSessionStrategy(new ExpiredSessionStrategy())
                .and()
                .and()
                .authorizeRequests()
                // 如果是/login.html直接放行，注意：谷歌浏览器自己会请求favicon.ico
                .antMatchers(
                        "/authentication/form",
                        "/authentication/require").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .csrf().disable();


    }
}
