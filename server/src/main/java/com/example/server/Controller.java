package com.example.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class Controller {

  @Autowired
  BenchmarkProvider benchmarkProvider;

  @GetMapping("/sms-request/{s}")
  Mono<String> requestResponse(@PathVariable("s") String s) {
    benchmarkProvider.incrementProcessed();
    return Mono.just("test :" + s);
  }

}
