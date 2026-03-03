package com.example.myservice2;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/data")
public class MyService2Rest {

    private final ItemRepository itemRepository;

    public MyService2Rest(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public List<Item> getData() {
        // Seed data if empty
        if (itemRepository.count() == 0) {
            itemRepository.save(new Item("item-1"));
            itemRepository.save(new Item("item-2"));
            itemRepository.save(new Item("item-3"));
        }
        return itemRepository.findAll();
    }

    @PostMapping
    public Item addItem(@RequestBody Item item) {
        return itemRepository.save(item);
    }
}