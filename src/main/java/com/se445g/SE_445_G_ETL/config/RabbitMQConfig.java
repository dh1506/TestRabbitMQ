package com.se445g.SE_445_G_ETL.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Tên queue, exchange, routing key
    public static final String EXCHANGE_NAME = "etl_exchange";

    //employees
    public static final String EMPLOYEES_QUEUE = "employees_queue";
    public static final String EMPLOYEES_ROUTING_KEY = "employees_routing_key";
    
    //performance
    public static final String PERFORMANCE_QUEUE = "performance_queue";
    public static final String PERFORMANCE_ROUTING_KEY = "performance_routing_key";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue employeesQueue() {
        return new Queue(EMPLOYEES_QUEUE, true);
    }
    
    @Bean
    public Queue performanceQueue() {
        return new Queue(PERFORMANCE_QUEUE, true);
    }

    @Bean
    public Binding employeesBinding(Queue employeesQueue, TopicExchange exchange) {
        return BindingBuilder.bind(employeesQueue).to(exchange).with(EMPLOYEES_ROUTING_KEY);
    }
    
    @Bean
    public Binding performanceBinding(Queue performanceQueue, TopicExchange exchange) {
        return BindingBuilder.bind(performanceQueue).to(exchange).with(PERFORMANCE_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template; 
    }
}
