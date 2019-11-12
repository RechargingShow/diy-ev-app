package org.open.ev.app.gps;

import org.json.JSONException;
import org.json.JSONObject;

public class GPSData {

    private static GPSData instance;

    private Double latitude;
    private Double longitude;
    private Integer speed;
    private Integer accuracy;

    private GPSData(){

    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("latitude", latitude);
        jsonObject.put("longitude", longitude);
        jsonObject.put("speed", speed);
        jsonObject.put("accuracy", accuracy);
        return jsonObject;
    }

    public static GPSData getInstance(){
        if(instance == null){
            instance = new GPSData();
        }
        return instance;
    }
}
