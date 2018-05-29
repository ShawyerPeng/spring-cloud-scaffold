package com.bitswild;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 调用提供者的home方法
 */
@RestController
public class ConsumerController {
    @Autowired
    private HomeClient homeClient;

    @GetMapping(value = "/home")
    public String home() {
        return  homeClient.consumer();
    }
}