package net.greeta.order.mapper;

import net.greeta.order.model.Order;
import net.greeta.order.rest.dto.CreateOrderRequest;
import net.greeta.order.rest.dto.OrderDto;

public interface OrderMapper {

    Order toOrder(CreateOrderRequest createOrderRequest);

    OrderDto toOrderDto(Order order);
}