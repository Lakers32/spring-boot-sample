package com.cn.boot.sample.amqp.spring.config;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>Title:</p>
 * <p>Description:</p>
 *
 * @author Chen Nan
 * @date 2019/6/9.
 */
@Slf4j
public class MessageDelegate {

    /**
     * 方法名固定，或设置适配器反射的方法名
     * 传入参数类型根据消息发送方发送的消息类型
     */
    public void handleMessage(byte[] body) {
        log.info("收到消息：" + new String(body));
    }

    public void handleMessage(String body) {
        log.info("收到消息：" + body);
    }
}
