package com.jamdam.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/backend")
    public String backend(){
        return "Backend Response";
    }
}
