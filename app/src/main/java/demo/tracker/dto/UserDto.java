package demo.tracker.dto;

import java.util.ArrayList;
import java.util.List;

public class UserDto {
	private String userId;
	private boolean userStatus = false;
	private List<DateDto> dates = new ArrayList<>();

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
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