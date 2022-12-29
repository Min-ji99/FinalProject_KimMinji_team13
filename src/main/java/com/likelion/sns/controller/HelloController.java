package com.likelion.sns.controller;

import com.likelion.sns.service.HelloService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hello")
public class HelloController {
    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("")
    public String hello(){
        return "김민지";
    }
    @GetMapping("/{num}")
    public String sumOfDigits(@PathVariable Integer num){
        int sum=helloService.sumOfDigits(num);
        return String.valueOf(sum);
    }
}
