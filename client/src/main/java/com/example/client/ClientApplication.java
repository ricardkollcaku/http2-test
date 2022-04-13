package com.example.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

@SpringBootApplication
public class ClientApplication implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(ClientApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    WebClient webClient = WebClient.create().mutate()
        .clientConnector(
            new ReactorClientHttpConnector(
                HttpClient.create()
//remove to make it http1
                    .protocol(HttpProtocol.H2C)
            ))
        .baseUrl("http://localhost:8080/sms-request/test").build();

    Flux.range(1, 100000000)
        .flatMap(integer -> webClient.get().retrieve().bodyToMono(String.class))
        .subscribe()
    ;
  }
}
