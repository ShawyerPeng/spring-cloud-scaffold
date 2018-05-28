package com.bitswild;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Optional;
import java.util.stream.Stream;

@EnableEurekaClient
@EnableBinding(Sink.class)
@SpringBootApplication
public class ReservationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReservationServiceApplication.class, args);
    }

    @Bean
    ApplicationRunner sampleData(ReservationRepository reservationRepository) {
        return arg -> {
            Stream.of("wangliang", "baiju", "jinlu", "tale", "guchi", "血龙木")
                    .forEach(name -> reservationRepository.save(new Reservation(null, name)));
            reservationRepository.findAll().forEach(System.out::println);
        };
    }
}

@Component
class ReservationConsumer {
    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationConsumer(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @StreamListener(Sink.INPUT)
    public void write(String name) {
        reservationRepository.save(new Reservation(null, name));
    }
}

@RestController
@RefreshScope
class MessageController {
    private final String value;

    public MessageController(@Value("${message}") String value) {
        this.value = value;
    }

    @RequestMapping("/message")
    public String read() {
        return value;
    }
}

@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @RestResource(path = "by-name")
    Optional<Reservation> findByReservationName(@Param("name") String name);
}

@Entity
class Reservation {
    @Id
    @GeneratedValue
    private Long id;
    private String reservationName;

    public Reservation() {
    }

    public Reservation(Long id, String reservationName) {
        this.id = id;
        this.reservationName = reservationName;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReservationName() {
        return reservationName;
    }

    public void setReservationName(String reservationName) {
        this.reservationName = reservationName;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", reservationName='" + reservationName + '\'' +
                '}';
    }
}