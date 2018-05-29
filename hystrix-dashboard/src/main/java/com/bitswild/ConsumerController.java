package com.bitswild;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

/**
 * 调用提供者的home方法
 */
public class ConsumerController {
    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "defaultStores")
    @GetMapping(value = "/home")
    public String home() {
        return restTemplate.getForEntity("http://eureka-provider/", String.class).getBody();
    }

    public String defaultStores() {
        return "Ribbon + hystrix Dashboard ,提供者服务挂了";
    }
}