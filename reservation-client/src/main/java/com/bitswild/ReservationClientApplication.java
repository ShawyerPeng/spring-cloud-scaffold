package com.bitswild;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.hateoas.Resources;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@EnableZuulProxy
@EnableEurekaClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableBinding(Source.class)
@SpringBootApplication
public class ReservationClientApplication {
	public static void main(String[] args) {
		SpringApplication.run(ReservationClientApplication.class, args);
	}
}

@FeignClient("reservation-service")
interface ReservationRead {
    @RequestMapping(method = RequestMethod.GET, value = "/reservations")
    Resources<Reservation> read();
}

@RestController
class ResearvationController {
    private final ReservationRead reservationRead;
    private final MessageChannel messageChannel;

    @Autowired
    public ResearvationController(ReservationRead reservationRead, @Qualifier(Source.OUTPUT) MessageChannel messageChannel) {
        this.reservationRead = reservationRead;
        this.messageChannel = messageChannel;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reservations/write")
    public void write(@RequestBody Reservation reservation) {
        messageChannel.send(MessageBuilder.withPayload(reservation.getReservationName()).build());
    }

    public Collection<String> fallback() {
        List<String> list = new ArrayList<>();
        list.add("熔断机制启用");
        return list;
    }

    @HystrixCommand(fallbackMethod = "fallback")
    @RequestMapping(method = RequestMethod.GET, value = "/reservations/names")
    public Collection<String> read() {
        return this.reservationRead.read()
                .getContent()
                .stream()
                .map(Reservation::getReservationName)
                .collect(Collectors.toList());
    }
}

class Reservation {
    private String reservationName;

    public String getReservationName() {
        return reservationName;
    }

    public void setReservationName(String reservationName) {
        this.reservationName = reservationName;
    }
}