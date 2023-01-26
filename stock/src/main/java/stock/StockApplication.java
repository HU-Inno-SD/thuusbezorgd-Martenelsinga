package stock;

import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import stock.application.StockService;
import stock.data.IngredientRepository;

@SpringBootApplication
public class StockApplication {
    @Autowired
    private IngredientRepository ingredientRepository;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(StockApplication.class, args);
        StockService service = new StockService();
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection(new IngredientRepository());
        Channel channel = connection.createChannel();
        channel.queueDeclare("stock-check", false, false, false, null);

        DeliverCallback callback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            try {
                service.checkStock();
            } finally {
                System.out.println(" [x] Done");
            }
        };
        channel.basicConsume("stock-check", true, callback, consumerTag -> {});
    }
}
