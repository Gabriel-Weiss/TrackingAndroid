package demo.tracker.repository;

import java.util.List;

import demo.tracker.entity.AppUser;
import demo.tracker.entity.SavedDate;

public interface AppRepository {
    void saveUserToRealm();
    AppUser readUserFromRealm();
    void saveDateToRealm(double lon, double lat, double alt);
    List<SavedDate> readDatesFromRealm();
    AppUser getRealmUser();
    void checkUser();
}
