package com.example.myservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import java.util.List;
import java.util.Map;

@RestController
public class MyServiceRest {

    @Value("${service2.url:http://myservice2:8081}")
    private String service2Url;

    @GetMapping("/")
    public String sayHello() {
        return "Hello from Service 1!";
    }

    @GetMapping("/data")
    public List<Map<String, Object>> getData() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            service2Url + "/data",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        return response.getBody();
    }
}