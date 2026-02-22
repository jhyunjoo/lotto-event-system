package com.jhj.lottoevent.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class LottoNumberGenerator {

    private final SecureRandom random = new SecureRandom();

    /**
     * suffix 기준으로 일치 자리 수를 보장한다.
     * 1등: 6자리 동일
     * 2등: 뒤 5자리 동일 (맨 앞 1자리만 다름)
     * 3등: 뒤 4자리 동일 (5/6자리 동일 금지)
     * 4등: 뒤 3자리 동일 (4/5/6자리 동일 금지)
     * 0등: 뒤 3자리 동일 금지
     */
    public String generate(String winning, byte rank) {
        if (winning == null || winning.length() != 6) {
            throw new IllegalArgumentException("winning_number must be 6 digits");
        }

        return switch (rank) {
            case 1 -> winning;
            case 2 -> suffixSameButDifferentHead(winning, 5);
            case 3 -> suffixSameButNotMore(winning, 4); // 5/6자리 동일 차단
            case 4 -> suffixSameButNotMore(winning, 3); // 4/5/6자리 동일 차단
            default -> nonWinning(winning);
        };
    }

    // 뒤 suffixLen 자리 동일 + 그 앞(0번 자리)은 반드시 다르게
    private String suffixSameButDifferentHead(String winning, int suffixLen) {
        char[] out = new char[6];

        // suffix 복사
        int start = 6 - suffixLen;
        for (int i = start; i < 6; i++) {
            out[i] = winning.charAt(i);
        }

        // 앞부분 랜덤
        for (int i = 0; i < start; i++) {
            out[i] = randomDigit();
        }

        // 맨 앞(0번)을 winning과 다르게 보장 (2등이면 start=1이라 딱 0만 해당)
        if (out[0] == winning.charAt(0)) {
            out[0] = differentDigit(winning.charAt(0));
        }

        // 혹시 전체 동일이면 재생성
        String s = new String(out);
        if (s.equals(winning)) return suffixSameButDifferentHead(winning, suffixLen);
        return s;
    }

    // 뒤 suffixLen 자리 동일은 보장하되, suffixLen+1 이상 동일(더 높은 등수) 나오면 안 됨
    private String suffixSameButNotMore(String winning, int suffixLen) {
        char[] out = new char[6];

        int start = 6 - suffixLen;      // suffix 시작 인덱스
        int blockIdx = start - 1;       // 더 높은 등수 방지용(바로 앞 자리)

        // suffix 복사
        for (int i = start; i < 6; i++) {
            out[i] = winning.charAt(i);
        }

        // blockIdx는 winning과 다르게 만들어서 "suffixLen+1 동일" 차단
        out[blockIdx] = differentDigit(winning.charAt(blockIdx));

        // 그 앞부분은 랜덤
        for (int i = 0; i < blockIdx; i++) {
            out[i] = randomDigit();
        }

        return new String(out);
    }

    // 꽝: 뒤 3자리 동일(4등) 자체를 피하려면 마지막 3자리 중 하나를 다르게 만들면 됨
    private String nonWinning(String winning) {
        char[] out = new char[6];

        // 전체 랜덤
        for (int i = 0; i < 6; i++) {
            out[i] = randomDigit();
        }

        // 마지막 3자리 중 하나는 winning과 다르게 보장(여기서는 마지막 자리)
        int idx = 5;
        if (out[idx] == winning.charAt(idx)) {
            out[idx] = differentDigit(winning.charAt(idx));
        }

        // 혹시 우연히 뒤 3자리 모두 같아졌을 수도 있으니(확률은 낮지만) 확실히 한 자리 더 깨기
        // 마지막 3자리 모두 winning과 같으면 중간 자리(4)도 깨기
        if (out[3] == winning.charAt(3) && out[4] == winning.charAt(4) && out[5] == winning.charAt(5)) {
            out[4] = differentDigit(winning.charAt(4));
        }

        return new String(out);
    }

    private char randomDigit() {
        return (char) ('0' + random.nextInt(10));
    }

    private char differentDigit(char original) {
        char d;
        do {
            d = randomDigit();
        } while (d == original);
        return d;
    }
}