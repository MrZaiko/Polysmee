package io.github.polysmee.internet.connection;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;

import io.github.polysmee.R;
import io.github.polysmee.agora.Command;

public class InternetConnection {

    private static boolean connectionOn = false;
    public static Command commandToUpdateWifiLogo;
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
                    if(commandToUpdateWifiLogo != null) {
                        commandToUpdateWifiLogo.execute(false, false);
                    }
                }

                @Override
                public void onLost(Network network) {
                    System.out.println("no connection !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    connectionOn = false;
                    if(commandToUpdateWifiLogo != null) {
                        commandToUpdateWifiLogo.execute(true, true);
                    }
                }


            });

        } catch (Exception e){
            System.out.println("exception !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    }

    public static void setCommand(Command<Boolean, Boolean> command) {
        commandToUpdateWifiLogo = command;
    }

}
