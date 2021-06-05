package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTests {

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    private ModifyCartRequest modifyCartRequest = mock(ModifyCartRequest.class);

    @Before
    public void setup(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository",itemRepository);
    }

    @Test
    public void add_to_cart_happy_path() {
        User user = createUser();
        Item item = createItem();
        Cart cart = user.getCart();
        cart.addItem(item);
        cart.setUser(user);
        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("test");
        cartRequest.setItemId(1);
        cartRequest.setQuantity(1);
        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);

        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());
        assertEquals("test",response.getBody().getUser().getUsername());
        assertEquals(2,response.getBody().getItems().size());
    }

    @Test
    public void add_to_cart_id_not_found(){
        modifyCartRequest.setItemId(1L);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void add_to_cart_user_not_found(){
        modifyCartRequest.setUsername("user");
        ResponseEntity<Cart> responseEntity = cartController.addTocart(modifyCartRequest);
        assertEquals(404,responseEntity.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_happy_path(){
        User user = createUser();
        Item item = createItem();
        Cart cart = user.getCart();
        cart.addItem(item);
        cart.setUser(user);
        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setUsername("test");
        cartRequest.setItemId(1);
        cartRequest.setQuantity(1);
        ResponseEntity<Cart> response = cartController.removeFromcart(cartRequest);

        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());
        assertEquals("test",response.getBody().getUser().getUsername());
        assertEquals(0,response.getBody().getItems().size());
    }

    private Cart createCart(){
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(null);
        cart.setItems(new ArrayList<Item>());
        cart.setTotal(BigDecimal.valueOf(0.0));
        return cart;
    }

    private User createUser(){
        User user = new User();
        user.setId(1);
        user.setUsername("test");
        user.setPassword("Password");
        user.setCart(createCart());
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

}
