package demo.tracker.repository;

import android.os.Build;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import demo.tracker.entity.AppUser;
import demo.tracker.entity.SavedDate;
import demo.tracker.entity.SavedTime;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class AppRepositoryImpl implements AppRepository {
    private static final String TAG = "AppRepositoryImpl";
    private static final String CODE = Build.MANUFACTURER + "-" + Build.ID;

    private Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    @Override
    public AppUser getRealmUser() {
        Realm instance = getRealm();
        AppUser appUser = instance.where(AppUser.class).equalTo("userCode", CODE).findFirst();
        instance.close();
        return appUser;
    }

    @Override
    public void saveUserToRealm() {
        Realm instance = getRealm();
        instance.executeTransactionAsync(
                realm -> realm.createObject(AppUser.class, CODE)
        );
        Log.d(TAG, "saveUserToRealm() called: UserDto nonexistent ...saving");
        instance.close();
    }

    @Override
    public void checkUser() {
        if (getRealmUser() == null) {
            saveUserToRealm();
        }
    }

    @Override
    public AppUser readUserFromRealm() {
        Realm instance = getRealm();
        return instance.copyFromRealm(getRealmUser());
    }

    @Override
    public List<SavedDate> readDatesFromRealm() {
        Realm instance = getRealm();
        List<SavedDate> dates = instance.copyFromRealm(instance.where(SavedDate.class).findAll());
        instance.close();
        return dates;
    }

    @Override
    public void saveDateToRealm(double lon, double lat, double alt) {
        Realm instance = getRealm();

        String now = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String code = UUID.randomUUID().toString();

        instance.executeTransactionAsync(
                realm -> {
                    SavedDate savedDate = realm.where(SavedDate.class).equalTo("date", today).findFirst();
                    if (savedDate == null) {
                        SavedDate date = realm.createObject(SavedDate.class, today);
                        SavedTime time = new SavedTime(now, lon, lat, alt);
                        date.times.add(time);
                        date.codes.add(code);
                        Log.d(TAG, "saveDataToRealm(): Date non existent saving: savedInstance = [" + date + "]");
                    } else {
                        SavedTime time = new SavedTime(now, lon, lat, alt);
                        savedDate.times.add(time);
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
