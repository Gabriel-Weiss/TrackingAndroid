package demo.tracker.entity;

import androidx.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AppUser extends RealmObject {
    @PrimaryKey
    public String userCode;
    public boolean userStatus;

    public AppUser() {
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public boolean isUserStatus() {
        return userStatus;
    }

    public void setUserStatus(boolean userStatus) {
        this.userStatus = userStatus;
    }

    @NonNull
    @Override
    public String toString() {
        return "AppUser{" +
                "userCode='" + userCode + '\'' +
                ", userStatus=" + userStatus +
                '}';
    }
}
