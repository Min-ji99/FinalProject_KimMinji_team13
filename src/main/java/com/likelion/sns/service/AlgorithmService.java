package com.likelion.sns.service;

import org.springframework.stereotype.Service;

@Service
public class AlgorithmService {
    public int sumOfDigits(Integer num) {
        int sum=0;
        while(num>0){
            sum+=num%10;
            num=num/10;
        }

        return sum;
    }
}
