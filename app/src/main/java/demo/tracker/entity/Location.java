package demo.tracker.entity;

import androidx.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass(embedded = true)
public class Location extends RealmObject {
    public String time;
    public Double longitude;
    public Double latitude;
    public Double altitude;

    public Location() {
    }

    public Location(String time, Double longitude, Double latitude, Double altitude) {
        this.time = time;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

}
