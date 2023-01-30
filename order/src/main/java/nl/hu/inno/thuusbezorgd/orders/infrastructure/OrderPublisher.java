package nl.hu.inno.thuusbezorgd.orders.infrastructure;

import common.messages.Ack;
import common.messages.AddDeliveryCommand;
import common.messages.RetractStockCommand;
import common.messages.StockCheckRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderPublisher {
    @Autowired
    private RabbitTemplate template;
    public void checkStock(StockCheckRequest request){
        template.convertAndSend("topicExchange", "stockKey", request);
    }

    public void deliver(AddDeliveryCommand command){
        template.convertAndSend("topicExchange", "deliveryKey", command);
    }

    public void ack(Ack ack){
        template.convertAndSend("topicExchange", "ackKey", ack);
    }
}
