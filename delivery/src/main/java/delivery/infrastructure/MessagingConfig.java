package delivery.infrastructure;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MessagingConfig {
    @Value("localhost")
    private String host;

    @Value("5672")
    private int port;

    @Value("AckBindingKey")
    private String ackKey;

    @Value("orderBindingKey")
    private String orderKey;

    @Value("deliveryBindingKey")
    private String deliveryKey;
    @Bean
    public Queue ackQueue(){
        return QueueBuilder.durable("ackQueue").build();
    }

    @Bean
    public Queue deliveryQueue(){return QueueBuilder.durable("deliveryQueue").build();}

    @Bean
    public Queue orderQueue(){
        return QueueBuilder.durable("orderQueue").build();
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange("topicExchange");
    }

    @Bean
    public Binding ackBinding(){
        return BindingBuilder.bind(ackQueue()).to(topicExchange()).with(ackKey);
    }

    @Bean
    public Binding deliveryBinding(){return BindingBuilder.bind(deliveryQueue()).to(topicExchange()).with(deliveryKey);}

    @Bean
    public Binding orderBinding(){
        return BindingBuilder.bind(orderQueue()).to(topicExchange()).with(orderKey);
    }

    @Bean
    public RabbitTemplate template(){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(host, port);
    }
}
