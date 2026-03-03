package com.example.myservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import java.util.List;

@RestController
public class MyServiceRest {

    // In Kubernetes, services are reachable by their service name
    @Value("${service2.url:http://myservice2:8081}")
    private String service2Url;

    @GetMapping("/")
    public String sayHello() {
        return "Hello from Service 1!";
    }

    @GetMapping("/data")
    public List<String> getData() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<String>> response = restTemplate.exchange(
            service2Url + "/data",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<String>>() {}
        );
        return response.getBody();
    }
}