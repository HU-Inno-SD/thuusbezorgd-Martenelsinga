package stock.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;


@Configuration
public class MessagingConfig {

    @Value("localhost")
    private String host;

    @Value("5672")
    private int port;
    @Value("stockBindingKey")
    private String stockKey;
    @Value("menuBindingKey")
    private String menuKey;

    @Value("orderBindingKey")
    private String orderKey;
    @Value("AckBindingKey")
    private String ackKey;

    @Bean
    public Queue ackQueue(){
        return QueueBuilder.durable("ackQueue").build();
    }
    @Bean
    public Queue stockQueue(){
        return QueueBuilder.durable("stockQueue").build();
    }
    @Bean
    public Queue menuQueue(){
        return QueueBuilder.durable("menuQueue").build();
    }

    @Bean
    public Queue orderQueue(){
        return QueueBuilder.durable("orderQueue").build();
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange("topicExchange");
    }
    @Bean
    public Binding stockBinding(){
        return BindingBuilder.bind(stockQueue()).to(topicExchange()).with(stockKey);
    }
    @Bean
    public Binding orderBinding(){
        return BindingBuilder.bind(orderQueue()).to(topicExchange()).with(orderKey);
    }
    @Bean
    public Binding menuBinding(){
        return BindingBuilder.bind(menuQueue()).to(topicExchange()).with(menuKey);
    }
    @Bean
    public Binding ackBinding(){
        return BindingBuilder.bind(ackQueue()).to(topicExchange()).with(ackKey);
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
