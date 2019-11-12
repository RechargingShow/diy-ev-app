package org.open.ev.app.bluetooth;

import android.util.Log;

import org.open.ev.app.odb2.ODB2Data;

public class BluetoothWriter {

    private static BluetoothWriter instance = new BluetoothWriter();

    private BluetoothWriter(){

    }

    public static BluetoothWriter getInstance(){
        return instance;
    }

    public void writeMessage(String message){
        MyBluetoothManager manager = MyBluetoothManager.getInstance();
        if(manager.isConnected()){
            ODB2Data.getInstance().resetCommandResponse();
            Log.d("BluetoothWriter", String.format("Sending ODB2 command: %s", message));
            manager.getBluetooth().send(message + '\r');
        }
    }
}
