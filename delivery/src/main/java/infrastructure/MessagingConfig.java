package infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;


@Configuration
public class MessagingConfig {
    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
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
