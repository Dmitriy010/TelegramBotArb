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
        return new Queue("text_message_order_info");
    }
    @Bean
    public Queue textAnswerMessageQueue() {return new Queue("answer_order_info_message");}
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
    @Bean
    public Queue textUserActionExchangeQueue() {
        return new Queue("text_action_user_exchange");
    }
    @Bean
    public Queue textAnswerUserActionExchangeQueue() {
        return new Queue("answer_action_user_exchange");
    }
    @Bean
    public Queue textUserActionPaymentSystemQueue() {
        return new Queue("text_action_user_payment_system");
    }
    @Bean
    public Queue textAnswerUserActionPaymentSystemQueue() {
        return new Queue("answer_action_user_payment_system");
    }
    @Bean
    public Queue textUserRegisterQueue() {
        return new Queue("text_register_user");
    }
    @Bean
    public Queue textAnswerUserRegisterQueue() {
        return new Queue("answer_register_user");
    }
}
