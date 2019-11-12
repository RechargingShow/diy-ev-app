package org.open.ev.app.network;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.open.ev.app.gps.GPSData;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GPSPostAsyncTask extends AsyncTask<GPSData, Void, Boolean> {

    private static final String LOG_TAG = GPSPostAsyncTask.class.getName();
    private static final String POST_URL = "<<URL TO YOUR BACK-END OR WHATEVER>>";

    @Override
    protected Boolean doInBackground(GPSData... gpsData) {
        HttpURLConnection connection = null;
        try {
            JSONObject requestObject = gpsData[0].toJSONObject();

            URL url = new URL(POST_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoInput(true);

            Log.d(LOG_TAG, String.format("Sending GPS data request with content %s", requestObject.toString()));

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(requestObject.toString());

            os.flush();
            os.close();

            Log.d(LOG_TAG, String.format("Repsonsecode of carinformation request is:%d", connection.getResponseCode()));
        } catch (Exception exception) {
            Log.e(LOG_TAG, "Exception occured while sending carinformation request. Better luck next time", exception);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return true;
    }
}
