package org.open.ev.app.bluetooth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import me.aflak.bluetooth.Bluetooth;
import me.aflak.bluetooth.BluetoothCallback;
import me.aflak.bluetooth.DeviceCallback;
import me.aflak.bluetooth.DiscoveryCallback;
import me.aflak.bluetooth.ThreadHelper;

public class MyBluetooth {
    private static final int REQUEST_ENABLE_BT = 1111;

    private Activity activity;
    private Context context;
    private UUID uuid;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private BluetoothDevice device, devicePair;
    private InputStream input;
    private OutputStream out;

    private DeviceCallback deviceCallback;
    private DiscoveryCallback discoveryCallback;
    private BluetoothCallback bluetoothCallback;
    private boolean connected;

    private boolean runOnUi;

    public MyBluetooth(Context context){
        initialize(context, UUID.fromString("<<YOUR ODB2 UUID>>"));
    }

    public MyBluetooth(Context context, UUID uuid){
        initialize(context, uuid);
    }

    private void initialize(Context context, UUID uuid){
        this.context = context;
        this.uuid = uuid;
        this.deviceCallback = null;
        this.discoveryCallback = null;
        this.bluetoothCallback = null;
        this.connected = false;
        this.runOnUi = false;
    }

    public void onStart(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        context.registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    public void onStop(){
        context.unregisterReceiver(bluetoothReceiver);
    }

    public void showEnableDialog(Activity activity){
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void enable(){
        if(bluetoothAdapter!=null) {
            if (!bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.enable();
            }
        }
    }

    public void disable(){
        if(bluetoothAdapter!=null) {
            if (bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.disable();
            }
        }
    }

    public BluetoothSocket getSocket(){
        return socket;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public boolean isEnabled(){
        if(bluetoothAdapter!=null) {
            return bluetoothAdapter.isEnabled();
        }
        return false;
    }

    public void setCallbackOnUI(Activity activity){
        this.activity = activity;
        this.runOnUi = true;
    }

    public void onActivityResult(int requestCode, final int resultCode){
        if(bluetoothCallback!=null){
            if(requestCode==REQUEST_ENABLE_BT){
                ThreadHelper.run(runOnUi, activity, new Runnable() {
                    @Override
                    public void run() {
                        if(resultCode==Activity.RESULT_CANCELED){
                            bluetoothCallback.onUserDeniedActivation();
                        }
                    }
                });
            }
        }
    }

    public void connectToAddress(String address, boolean insecureConnection) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        connectToDevice(device, insecureConnection);
    }

    public void connectToAddress(String address) {
        connectToAddress(address, false);
    }

    public void connectToName(String name, boolean insecureConnection) {
        for (BluetoothDevice blueDevice : bluetoothAdapter.getBondedDevices()) {
            if (blueDevice.getName().equals(name)) {
                connectToDevice(blueDevice, insecureConnection);
                return;
            }
        }
    }

    public void connectToName(String name) {
        connectToName(name, false);
    }

    public void connectToDevice(BluetoothDevice device, boolean insecureConnection){
        new MyBluetooth.ConnectThread(device, insecureConnection).start();
    }

    public void connectToDevice(BluetoothDevice device){
        connectToDevice(device, false);
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (final IOException e) {
            if(deviceCallback !=null) {
                ThreadHelper.run(runOnUi, activity, new Runnable() {
                    @Override
                    public void run() {
                        deviceCallback.onError(e.getMessage());
                    }
                });
            }
        }
    }

    public boolean isConnected(){
        return connected;
    }

    public void send(String msg, String charset){
        try {
            if(!TextUtils.isEmpty(charset)) {
                out.write(msg.getBytes(charset));//Eg: "US-ASCII"
            }else {
                out.write(msg.getBytes());//Sending as UTF-8 as default
            }
        } catch (final IOException e) {
            connected=false;
            if(deviceCallback !=null){
                ThreadHelper.run(runOnUi, activity, new Runnable() {
                    @Override
                    public void run() {
                        deviceCallback.onDeviceDisconnected(device, e.getMessage());
                    }
                });
            }
        }
    }

    public void send(String msg){
        send(msg, null);
    }

    public List<BluetoothDevice> getPairedDevices(){
        List<BluetoothDevice> devices = new ArrayList<>();
        devices.addAll(bluetoothAdapter.getBondedDevices());
        return devices;
    }

    public void startScanning(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);

        context.registerReceiver(scanReceiver, filter);
        bluetoothAdapter.startDiscovery();
    }

    public void stopScanning(){
        context.unregisterReceiver(scanReceiver);
        bluetoothAdapter.cancelDiscovery();
    }

    public void pair(BluetoothDevice device){
        context.registerReceiver(pairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        devicePair=device;
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (final Exception e) {
            if(discoveryCallback!=null) {
                ThreadHelper.run(runOnUi, activity, new Runnable() {
                    @Override
                    public void run() {
                        discoveryCallback.onError(e.getMessage());
                    }
                });
            }
        }
    }

    public void unpair(BluetoothDevice device) {
        context.registerReceiver(pairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        devicePair=device;
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (final Exception e) {
            if(discoveryCallback!=null) {
                ThreadHelper.run(runOnUi, activity, new Runnable() {
                    @Override
                    public void run() {
                        discoveryCallback.onError(e.getMessage());
                    }
                });
            }
        }
    }

    private class ReceiveThread extends Thread implements Runnable{
        public void run(){
            int bytes;
            byte[] buffer = new byte[1024];
            try {
                while((bytes = input.read(buffer)) != -1) {
                    if(deviceCallback != null){
                        final String msg = new String(buffer, 0, bytes);
                        ThreadHelper.run(runOnUi, activity, new Runnable() {
                            @Override
                            public void run() {
                                deviceCallback.onMessage(msg);
                            }
                        });
                    }
                }
            } catch (final IOException e) {
                connected=false;
                if(deviceCallback != null){
                    ThreadHelper.run(runOnUi, activity, new Runnable() {
                        @Override
                        public void run() {
                            deviceCallback.onDeviceDisconnected(device, e.getMessage());
                        }
                    });
                }
            }
        }
    }

    private class ConnectThread extends Thread {
        ConnectThread(BluetoothDevice device, boolean insecureConnection) {
            MyBluetooth.this.device=device;
            try {
                if(insecureConnection){
                    MyBluetooth.this.socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                }
                else{
                    MyBluetooth.this.socket = device.createRfcommSocketToServiceRecord(uuid);
                }
            } catch (IOException e) {
                if(deviceCallback !=null){
                    deviceCallback.onError(e.getMessage());
                }
            }
        }

        public void run() {
            bluetoothAdapter.cancelDiscovery();

            try {
                socket.connect();
                out = socket.getOutputStream();
                input = socket.getInputStream();
                connected=true;

                new MyBluetooth.ReceiveThread().start();

                if(deviceCallback !=null) {
                    ThreadHelper.run(runOnUi, activity, new Runnable() {
                        @Override
                        public void run() {
                            deviceCallback.onDeviceConnected(device);
                        }
                    });
                }
            } catch (final IOException e) {
                if(deviceCallback !=null) {
                    ThreadHelper.run(runOnUi, activity, new Runnable() {
                        @Override
                        public void run() {
                            deviceCallback.onConnectError(device, e.getMessage());
                        }
                    });
                }

                try {
                    socket.close();
                } catch (final IOException closeException) {
                    if (deviceCallback != null) {
                        ThreadHelper.run(runOnUi, activity, new Runnable() {
                            @Override
                            public void run() {
                                deviceCallback.onError(closeException.getMessage());
                            }
                        });
                    }
                }
            }
        }
    }

    private BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action!=null) {
                switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    if (state == BluetoothAdapter.STATE_OFF) {
                        if (discoveryCallback != null) {
                            ThreadHelper.run(runOnUi, activity, new Runnable() {
                                @Override
                                public void run() {
                                    discoveryCallback.onError("Bluetooth turned off");
                                }
                            });
                        }
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    if (discoveryCallback != null){
                        ThreadHelper.run(runOnUi, activity, new Runnable() {
                            @Override
                            public void run() {
                                discoveryCallback.onDiscoveryStarted();
                            }
                        });
                    }
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    context.unregisterReceiver(scanReceiver);
                    if (discoveryCallback != null){
                        ThreadHelper.run(runOnUi, activity, new Runnable() {
                            @Override
                            public void run() {
                                discoveryCallback.onDiscoveryFinished();
                            }
                        });
                    }
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (discoveryCallback != null){
                        ThreadHelper.run(runOnUi, activity, new Runnable() {
                            @Override
                            public void run() {
                                discoveryCallback.onDeviceFound(device);
                            }
                        });
                    }
                    break;
                }
            }
        }
    };

    private final BroadcastReceiver pairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d("BLE", "PAIR RECEIVER");

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState	= intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    context.unregisterReceiver(pairReceiver);
                    if(discoveryCallback!=null){
                        ThreadHelper.run(runOnUi, activity, new Runnable() {
                            @Override
                            public void run() {
                                discoveryCallback.onDevicePaired(devicePair);
                            }
                        });
                    }
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                    context.unregisterReceiver(pairReceiver);
                    if(discoveryCallback!=null){
                        ThreadHelper.run(runOnUi, activity, new Runnable() {
                            @Override
                            public void run() {
                                discoveryCallback.onDeviceUnpaired(devicePair);
                            }
                        });
                    }
                }
            }
        }
    };

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action!=null && action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if(bluetoothCallback!=null) {
                    ThreadHelper.run(runOnUi, activity, new Runnable() {
                        @Override
                        public void run() {
                            switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                                bluetoothCallback.onBluetoothOff();
                                break;
                            case BluetoothAdapter.STATE_TURNING_OFF:
                                bluetoothCallback.onBluetoothTurningOff();
                                break;
                            case BluetoothAdapter.STATE_ON:
                                bluetoothCallback.onBluetoothOn();
                                break;
                            case BluetoothAdapter.STATE_TURNING_ON:
                                bluetoothCallback.onBluetoothTurningOn();
                                break;
                            }
                        }
                    });
                }
            }
        }
    };

    public void setDeviceCallback(DeviceCallback deviceCallback) {
        this.deviceCallback = deviceCallback;
    }

    public void removeCommunicationCallback(){
        this.deviceCallback = null;
    }

    public void setDiscoveryCallback(DiscoveryCallback discoveryCallback){
        this.discoveryCallback = discoveryCallback;
    }

    public void removeDiscoveryCallback(){
        this.discoveryCallback = null;
    }

    public void setBluetoothCallback(BluetoothCallback bluetoothCallback){
        this.bluetoothCallback = bluetoothCallback;
    }

    public void removeBluetoothCallback(){
        this.bluetoothCallback = null;
    }
}
