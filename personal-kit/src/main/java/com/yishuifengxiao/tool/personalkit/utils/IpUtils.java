package com.yishuifengxiao.tool.personalkit.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IpUtils {


    public static List<String> localIps() {
        Set<String> ips = new HashSet<>();
        try {
            Enumeration<NetworkInterface> networkInterfaces =
                    NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    ips.add(inetAddress.getHostAddress());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ips.stream().filter(s -> !StringUtils.containsAnyIgnoreCase(s, "127.0.0.1", "172"
                + ".17.0.1", "%")).collect(Collectors.toList());
    }

    public static String getOutIp() {
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            String ip = in.readLine();
            return ip;

        } catch (Exception e) {
        }
        return "";

    }

    public static void main(String[] args) {
        List<String> strings = localIps();
        strings.forEach(System.out::println);
        System.out.println(getOutIp());
    }
}
