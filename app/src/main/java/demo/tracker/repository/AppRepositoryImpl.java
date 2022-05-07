package demo.tracker.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import demo.tracker.entity.AppUser;
import demo.tracker.entity.SavedDate;
import demo.tracker.entity.SavedTime;
import io.realm.Realm;
import io.realm.RealmResults;

public class AppRepositoryImpl {
    private static final String TAG = "AppRepositoryImpl";
    private static final String CODE = Build.MANUFACTURER + "-" + Build.ID;

    private Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    public AppUser getRealmUser() {
        Realm instance = getRealm();
        AppUser appUser = instance.where(AppUser.class).equalTo("userCode", CODE).findFirst();
        instance.close();
        return appUser;
    }

    public void saveUserToRealm(String phone) {
        Realm instance = getRealm();
        instance.executeTransactionAsync(
                realm -> {
                    AppUser appUser = realm.createObject(AppUser.class, CODE);
                    appUser.setUserPhone(phone);
                }
        );
        Log.d(TAG, "saveUserToRealm() called: UserDto nonexistent ...saving");
        instance.close();
    }

    public AppUser readUserFromRealm() {
        Realm instance = getRealm();
        return instance.copyFromRealm(getRealmUser());
    }

    public List<SavedDate> readDatesFromRealm() {
        Realm instance = getRealm();
        List<SavedDate> dates = instance.copyFromRealm(instance.where(SavedDate.class).findAll());
        instance.close();
        return dates;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void cleanDates() {
        Realm instance = getRealm();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        instance.executeTransactionAsync(realm -> {
            for (SavedDate savedDate : realm.where(SavedDate.class).findAll()) {
                LocalDate date = LocalDate.parse(savedDate.getDate(), dateFormatter);

                if (LocalDate.now().minusWeeks(2).isAfter(date)) {
                    SavedDate first = realm.where(SavedDate.class).equalTo("date", savedDate.getDate()).findFirst();

                    if (first != null) {
                        String s = first.getDate();
                        first.deleteFromRealm();
                        Log.d(TAG, "cleanDates() called. Deleted date:" + s);
                    } else {
                        Log.d(TAG, "cleanDates: called. Date not existent");
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
                    SavedDate savedDate = realm.where(SavedDate.class).equalTo("date", today).findFirst();
                    if (savedDate == null) {
                        SavedDate date = realm.createObject(SavedDate.class, today);
                        if (lon != 0 || lat != 0 || alt != 0) {
                            SavedTime time = new SavedTime(now, lon, lat, alt);
                            date.times.add(time);
                        }
                        date.codes.add(code);
                        Log.d(TAG, "saveDataToRealm(): Date non existent saving: savedInstance = [" + date + "]");
                    } else {
                        if (lon != 0 || lat != 0 || alt != 0) {
                            SavedTime time = new SavedTime(now, lon, lat, alt);
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
