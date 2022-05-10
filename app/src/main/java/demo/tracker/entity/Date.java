package demo.tracker.entity;

import androidx.annotation.NonNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Date extends RealmObject {
    @PrimaryKey
    public String date;
    public RealmList<Location> times = new RealmList<>();
    public RealmList<String> codes = new RealmList<>();

    public Date() {
    }

    public Date(String date, RealmList<Location> times, RealmList<String> codes) {
        this.date = date;
        this.times = times;
        this.codes = codes;
    }

    public void addTimes(Location time) {
        this.times.add(time);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public RealmList<Location> getTimes() {
        return times;
    }

    public void setTimes(RealmList<Location> times) {
        this.times = times;
    }

    public RealmList<String> getCodes() {
        return codes;
    }

    public void setCodes(RealmList<String> codes) {
        this.codes = codes;
    }

    @NonNull
    @Override
    public String toString() {
        return "Date{" +
                "date='" + date + '\'' +
                ", times=" + times +
                ", codes=" + codes +
                '}';
    }
}
