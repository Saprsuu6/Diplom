package com.example.instagram.services;

import androidx.annotation.NonNull;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class GetEthernetInfo {
    public static String getNetworkInfo() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : all) {
                if (networkInterface.getName().equalsIgnoreCase("wlan0")) {
                    return getIp(networkInterface).substring(0, getIp(networkInterface).indexOf("%"));
                }
            }

            return null;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    private static String getIp(NetworkInterface networkInterface) {
        return networkInterface.getInetAddresses().nextElement().toString();
    }
}
