package com.metoo.nspm.core.config.security.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    public static String encrypt(String text)  {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        String original = "123456";
        String encrypted = null;
        encrypted = encrypt(original);
        System.out.println("Original: " + original);
        System.out.println("Encrypted: " + encrypted);
    }
}
