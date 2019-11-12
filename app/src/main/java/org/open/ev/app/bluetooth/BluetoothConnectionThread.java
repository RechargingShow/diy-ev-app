package org.open.ev.app.bluetooth;

import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import me.aflak.bluetooth.Bluetooth;

import org.open.ev.app.odb2.ODB2DeviceCallback;

public class BluetoothConnectionThread implements Runnable {

    private Activity activity;
    private static final String ODB2_MAC_ADRESS = "<<YOUR ODB2 Dongle MAC adress>>";

    public BluetoothConnectionThread(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void run() {
        MyBluetooth bluetooth = new MyBluetooth(activity);
        bluetooth.onStart();
        bluetooth.enable();

        List<BluetoothDevice> devices = bluetooth.getPairedDevices();

        BluetoothDevice odb2Dongle = null;
        for(BluetoothDevice device : devices){
            if(ODB2_MAC_ADRESS.equals(device.getAddress())){
                odb2Dongle = device;
                break;
            }
        }

        if(odb2Dongle != null){
            bluetooth.setDeviceCallback(new ODB2DeviceCallback());
            bluetooth.connectToDevice(odb2Dongle);
            MyBluetoothManager.getInstance().setBluetooth(bluetooth);
        }
    }
}
