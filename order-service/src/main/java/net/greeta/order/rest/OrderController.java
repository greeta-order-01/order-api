package net.greeta.order.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.greeta.order.mapper.OrderMapper;
import net.greeta.order.model.Order;
import net.greeta.order.model.User;
import net.greeta.order.rest.dto.CreateOrderRequest;
import net.greeta.order.rest.dto.OrderDto;
import net.greeta.order.service.OrderService;
import net.greeta.order.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class OrderController {

    private final UserService userService;
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @GetMapping
    public List<OrderDto> getOrders(@RequestParam(value = "text", required = false) String text) {
        List<Order> orders = (text == null) ? orderService.getOrders() : orderService.getOrdersContainingText(text);
        return orders.stream()
                .map(orderMapper::toOrderDto)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderDto createOrder(@Valid @RequestBody CreateOrderRequest createOrderRequest,
                                JwtAuthenticationToken token) {
        User user = userService.validateAndGetUserByUsername(token.getName());
        Order order = orderMapper.toOrder(createOrderRequest);
        order.setId(UUID.randomUUID().toString());
        order.setUser(user);
        return orderMapper.toOrderDto(orderService.saveOrder(order));
    }

    @DeleteMapping("/{id}")
    public OrderDto deleteOrders(@PathVariable UUID id) {
        Order order = orderService.validateAndGetOrder(id.toString());
        orderService.deleteOrder(order);
        return orderMapper.toOrderDto(order);
    }
}
