package org.open.ev.app.carinformation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CarInformationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, CarInformationRetrievalService.class);
        context.startService(i);
    }
}
