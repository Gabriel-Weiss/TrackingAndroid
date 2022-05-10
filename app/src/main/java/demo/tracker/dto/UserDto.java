package demo.tracker.dto;

import java.util.ArrayList;
import java.util.List;

public class UserDto {
    private String userId;
    public String userPhone;
    private boolean userStatus;
    private List<DateDto> dates = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public List<DateDto> getDates() {
        return dates;
    }

    public void setDates(List<DateDto> dates) {
        this.dates = dates;
    }
}