package com.likelion.sns.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlgorithmServiceTest {
    AlgorithmService algorithmService = new AlgorithmService();

    @Test
    @DisplayName("자릿수 합 잘 구하는지")
    void sumOfDigit() {
        assertEquals(21, algorithmService.sumOfDigits(687));
        assertEquals(22, algorithmService.sumOfDigits(787));
        assertEquals(0, algorithmService.sumOfDigits(0));
        assertEquals(5, algorithmService.sumOfDigits(11111));

    }
}