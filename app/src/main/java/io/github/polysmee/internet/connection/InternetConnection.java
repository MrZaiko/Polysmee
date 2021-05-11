package io.github.polysmee.internet.connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class InternetConnection {

    private static boolean connectionOn = false;

    private InternetConnection() {}

    public static boolean isOn() {
        return connectionOn;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void addConnectionListener(Context context) {
        try {
            System.out.println("MESAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGE");
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {

                @Override
                public void onAvailable(Network network) {
                    System.out.println("connection !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    connectionOn = true;
                }

                @Override
                public void onLost(Network network) {
                    System.out.println("no connection !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    connectionOn = false;
                }


            });

        }catch (Exception e){
            System.out.println("exception !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }

}
