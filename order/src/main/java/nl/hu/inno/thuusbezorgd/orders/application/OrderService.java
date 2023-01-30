package nl.hu.inno.thuusbezorgd.orders.application;


import common.Address;
import common.dto.DishDTO;
import common.User;
import common.DishList;
import common.exception.ServiceDownException;
import common.messages.*;
import nl.hu.inno.thuusbezorgd.orders.data.OrderRepository;
import nl.hu.inno.thuusbezorgd.orders.domain.Order;
import nl.hu.inno.thuusbezorgd.orders.infrastructure.OrderPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {
    private OrderPublisher publisher;
    private OrderRepository repository;

    public void placeOrder(User user, DishList order, Address address){
        StockCheckRequest request = new StockCheckRequest(user, order, address);
        publisher.checkStock(request);
    }

    @RabbitListener(queues = "orderQueue")
    public void orderValidated(PlaceOrderCommand command){
        this.repository.save(new Order(command.getUser(), command.getDishList(), LocalDateTime.now()));
        List<String> stringList = new ArrayList<>();
        for(DishDTO dto : command.getDishList()){
            stringList.add(dto.getName());
        }
        DishList list = new DishList(stringList);
        AddDeliveryCommand newCommand = new AddDeliveryCommand(command.getUser(),command.getAddress(), list);
        this.publisher.deliver(newCommand);
    }

}
