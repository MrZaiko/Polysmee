package io.github.polysmee.internet.connection;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import androidx.annotation.RequiresApi;
import io.github.polysmee.agora.Command;

/**
 * Class to be used in order to check whether the internet connection is on or not
 */
public class InternetConnection {

    private static boolean connectionOn = false;
    private static boolean connectionOnTest = false;
    private static boolean testRunning = false;
    public static Command commandToUpdateWifiLogo;

    private InternetConnection() {}

    public static boolean isOn() {
        return testRunning ? connectionOnTest : connectionOn;
    }

    /**
     * Adds a listener on connection state
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void addConnectionListener(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {

                @Override
                public void onAvailable(Network network) {
                    connectionOn = true;
                    if(commandToUpdateWifiLogo != null) {
                        commandToUpdateWifiLogo.execute(false, false);
                    }
                }

                @Override
                public void onLost(Network network) {
                    connectionOn = false;
                    if(commandToUpdateWifiLogo != null) {
                        commandToUpdateWifiLogo.execute(true, true);
                    }
                }


            });

        } catch (Exception e){
        }
    }

    /**
     * Sets the command to be ran with arguments (true,true) when connection is lost and (false,false) when connection is available
     * @param command the new command attribute
     */
    public static void setCommand(Command<Boolean, Boolean> command) {
        commandToUpdateWifiLogo = command;
    }


    /**
     * Sets the internet connection in test mode
     * @param connected the new of value for the internet connection state
     */
    public static void setManuallyInternetConnectionForTests(boolean connected) {
        connectionOnTest = connected;
        testRunning = true;
    }

}
