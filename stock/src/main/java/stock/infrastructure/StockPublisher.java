package stock.infrastructure;

import common.messages.PlaceOrderCommand;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class StockPublisher {

    @Autowired
    private RabbitTemplate template;

    public void returnOrderCommand(PlaceOrderCommand command){
        template.convertAndSend("topicExchange", "orderKey", command);
    }
}
