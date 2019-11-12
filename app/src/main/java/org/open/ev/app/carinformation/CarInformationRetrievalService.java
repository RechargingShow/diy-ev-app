package org.open.ev.app.carinformation;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.open.ev.app.bluetooth.BluetoothWriter;
import org.open.ev.app.bluetooth.MyBluetoothManager;
import org.open.ev.app.network.NetworkManager;
import org.open.ev.app.odb2.ODB2Data;

public class CarInformationRetrievalService extends Service {

    private static final String LOG_TAG = CarInformationRetrievalService.class.getName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "CarInformationRetrievalService onStartCommand");
        retrieveAndSendCarInformation();
        return START_NOT_STICKY;
    }

    private void retrieveAndSendCarInformation() {
        Log.d(LOG_TAG, "Started the retrieval of the car information");
        NetworkManager networkManager = new NetworkManager(getApplicationContext());
        if (!networkManager.isNetworkAvailable()) {
            Log.d(LOG_TAG, "No network to even send car data. Stopping retrieval");
            return;
        }
        if(!MyBluetoothManager.getInstance().isConnected()){
            Log.d(LOG_TAG, "No Bluetooth device connected.");
            return;
        }
        ODB2Data.reset();
        BluetoothWriter.getInstance().writeMessage(ODB2Data.getInstance().getNextInitCommand());
    }
}
