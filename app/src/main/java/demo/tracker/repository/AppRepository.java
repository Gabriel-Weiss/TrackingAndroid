package demo.tracker.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import demo.tracker.entity.Date;
import demo.tracker.entity.Location;
import demo.tracker.entity.User;
import io.realm.Realm;

public class AppRepository {
    private static final String TAG = "AppRepository";
    private static final String CODE = UUID.randomUUID().toString();

    private Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    public User getRealmUser() {
        Realm instance = getRealm();
        User appUser = instance.where(User.class).equalTo("userCode", CODE).findFirst();
        instance.close();
        return appUser;
    }

    public void saveUserToRealm(String phone) {
        Realm instance = getRealm();
        instance.executeTransactionAsync(
                realm -> {
                    User appUser = realm.createObject(User.class, CODE);
                    appUser.setUserPhone(phone);
                }
        );
        Log.d(TAG, "saveUserToRealm() called: UserDto nonexistent ...saving");
        instance.close();
    }

    public User readUserFromRealm() {
        Realm instance = getRealm();
        return instance.copyFromRealm(getRealmUser());
    }

    public List<Date> readDatesFromRealm() {
        Realm instance = getRealm();
        List<Date> dates = instance.copyFromRealm(instance.where(Date.class).findAll());
        instance.close();
        return dates;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void cleanDates() {
        Realm instance = getRealm();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        instance.executeTransactionAsync(realm -> {
            for (Date savedDate : realm.where(Date.class).findAll()) {
                LocalDate date = LocalDate.parse(savedDate.getDate(), dateFormatter);
                Log.e(TAG, "cleanDates: " + date.format(dateFormatter));

                if (LocalDate.now().minusDays(2).isAfter(date)) {
                    Date first = realm.where(Date.class).equalTo("date", savedDate.getDate()).findFirst();

                    if (first != null) {
                        String s = first.getDate();
                        first.deleteFromRealm();
                        Log.e(TAG, "cleanDates() called. Deleted date:" + s);
                    } else {
                        Log.e(TAG, "cleanDates: called. Date not existent");
                    }
                }
            }
        });
        instance.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveDateToRealm(double lon, double lat, double alt) {
        Realm instance = getRealm();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String today = LocalDate.now().format(dateFormatter);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String now = LocalTime.now().format(timeFormatter);
        String code = UUID.randomUUID().toString();

        instance.executeTransactionAsync(
                realm -> {
                    Date savedDate = realm.where(Date.class).equalTo("date", today).findFirst();
                    if (savedDate == null) {
                        Date date = realm.createObject(Date.class, today);
                        if (lon != 0 || lat != 0 || alt != 0) {
                            Location time = new Location(now, lon, lat, alt);
                            date.times.add(time);
                        }
                        date.codes.add(code);
                        Log.d(TAG, "saveDataToRealm(): Date non existent saving: savedInstance = [" + date + "]");
                    } else {
                        if (lon != 0 || lat != 0 || alt != 0) {
                            Location time = new Location(now, lon, lat, alt);
                            savedDate.times.add(time);
                        }
                        savedDate.codes.add(code);
                        Log.d(TAG, "saveDataToRealm(): Date exists updating: updatedInstance = [" + savedDate + "]");
                    }
                }
                ,
                () -> Log.d(TAG, "onSuccess: successfully executed transaction")
                ,
                error -> Log.d(TAG, "Transaction failed with = [" + error + "]")
        );
        instance.close();
    }
}