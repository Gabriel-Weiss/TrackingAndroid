package demo.tracker.dto;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DateDto {
	private String date;
	private List<LocationDto> locations = new ArrayList<>();
	private Set<String> codes = new LinkedHashSet<>();

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<LocationDto> getLocations() {
		return locations;
	}

	public void setLocations(List<LocationDto> locations) {
		this.locations = locations;
	}

	public Set<String> getCodes() {
		return codes;
	}

	public void setCodes(Set<String> codes) {
		this.codes = codes;
	}
}