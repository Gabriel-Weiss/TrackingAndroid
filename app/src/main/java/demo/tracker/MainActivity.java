package demo.tracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import demo.tracker.dto.DateDto;
import demo.tracker.dto.UserDto;
import demo.tracker.entity.AppUser;
import demo.tracker.entity.SavedDate;
import demo.tracker.mapper.Mapper;
import demo.tracker.repository.AppRepository;
import demo.tracker.repository.AppRepositoryImpl;
import demo.tracker.utilities.Utilities;
import io.realm.Realm;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 33;
    private final AppRepository appRepository;
    Button sendData, stopTask;
    private TextView jsonView;

    public MainActivity() {
        this.appRepository = new AppRepositoryImpl();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, REQUEST_CODE);

        jsonView = findViewById(R.id.json_view);
        sendData = findViewById(R.id.btn_send_data);
        stopTask = findViewById(R.id.btn_stop_task);
        appRepository.checkUser();
        postUser();
        Timer timer = initialzeTimerTask();

        sendData.setOnClickListener(v -> postUserData());
        stopTask.setOnClickListener(v -> {
            timer.cancel();
            timer.purge();
        });


    }

    @NonNull
    private Timer initialzeTimerTask() {
        Timer timer = new Timer();
        timer.schedule(new CustomTimer(), 10000, 5000);
        return timer;
    }

    private class CustomTimer extends TimerTask {
        @Override
        public void run() {
            getLocation();
        }
    }

    public Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    void getLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(
                    location -> appRepository.saveDateToRealm(location.getLongitude(), location.getLatitude(), location.getAltitude())
            );
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
    }

    private void postUser() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = Utilities.getProperty("post_user_url", getApplicationContext());

        AppUser appUser = appRepository.readUserFromRealm();
        JSONObject jsonUser = new JSONObject();
        try {
            jsonUser.put("userId", appUser.getUserCode());
            jsonUser.put("userStatus", appUser.isUserStatus());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonView.setText(jsonUser.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonUser,
                response -> Log.i("onResponse() called. ", "User was posted successfully"),
                error -> Log.e("onErrorResponse: ", error.toString()));

        requestQueue.add(jsonObjectRequest);
    }

    public void postUserData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = Utilities.getProperty("post_user_url", getApplicationContext());

        AppUser appUser = appRepository.readUserFromRealm();
        List<SavedDate> dates = appRepository.readDatesFromRealm();
        List<DateDto> dateDtoList = Mapper.convertListSaveDateToDateDto(dates);

        UserDto userDto = new UserDto();
        userDto.setDates(dateDtoList);
        userDto.setUserId(appUser.getUserCode());
        userDto.setUserStatus(appUser.isUserStatus());

        Gson gson = new Gson();
        String toJson = gson.toJson(userDto);
        JSONObject jsonUser = new JSONObject();

        try {
            jsonUser = new JSONObject(toJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonView.setText(jsonUser.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonUser,
                response -> Log.i("onResponse() called. ", "Data was posted successfully"),
                error -> Log.e("onErrorResponse: ", error.toString()));

        requestQueue.add(jsonObjectRequest);
    }
}