package stock.infrastructure;

import common.messages.PlaceOrderCommand;
import common.messages.RandomStockCheckReply;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

public class StockPublisher {

    @Autowired
    private RabbitTemplate template;

    public StockPublisher() {
        ConnectionFactory factory = new CachingConnectionFactory("localhost", 5672);
        RabbitTemplate template = new RabbitTemplate(factory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        this.template = template;
    }

    public void throwError(String message){
        template.convertAndSend("topicExchange", "orderBindingKey", message);
    }

    public void returnOrderCommand(PlaceOrderCommand command){
        template.convertAndSend("topicExchange", "orderBindingKey", command);
    }

    public void stockCheckReply(RandomStockCheckReply reply){
        template.convertAndSend("topicExchange", "orderBindingKey", reply);
    }
}
