package com.example.techtask.service.impl;

import com.example.techtask.model.Order;
import com.example.techtask.model.User;
import com.example.techtask.model.enumiration.OrderStatus;
import com.example.techtask.repository.OrderRepository;
import com.example.techtask.repository.UserRepository;
import com.example.techtask.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    //    1. Возвращать пользователя с максимальной общей суммой товаров, доставленных в 2003.
    @Override
    public User findUser() {
        final int REQUIRED_YEAR = 2003;
        List<Order> orders = orderRepository.findAll();
        Map.Entry<Integer, Double> maxUserEntry = orders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.DELIVERED && o.getCreatedAt().getYear() == REQUIRED_YEAR)
                .collect(Collectors.groupingBy(
                        Order::getUserId,
                        Collectors.summingDouble(o -> o.getPrice() * o.getQuantity())
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow(() -> new RuntimeException("No order found"));
        return userRepository.findById(maxUserEntry.getKey()).orElseThrow(() -> new RuntimeException("No user found"));
    }

    //    2. Возвращать пользователей у которых есть оплаченные заказы в 2010.
    @Override
    public List<User> findUsers() {
        final int REQUIRED_YEAR = 2010;
        List<Order> orders = orderRepository.findAll();
        List<Integer> ids = orders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.PAID && o.getCreatedAt().getYear() == REQUIRED_YEAR)
                .map(Order::getUserId)
                .toList();
        return userRepository.findAllById(ids);
    }
}
