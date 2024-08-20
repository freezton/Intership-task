package com.example.techtask.service.impl;

import com.example.techtask.model.Order;
import com.example.techtask.model.User;
import com.example.techtask.model.enumiration.UserStatus;
import com.example.techtask.repository.OrderRepository;
import com.example.techtask.repository.UserRepository;
import com.example.techtask.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final UserRepository userRepository;

    //    3. Возвращать самый новый заказ, в котором больше одного предмета.
    @Override
    public Order findOrder() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .filter(o -> o.getQuantity() > 1)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    //    4. Возвращать заказы от активных пользователей, отсортированные по дате создания.
    @Override
    public List<Order> findOrders() {
        Set<Integer> activeUserIds = userRepository.findAll().stream()
                .filter(u -> u.getUserStatus() == UserStatus.ACTIVE)
                .map(User::getId)
                .collect(Collectors.toSet());
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .filter(o -> activeUserIds.contains(o.getUserId()))
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .toList();
    }
}
