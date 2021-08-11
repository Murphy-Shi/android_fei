package com.feilib.tool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

/**
 * @Description: java类作用描述
 * @Author: murphy
 * @CreateDate: 2021/8/5 11:07 上午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/8/5 11:07 上午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class NetworkUtil {
    private final String TAG = "NetworkUtil";

    public NetworkUtil() {
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] infos = mgr.getAllNetworkInfo();
        if (infos != null) {
            for (int i = 0; i < infos.length; i++) {
                if (infos[i].isConnected() == true) {
                    return true;
                }
            }
        }
        return false;
    }

    private void setNetworkCallback(Context context){
        if(context == null){
            Log.e(TAG, "setLisntenNetwork: context为空");
            return;
        }

        ConnectivityManager connectivityManager=(ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    Log.i(TAG, "onAvailable: "+network);
                }

                @Override
                public void onLosing(Network network, int maxMsToLive) {
                    super.onLosing(network, maxMsToLive);
                    Log.i(TAG, "onLosing: "+network);
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    Log.i(TAG, "onLost: "+network);
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    Log.i(TAG, "onUnavailable: ");
                }

                @Override
                public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    Log.i(TAG, "onCapabilitiesChanged: "+network);
                }

                @Override
                public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                    super.onLinkPropertiesChanged(network, linkProperties);
                    Log.i(TAG, "onLinkPropertiesChanged: "+network);
                }
            });
        }
    }
}
