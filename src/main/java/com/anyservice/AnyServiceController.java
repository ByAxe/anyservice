package com.anyservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AnyServiceController {

    @Value("${TARGET:World}")
    private String target;

    @GetMapping
    private String hello() {
        return "Hello " + target + "!";
    }
}
