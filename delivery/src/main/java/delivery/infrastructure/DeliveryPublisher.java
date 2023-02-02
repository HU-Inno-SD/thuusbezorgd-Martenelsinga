package delivery.infrastructure;

import common.messages.ConfirmDeliveryCommand;
import common.messages.DeliveryError;
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

    public void confirmDelivery(ConfirmDeliveryCommand command){
        template.convertAndSend("topicExchange", "orderBindingKey", command);
    }

    public void throwError(DeliveryError error){
        template.convertAndSend("topicExchange", "orderBindingKey", error);
    }
}
