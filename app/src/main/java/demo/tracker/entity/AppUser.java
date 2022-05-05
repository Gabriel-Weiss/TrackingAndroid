package demo.tracker.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AppUser extends RealmObject {
    @PrimaryKey
    public String userCode;
    public String userPhone;
    public boolean userStatus;

    public AppUser() {
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public boolean isUserStatus() {
        return userStatus;
    }

    public void setUserStatus(boolean userStatus) {
        this.userStatus = userStatus;
    }
}
