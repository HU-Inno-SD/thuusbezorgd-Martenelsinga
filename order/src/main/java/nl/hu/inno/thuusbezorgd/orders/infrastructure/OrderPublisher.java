package nl.hu.inno.thuusbezorgd.orders.infrastructure;

import common.messages.AddDeliveryCommand;
import common.messages.RandomStockCheck;
import common.messages.StockCheckRequest;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class OrderPublisher {
    @Autowired
    private RabbitTemplate template;

    public OrderPublisher() {
        ConnectionFactory factory = new CachingConnectionFactory("localhost", 5672);
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        this.template = template;

    }

    public void checkStock(StockCheckRequest request){
        template.convertAndSend("topicExchange", "stockBindingKey", request);
    }

    public void deliver(AddDeliveryCommand command){
        template.convertAndSend("topicExchange", "deliveryBindingKey", command);
    }

    public String ack(){
        template.convertSendAndReceive("topicExchange", "deliveryBindingKey", "ack");
        template.convertSendAndReceive("topicExchange", "stockBindingKey", "ack");
        return "hello";
    }

    public void randomStockCheck(RandomStockCheck check){
        template.convertAndSend("topicExchange", "stockBindingKey", check);
    }
}
