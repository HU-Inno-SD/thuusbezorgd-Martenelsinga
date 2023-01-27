package nl.hu.inno.thuusbezorgd.orders.infrastructure;

import common.User;
import common.DishList;
import common.requests.addDeliveryCommand;
import common.requests.stockCheckRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderPublisher {
    @Autowired
    private RabbitTemplate template;
    public void checkStock(stockCheckRequest request){
        template.convertAndSend("topicExchange", "stockKey", request);
    }

    public void deliver(addDeliveryCommand command){
        template.convertAndSend("topicExchange", "deliveryKey", command);
    }
}
