package com.sunway.averychoke.studywifidirect3.util;

import android.net.wifi.p2p.WifiP2pManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by AveryChoke on 8/4/2017.
 */

public class WifiDirectUtil {
    public static void deletePersistentGroups(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        try {
            Method method = WifiP2pManager.class.getMethod("deletePersistentGroup",
                    WifiP2pManager.Channel.class, Integer.class, WifiP2pManager.ActionListener.class);

            for (int netId = 0; netId < 32; netId++) {
                method.invoke(manager, channel, netId, null);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
