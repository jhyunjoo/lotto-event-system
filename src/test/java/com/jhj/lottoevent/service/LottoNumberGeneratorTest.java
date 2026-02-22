package com.jhj.lottoevent.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LottoNumberGeneratorTest {

    private final LottoNumberGenerator generator = new LottoNumberGenerator();
    private final String winning = "123456";

    @Test
    void firstPrize_shouldMatch6Digits() {
        String number = generator.generate(winning, (byte) 1);
        assertThat(number).isEqualTo(winning);
    }

    @Test
    void secondPrize_shouldMatchLast5Digits() {
        String number = generator.generate(winning, (byte) 2);

        assertThat(number.substring(1))
                .isEqualTo(winning.substring(1));

        assertThat(number.charAt(0))
                .isNotEqualTo(winning.charAt(0));
    }

    @Test
    void thirdPrize_shouldMatchLast4Digits_only() {
        String number = generator.generate(winning, (byte) 3);

        assertThat(number.substring(2))
                .isEqualTo(winning.substring(2));

        // 5자리 이상 동일하면 안됨
        assertThat(number.substring(1))
                .isNotEqualTo(winning.substring(1));
    }

    @Test
    void fourthPrize_shouldMatchLast3Digits_only() {
        String number = generator.generate(winning, (byte) 4);

        assertThat(number.substring(3))
                .isEqualTo(winning.substring(3));

        // 4자리 이상 동일하면 안됨
        assertThat(number.substring(2))
                .isNotEqualTo(winning.substring(2));
    }

    @Test
    void nonWinning_shouldNotMatchLast3Digits() {
        String number = generator.generate(winning, (byte) 0);

        assertThat(number.substring(3))
                .isNotEqualTo(winning.substring(3));
    }
}