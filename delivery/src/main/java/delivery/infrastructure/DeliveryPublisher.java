package delivery.infrastructure;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

public class DeliveryPublisher {
    @Autowired
    private RabbitTemplate template;

    public DeliveryPublisher() {
        ConnectionFactory factory = new CachingConnectionFactory("localhost", 5672);
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        this.template = template;
    }

    public void publish(){
        String s = new String("aap");
        template.convertAndSend("topicExchange", "deliveryBindingKey", s);
    }
}
