package infrastructure;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class DeliveryPublisher {
    @Autowired
    private RabbitTemplate template;


}
