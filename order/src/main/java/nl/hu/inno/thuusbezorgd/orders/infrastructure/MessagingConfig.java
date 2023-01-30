package nl.hu.inno.thuusbezorgd.orders.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;


@Configuration
public class MessagingConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("orderBindingKey")
    private String orderKey;
    @Value("stockBindingKey")
    private String stockKey;
    @Value("menuBindingKey")
    private String menuKey;

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
    public Queue orderQueue(){
        return QueueBuilder.durable("orderQueue").build();
    }
    @Bean
    public Queue menuQueue(){
        return QueueBuilder.durable("menuQueue").build();
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
    public RabbitTemplate template(Jackson2JsonMessageConverter converter){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter converter(Jackson2ObjectMapperBuilder builder) {
        // We need to configure a message converter to be used by RabbitTemplate.
        // We could use any format, but we'll use JSON so that it is easier to inspect.
        ObjectMapper objectMapper = builder
                .createXmlMapper(false)
                .build();

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        // Set this in order to prevent deserialization using the sender-specific
        // __TYPEID__ in the message header.
        converter.setAlwaysConvertToInferredType(true);

        return converter;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new CachingConnectionFactory(host, port);
    }
}
