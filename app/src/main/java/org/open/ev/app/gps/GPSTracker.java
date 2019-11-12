package org.open.ev.app.gps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import org.open.ev.app.network.GPSPostAsyncTask;
import org.open.ev.app.network.NetworkManager;

public class GPSTracker implements LocationListener {

    private static final String LOG_TAG = GPSTracker.class.getName();
    private Context context;
    //private static final int GPS_UPDATE_TIME = 300000;
    private static final int GPS_UPDATE_TIME = 1000 * 60 * 4;

    public GPSTracker(Context context) {
        this.context = context;
    }

    public void start() {
        stop();
        Log.d(LOG_TAG, "Started listening for GPS updates");
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Please give app permission to use the GPS.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "Please enable location services in the settings.", Toast.LENGTH_LONG).show();
            return;
        }
        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, GPS_UPDATE_TIME, 0, this);
    }

    public void stop() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
        Log.d(LOG_TAG, "Stopped GPSTracker from receiving updates");
    }

    /**
     * Because of speed inaccuracy we say everything less than 5km/h is standing still.
     */
    @Override
    public void onLocationChanged(Location location) {
        if (!new NetworkManager(context).isNetworkAvailable()) {
            return;
        }
        if (location.getLatitude() == 0 || location.getLongitude() == 0) {
            Log.d(LOG_TAG, String.format("Location unknown. Setting values to null. lat: %f,long: %f",
                    location.getLatitude(), location.getLongitude()));
            return;
        }
        GPSData gpsData = GPSData.getInstance();
        gpsData.setLatitude(location.getLatitude());
        gpsData.setLongitude(location.getLongitude());
        gpsData.setAccuracy(Long.valueOf(Math.round(location.getAccuracy())).intValue());

        Float speed = location.hasSpeed() ? location.getSpeed() : null;
        if (speed != null) {
            double speedInKmH = speed * 3.6;
            int roundedSpeed = Long.valueOf(Math.round(speedInKmH)).intValue();
            gpsData.setSpeed(roundedSpeed > 5 ? roundedSpeed : 0);
        }
        GPSPostAsyncTask gpsPostAsyncTask = new GPSPostAsyncTask();
        gpsPostAsyncTask.execute(gpsData);
        Log.d(LOG_TAG, String.format("New location received lat: %f,long: %f, speed: %f, accuracy: %f", location.getLatitude(), location.getLongitude(), speed,
                location.getAccuracy()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
