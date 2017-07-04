package com.rapido.net;

/**
 * Created by pragati.singh on 7/4/2017.
 */

public class NetUtil {
    public static final int CONNECT_TIMEOUT = 60 * 1000;
    public static final int READ_TIMEOUT = 60 * 1000;

    public static String getBaseUrl(String url) {
        int index = 0;
        try {
            index = url.lastIndexOf('/');
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (url.substring(0, index) + "/");
    }

    public static String getSubUrl(String url) {
        int index = 0;
        try {
            index = url.lastIndexOf('/') + 1;
        } catch (StringIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        return (url.substring(index, url.length()));
    }
}
