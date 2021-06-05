package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTests {
    private OrderController orderController;

    private OrderRepository orderRepository = mock(OrderRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    private Cart cart;

    private User user;

    private Item item;

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);

        user = createUser();
        item = createItem();
        cart = createCart(item,user);
        user.setCart(cart);
    }

    @Test
    public void submit_order_happy_path(){
        when(userRepository.findByUsername("test")).thenReturn(user);
        ResponseEntity<UserOrder> responseEntity = orderController.submit(user.getUsername());
        assertEquals(200,responseEntity.getStatusCodeValue());
        assertNotNull(responseEntity);
        assertEquals(BigDecimal.valueOf(30),responseEntity.getBody().getTotal());
    }

    @Test
    public void get_order_for_user_happy_path(){
        when(userRepository.findByUsername("test")).thenReturn(user);
        List<UserOrder> orders = new ArrayList<>();
        ResponseEntity<UserOrder> newOrder = orderController.submit(user.getUsername());
        orders.add(newOrder.getBody());
        when(orderRepository.findByUser(user)).thenReturn(orders);

        ResponseEntity<List<UserOrder>> responseEntity = orderController.getOrdersForUser("test");
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(1,responseEntity.getBody().size());
    }

    private User createUser(){
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(null);
        cart.setItems(new ArrayList<Item>());
        cart.setTotal(BigDecimal.valueOf(0.0));
        User user = new User();
        user.setId(1);
        user.setUsername("test");
        user.setPassword("Password");
        user.setCart(cart);

        return user;
    }

    private Item createItem(){
        Item item = new Item();
        item.setId(1L);
        item.setName("Test item");
        item.setDescription("This is test item");
        item.setPrice(BigDecimal.valueOf(10));
        return item;
    }

    private Cart createCart(Item item, User user){
        Cart cart = new Cart();
        cart.setId(0L);
        cart.addItem(item);
        cart.addItem(item);
        cart.addItem(item);
        cart.setUser(user);
        return cart;
    }
}
