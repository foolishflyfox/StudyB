package com.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author benfeihu
 */
public class NetUtils {
    public static String getLocalIp(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "unknown-ip";
        }
    }
}
