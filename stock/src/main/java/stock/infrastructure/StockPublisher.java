package stock.infrastructure;

import common.requests.placeOrderCommand;
import common.requests.stockCheckRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class StockPublisher {

    @Autowired
    private RabbitTemplate template;

    public void returnOrderCommand(placeOrderCommand command){
        template.convertAndSend("topicExchange", "orderKey", command);
    }
}
