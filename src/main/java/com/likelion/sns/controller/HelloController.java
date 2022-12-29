package com.likelion.sns.controller;

import com.likelion.sns.service.AlgorithmService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/hello")
public class HelloController {
    private final AlgorithmService algorithmService;

    public HelloController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @GetMapping("")
    public String hello(){
        return "김민지";
    }
    @GetMapping("/{num}")
    public String sumOfDigits(@PathVariable Integer num){
        int sum= algorithmService.sumOfDigits(num);
        return String.valueOf(sum);
    }
}
