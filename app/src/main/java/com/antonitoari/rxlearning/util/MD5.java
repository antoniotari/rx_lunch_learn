package com.antonitoari.rxlearning.util;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by antoniotari on 2016-04-12.
 */
public class MD5 {

    public static String md5(String toEncrypt) {
        String returnString;
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(toEncrypt.getBytes(Charset.forName("US-ASCII")), 0, toEncrypt.length());
            byte[] magnitude = digest.digest();
            BigInteger bi = new BigInteger(1, magnitude);
            returnString = String.format("%0" + (magnitude.length << 1) + "x", bi);
        } catch (NoSuchAlgorithmException e) {
            returnString = "";
        }

        return returnString;
    }
}
