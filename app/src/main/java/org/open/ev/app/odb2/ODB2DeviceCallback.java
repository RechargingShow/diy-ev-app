package org.open.ev.app.odb2;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import me.aflak.bluetooth.DeviceCallback;

import org.open.ev.app.bluetooth.BluetoothConnectionLabelUpdater;
import org.open.ev.app.bluetooth.MyBluetoothManager;

public class ODB2DeviceCallback implements DeviceCallback {

    private ODB2MessageProcessor messageProcessor;

    public ODB2DeviceCallback() {
        this.messageProcessor = new ODB2MessageProcessor();
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {
        Log.d("ODB2DeviceCallback", "onDeviceConnected");
        MyBluetoothManager.getInstance().connected();
        BluetoothConnectionLabelUpdater.getInstance().update();
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device, String message) {
        Log.d("ODB2DeviceCallback", String.format("onDeviceDisconnected: %s", message));
        MyBluetoothManager.getInstance().disconnect();
        BluetoothConnectionLabelUpdater.getInstance().update();
    }

    @Override
    public void onMessage(String message) {
        Log.d("ODB2DeviceCallback", String.format("onMessage: %s", message));
        if(!ODB2Data.getInstance().isSessionEnded()){
            messageProcessor.process(message);
        }
    }

    @Override
    public void onError(String message) {
        Log.d("ODB2DeviceCallback", String.format("onError: %s", message));
        ODB2Data.getInstance().sessionEnded();
    }

    @Override
    public void onConnectError(BluetoothDevice device, String message) {
        Log.d("ODB2DeviceCallback", String.format("onConnectError: %s", message));
        MyBluetoothManager.getInstance().disconnect();
        BluetoothConnectionLabelUpdater.getInstance().update();
    }
}
