package com.metoo.nspm.core.jwt.util;

public class Globals {

    private static Globals globals = new Globals();

    public final static String AUTHORIZATION = "AUTHORIZATION";

    public final static String BEARER = "BEARER";

    public final static long EXPIRE = 2 * 60 * 1000;

    public final static String AES_KEY = "#+NPzwDvPmCJvpY@";

    public Globals() {
    }

    public static Globals getInstance() {
        return globals;
    }
}
