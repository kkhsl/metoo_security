package com.metoo.nspm.core.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CommUtil {

    public static final String randomString(int length) {
        char[] numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
                + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
        if (length < 1) {
            return "";
        }
        Random randGen = new Random();
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
        }
        return new String(randBuffer);
    }

    public static void main(String[] args) {
        String code = randomString(8);
        System.out.println(code);
    }

}
