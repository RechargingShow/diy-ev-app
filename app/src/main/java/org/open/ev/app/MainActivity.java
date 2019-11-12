package org.open.ev.app;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.open.ev.app.bluetooth.BluetoothConnectionLabelUpdater;
import org.open.ev.app.bluetooth.BluetoothConnectionThread;
import org.open.ev.app.bluetooth.MyBluetoothManager;
import org.open.ev.app.carinformation.CarInformationBroadcastReceiver;
import org.open.ev.app.carinformation.CarInformationRetrievalService;
import org.open.ev.app.gps.GPSData;
import org.open.ev.app.gps.GPSTracker;
import org.open.ev.app.network.GPSPostAsyncTask;
import org.open.ev.app.network.ODB2PostAsyncTask;
import org.open.ev.app.notification.MyNotificationManager;
import org.open.ev.app.odb2.ODB2Data;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyNotificationManager.createNotificationChannel(getApplicationContext());
        BluetoothConnectionLabelUpdater.initialize(this);
        connectToBluetoothDevice();
        initStartButton();
        initTestODB2Button();
        initTestHttpButton();
    }

    private void connectToBluetoothDevice(){
        Thread connectionThread = new Thread(new BluetoothConnectionThread(this));
        connectionThread.run();
    }

    @Override
    protected void onDestroy() {
        GPSTracker gpsTracker = new GPSTracker(this);
        gpsTracker.stop();
        if(MyBluetoothManager.getInstance().getBluetooth() != null){
            MyBluetoothManager.getInstance().disconnect();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent serviceToCancel = new Intent(getApplicationContext(), CarInformationBroadcastReceiver.class);
        PendingIntent cancelServicePendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                0, // integer constant used to identify the service
                serviceToCancel,
                0 //no FLAG needed for a service cancel
        );
        alarmManager.cancel(cancelServicePendingIntent);

        Log.d(LOG_TAG, "Destroyed main activity");
        super.onDestroy();
    }

    private void initStartButton(){
        final Button button = findViewById(R.id.start_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(LOG_TAG, "Pressed start button.");
                GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
                gpsTracker.start();

                Intent intent = new Intent(getApplicationContext(), CarInformationBroadcastReceiver.class);
                final PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                //Set a repeating alarm every x minutes (ms * s * min)
                alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(),
                        1000 * 60 * 5, pendingIntent);
//                alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP,  System.currentTimeMillis(),
//                        1000 * 30 * 1, pendingIntent);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification =  createNotification();
                notificationManager.notify(MyNotificationManager.NOTIFICATION_ID, notification);

            }
        });

    }

    private void initTestODB2Button(){
        final Button button = findViewById(R.id.test_odb2_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(LOG_TAG, "Pressed test odb2 button. Retrieving odb2 data one time");
                GPSData gpsData =  GPSData.getInstance();
                //Set Aegon as gpsData
                gpsData.setLatitude(51.942937);
                gpsData.setLongitude(4.483278);
                gpsData.setSpeed(0);
                gpsData.setAccuracy(15);
                GPSPostAsyncTask gpsPostAsyncTask = new GPSPostAsyncTask();
                gpsPostAsyncTask.execute(gpsData);
                startService(new Intent(getApplicationContext(), CarInformationRetrievalService.class));
            }
        });
    }

    private void initTestHttpButton(){
        final Button button = findViewById(R.id.test_http_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(LOG_TAG, "Pressed test http button. Sending http request to server");
                GPSData gpsData =  GPSData.getInstance();
                //Set Aegon as gpsData
                gpsData.setLatitude(51.942937);
                gpsData.setLongitude(4.483278);
                gpsData.setSpeed(0);
                gpsData.setAccuracy(60);
                GPSPostAsyncTask gpsPostAsyncTask = new GPSPostAsyncTask();
                gpsPostAsyncTask.execute(gpsData);

                //Set some odb2 data
                ODB2Data odb2Data =  ODB2Data.getInstance();
                odb2Data.getDataCommand();
                //soc_soh
                odb2Data.addResonseStringToCommandResponse("7EC102E620105003FFF7EC21900000000000007EC220000000000003C7EC239742CC000150107EC240003E8000000007EC259B0000C3C300007EC261000000000AAAA>");
                odb2Data.setByteDataForDataCommand();
                odb2Data.resetCommandResponse();
                odb2Data.getDataCommand();
                //other_data
                odb2Data.addResonseStringToCommandResponse("7EC103E620101FFF7E77EC21FF973C9742CC807EC2200000EF90E0E0E7EC230E0D0E00000FC37EC240AC34700007A007EC250032EC000030957EC26000012FB0000117EC27560009B02A09007EC28020000000003E8>");
                odb2Data.setByteDataForDataCommand();

                ODB2PostAsyncTask odb2PostAsyncTask =  new ODB2PostAsyncTask();
                odb2PostAsyncTask.execute(odb2Data);
            }
        });
    }

    private Notification createNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), MyNotificationManager.CHANNEL_ID)
                .setSmallIcon(R.drawable.notificationlogo)
                .setContentTitle("Ev app service running")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        return mBuilder.build();
    }
}
