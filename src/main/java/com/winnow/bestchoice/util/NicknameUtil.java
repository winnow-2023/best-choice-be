package com.winnow.bestchoice.util;



import java.util.Random;
import java.util.UUID;

public class NicknameUtil {

    private static final String[] NICKNAME_PREFIX =
            {"지리산", "설악산", "한라산", "금강산", "태백산", "오대산", "덕유산", "가야산", "소백산", "도봉산",
            "백두산", "치악산", "계룡산", "무등산", "대둔산", "북한산", "청계산", "속리산", "주왕산", "내장산",
            "금오산", "구병산", "칠보산", "용봉산", "월악산", "두타산"};


    public static String generateNickname() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        int index = random.nextInt(NICKNAME_PREFIX.length);
        String prefix = NICKNAME_PREFIX[index];
        int middle = random.nextInt(10000 + 1);
        String suffix = UUID.randomUUID().toString().substring(0, 4);

        return sb.append(prefix).append(middle).append(suffix).toString();
    }

}