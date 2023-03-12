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
    public Queue orderInfoQueue() {
        return new Queue("order_info");
    }
    @Bean
    public Queue answerOrderInfoQueue() {return new Queue("answer_order_info");}
    @Bean
    public Queue subscribeQueue() {
        return new Queue("subscribe");
    }
    @Bean
    public Queue answerSubscribeQueue() {
        return new Queue("answer_subscribe");
    }
    @Bean
    public Queue createSubscribeQueue() {
        return new Queue("create_subscribe");
    }
    @Bean
    public Queue answerCreateSubscribeQueue() {
        return new Queue("answer_create_subscribe");
    }
    @Bean
    public Queue exchangeQueue() {
        return new Queue("exchange");
    }
    @Bean
    public Queue answerExchangeQueue() {
        return new Queue("answer_exchange");
    }
    @Bean
    public Queue paymentSystemQueue() {
        return new Queue("payment_system");
    }
    @Bean
    public Queue answerPaymentSystemQueue() {
        return new Queue("answer_payment_system");
    }
    @Bean
    public Queue limitQueue() {
        return new Queue("limit");
    }
    @Bean
    public Queue answerLimitQueue() {
        return new Queue("answer_limit");
    }
    @Bean
    public Queue registerUserQueue() {
        return new Queue("register_user");
    }
    @Bean
    public Queue answerRegisterUserQueue() {
        return new Queue("answer_register_user");
    }
}
