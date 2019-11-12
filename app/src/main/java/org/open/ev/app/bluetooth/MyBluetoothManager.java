package org.open.ev.app.bluetooth;

import me.aflak.bluetooth.Bluetooth;

public class MyBluetoothManager {

    private boolean connected;
    private MyBluetooth bluetooth;

    private static MyBluetoothManager instance = new MyBluetoothManager();

    private MyBluetoothManager(){

    }

    public static MyBluetoothManager getInstance(){
        return instance;
    }

    void setBluetooth(MyBluetooth bluetooth){
        this.bluetooth = bluetooth;
    }

    public void connected(){
        this.connected = true;
    }

    public void disconnect(){
        connected = false;
        this.bluetooth.onStop();
        this.bluetooth = null;
    }

    public MyBluetooth getBluetooth() {
        return bluetooth;
    }

    public boolean isConnected() {
        return connected;
    }
}
