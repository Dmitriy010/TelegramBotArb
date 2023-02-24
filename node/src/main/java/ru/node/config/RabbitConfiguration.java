package ru.node.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue textMessageQueue() {
        return new Queue("text_message_update");
    }
    @Bean
    public Queue textAnswerMessageQueue() {
        return new Queue("answer_message");
    }
    @Bean
    public Queue textSubscribeMessageQueue() {
        return new Queue("text_message_subscribe");
    }
    @Bean
    public Queue textAnswerSubscribeMessageQueue() {
        return new Queue("answer_message_subscribe");
    }
    @Bean
    public Queue textSubscribeActionQueue() {
        return new Queue("text_action_subscribe");
    }
    @Bean
    public Queue textAnswerSubscribeActionQueue() {
        return new Queue("answer_action_subscribe");
    }
}
