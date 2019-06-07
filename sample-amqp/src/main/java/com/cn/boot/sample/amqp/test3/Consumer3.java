package com.cn.boot.sample.amqp.test3;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * TopicExchange-消费者
 *
 * @author Chen Nan
 * @date 2019/6/2.
 */
@Component
@Slf4j
public class Consumer3 {

    static {
        try {
            init();
        } catch (IOException | TimeoutException e) {
            log.error("error:", e);
        }
    }

    private static void init() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setVirtualHost("/");

        Connection connection = factory.newConnection();

        Channel channel = connection.createChannel();

        String exchangeName = "test03_topic_exchange";
        String queueName = "test03_queue";
        String routingKey = "test03.#";

        // 声明交换机
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, true, false, false, null);
        // 声明队列
        channel.queueDeclare(queueName, true, false, false, null);
        // 绑定交换机与队列
        channel.queueBind(queueName, exchangeName, routingKey);

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, StandardCharsets.UTF_8);
                String routingKey = envelope.getRoutingKey();
                String contentType = properties.getContentType();
                long deliveryTag = envelope.getDeliveryTag();
                log.info("收到消息：message = " + message);
                log.info("routingKey = " + routingKey);
                log.info("contentType = " + contentType);
                log.info("deliveryTag = " + deliveryTag);
            }
        };

        channel.basicConsume(queueName, true, consumer);
    }
}
