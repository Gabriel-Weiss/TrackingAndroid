package demo.tracker.entity;

import androidx.annotation.NonNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SavedDate extends RealmObject {
    @PrimaryKey
    public String date;
    public RealmList<SavedTime> times = new RealmList<>();
    public RealmList<String> codes = new RealmList<>();

    public SavedDate() {
    }

    public SavedDate(String date, RealmList<SavedTime> times, RealmList<String> codes) {
        this.date = date;
        this.times = times;
        this.codes = codes;
    }

    public void addTimes(SavedTime time) {
        this.times.add(time);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public RealmList<SavedTime> getTimes() {
        return times;
    }

    public void setTimes(RealmList<SavedTime> times) {
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
        return "SavedDate{" +
                "date='" + date + '\'' +
                ", times=" + times +
                ", codes=" + codes +
                '}';
    }
}
