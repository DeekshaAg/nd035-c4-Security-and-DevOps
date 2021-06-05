package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTests {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController =  new UserController();
        TestUtils.injectObjects(userController,"userRepository", userRepository);
        TestUtils.injectObjects(userController,"cartRepository", cartRepository);
        TestUtils.injectObjects(userController,"bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() throws Exception{
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest newUser = new CreateUserRequest();
        newUser.setUsername("test");
        newUser.setPassword("testPassword");
        newUser.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(newUser);
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void find_by_id(){
        User newUser = createUser();
        when(userRepository.findById(1L)).thenReturn(Optional.of(newUser));
        ResponseEntity<User> response = userController.findById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User foundUser = response.getBody();

        assertNotNull(foundUser);
        assertEquals(1, foundUser.getId());
        assertEquals("test", foundUser.getUsername());
    }

    @Test
    public void find_by_user_name(){
        User newUser = createUser();
        when(userRepository.findByUsername("test")).thenReturn(newUser);
        ResponseEntity<User> response = userController.findByUserName("test");
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());
        assertNotNull(newUser);
        assertEquals(1, response.getBody().getId());
        assertEquals("test", response.getBody().getUsername());
        assertEquals(newUser,response.getBody());
    }

    private User createUser(){
        User user = new User();
        user.setId(1);
        user.setUsername("test");
        user.setPassword("Password");
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(null);
        cart.setItems(new ArrayList<Item>());
        cart.setTotal(BigDecimal.valueOf(0.0));
        user.setCart(cart);
        return user;
    }

}
