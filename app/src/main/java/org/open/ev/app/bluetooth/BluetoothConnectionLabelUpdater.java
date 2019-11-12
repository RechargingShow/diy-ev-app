package org.open.ev.app.bluetooth;

import android.app.Activity;
import android.widget.TextView;

import org.open.ev.app.R;

public class BluetoothConnectionLabelUpdater {

    private static BluetoothConnectionLabelUpdater instance = null;
    private static Activity activity;

    public static void initialize(Activity activity){
        instance = new BluetoothConnectionLabelUpdater();
        instance.setActivity(activity);
    }

    public static BluetoothConnectionLabelUpdater getInstance(){
        return instance;
    }

    private void setActivity(Activity activity) {
        BluetoothConnectionLabelUpdater.activity = activity;
    }

    public void update(){
        final TextView textView = activity.findViewById(R.id.bt_connection_state_label);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(MyBluetoothManager.getInstance().isConnected()){
                    textView.setText("ODB2 Dongle: CONNECTED");
                } else {
                    textView.setText("ODB2 Dongle: NOT CONNECTED");
                }
            }
        });
    }
}
