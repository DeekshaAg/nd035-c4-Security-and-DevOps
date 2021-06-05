package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTests {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private List<Item> items = new ArrayList<>();

    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
        items.add(createItem(1L,"Test1"));
        items.add(createItem(2L,"Test2"));
        items.add(createItem(3L,"Test3"));
    }

    @Test
    public void find_by_id_happy_path(){
        when(itemRepository.findById(1L)).thenReturn(Optional.of(createItem(1L,"Test1")));
        ResponseEntity<Item> responseEntity = itemController.getItemById(1L);
        assertEquals(200,responseEntity.getStatusCodeValue());
        assertNotNull(responseEntity.getBody());
        assertEquals("Test1", responseEntity.getBody().getName());
    }

    @Test
    public void find_all_items_happy_path(){
        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> responseEntity = itemController.getItems();
        assertNotNull(responseEntity);
        assertEquals(200,responseEntity.getStatusCodeValue());
        assertEquals(3,responseEntity.getBody().size());
    }

    @Test
    public void find_items_by_name_happy_path(){
        when(itemRepository.findByName("test")).thenReturn(items);
        ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("test");
        assertEquals(200,responseEntity.getStatusCodeValue());
        assertNotNull(responseEntity);
        assertEquals("Test1", responseEntity.getBody().get(0).getName());
        assertEquals("Test2", responseEntity.getBody().get(1).getName());
        assertEquals("Test3", responseEntity.getBody().get(2).getName());
        assertEquals(3,responseEntity.getBody().size());
    }

    private Item createItem(Long id, String name){
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription("This is test item");
        item.setPrice(BigDecimal.valueOf(10));
        return item;
    }
}
