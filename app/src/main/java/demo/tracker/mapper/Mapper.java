package demo.tracker.mapper;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import demo.tracker.dto.DateDto;
import demo.tracker.dto.LocationDto;
import demo.tracker.dto.UserDto;
import demo.tracker.entity.AppUser;
import demo.tracker.entity.SavedDate;
import demo.tracker.entity.SavedTime;

public class Mapper {

    //Set of methods that convert SavedTime entity to LocationDto and vice verso
    public static LocationDto convertSavedTimeToLocationDto(SavedTime savedTime) {
        LocationDto locationDto = new LocationDto();
        locationDto.setLongitude(savedTime.longitude);
        locationDto.setLatitude(savedTime.latitude);
        locationDto.setAltitude(savedTime.altitude);
        locationDto.setTime(savedTime.time);
        return locationDto;
    }

    public static List<LocationDto> convertListSavedTimeToLocationDto(List<SavedTime> savedTimes) {
        List<LocationDto> locationDtoList = new ArrayList<>(savedTimes.size());
        for (SavedTime savedTime : savedTimes) {
            locationDtoList.add(convertSavedTimeToLocationDto(savedTime));
        }
        return locationDtoList;
    }

    public static SavedTime convertLocationDtoToSavedTime(LocationDto locationDto) {
        SavedTime savedTime = new SavedTime();
        savedTime.time = locationDto.getTime();
        savedTime.altitude = locationDto.getAltitude();
        savedTime.latitude = locationDto.getLatitude();
        savedTime.longitude = locationDto.getLongitude();
        return savedTime;
    }

    public static List<SavedTime> convertListLocationDtoToSavedTime(List<LocationDto> locationDtoList) {
        List<SavedTime> savedTimes = new ArrayList<>(locationDtoList.size());
        for (LocationDto locationDto : locationDtoList) {
            savedTimes.add(convertLocationDtoToSavedTime(locationDto));
        }
        return savedTimes;
    }

    //Set of methods that convert SavedDate entity to DateDto and vice verso
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static DateDto convertSavedDateToDateDto(SavedDate savedDate) {
        DateDto dateDto = new DateDto();
        dateDto.setDate(savedDate.getDate());
        Set<String> codes = new LinkedHashSet<>(savedDate.codes.size());
        savedDate.getCodes().iterator().forEachRemaining(codes::add);
        dateDto.setCodes(codes);
        List<SavedTime> savedTimes = new ArrayList<>(savedDate.times.size());
        savedDate.getTimes().iterator().forEachRemaining(savedTimes::add);
        dateDto.setLocations(convertListSavedTimeToLocationDto(savedTimes));
        return dateDto;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<DateDto> convertListSaveDateToDateDto(List<SavedDate> savedDates) {
        List<DateDto> dateDtoList = new ArrayList<>(savedDates.size());
        for (SavedDate savedDate : savedDates) {
            dateDtoList.add(convertSavedDateToDateDto(savedDate));
        }
        return dateDtoList;
    }

}
