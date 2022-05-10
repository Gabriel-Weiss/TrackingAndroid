package demo.tracker.mapper;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import demo.tracker.dto.DateDto;
import demo.tracker.dto.LocationDto;
import demo.tracker.entity.Date;
import demo.tracker.entity.Location;

public class Mapper {

    //Set of methods that convert Location entity to LocationDto and vice verso
    public static LocationDto convertSavedTimeToLocationDto(Location location) {
        LocationDto locationDto = new LocationDto();
        locationDto.setLongitude(location.longitude);
        locationDto.setLatitude(location.latitude);
        locationDto.setAltitude(location.altitude);
        locationDto.setTime(location.time);
        return locationDto;
    }

    public static List<LocationDto> convertListSavedTimeToLocationDto(List<Location> locations) {
        List<LocationDto> locationDtoList = new ArrayList<>(locations.size());
        for (Location location : locations) {
            locationDtoList.add(convertSavedTimeToLocationDto(location));
        }
        return locationDtoList;
    }

    public static Location convertLocationDtoToSavedTime(LocationDto locationDto) {
        Location location = new Location();
        location.time = locationDto.getTime();
        location.altitude = locationDto.getAltitude();
        location.latitude = locationDto.getLatitude();
        location.longitude = locationDto.getLongitude();
        return location;
    }

    //Set of methods that convert Date entity to DateDto and vice verso
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static DateDto convertSavedDateToDateDto(Date date) {
        DateDto dateDto = new DateDto();
        dateDto.setDate(date.getDate());
        Set<String> codes = new LinkedHashSet<>(date.codes.size());
        date.getCodes().iterator().forEachRemaining(codes::add);
        dateDto.setCodes(codes);
        List<Location> locations = new ArrayList<>(date.times.size());
        date.getTimes().iterator().forEachRemaining(locations::add);
        dateDto.setLocations(convertListSavedTimeToLocationDto(locations));
        return dateDto;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<DateDto> convertListSaveDateToDateDto(List<Date> dates) {
        List<DateDto> dateDtoList = new ArrayList<>(dates.size());
        for (Date date : dates) {
            dateDtoList.add(convertSavedDateToDateDto(date));
        }
        return dateDtoList;
    }

}
