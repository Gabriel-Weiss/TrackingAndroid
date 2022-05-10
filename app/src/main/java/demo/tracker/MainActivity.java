package demo.tracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import demo.tracker.dto.DateDto;
import demo.tracker.dto.UserDto;
import demo.tracker.entity.Date;
import demo.tracker.entity.User;
import demo.tracker.mapper.Mapper;
import demo.tracker.repository.AppRepository;
import demo.tracker.utilities.Utilities;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 10;
    private AppRepository appRepository;
    private PopupWindow popupWindow;
    private ScheduledExecutorService scheduleTaskExecutor;
    private TextView jsonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appRepository = new AppRepository();
        jsonView = findViewById(R.id.json_view);
        Button sendData = findViewById(R.id.btn_send_location);
        Button startExecutor = findViewById(R.id.start_task_btn);
        Button stopExecutor = findViewById(R.id.stop_task_btn);
        scheduleTaskExecutor = Executors
                .newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

        checkUser();
        scheduleTaskExecutor.scheduleAtFixedRate(appRepository::cleanDates, 0, 2, TimeUnit.MINUTES);
        scheduleTaskExecutor.scheduleAtFixedRate(this::getLocation, 0, 2, TimeUnit.MINUTES);

        startExecutor.setOnClickListener(v -> {
            if (scheduleTaskExecutor.isShutdown()) {
                scheduleTaskExecutor.scheduleAtFixedRate(appRepository::cleanDates, 0, 2, TimeUnit.DAYS);
                scheduleTaskExecutor.scheduleAtFixedRate(this::getLocation, 0, 2, TimeUnit.MINUTES);
            }
        });
        stopExecutor.setOnClickListener(v -> scheduleTaskExecutor.shutdownNow());
        sendData.setOnClickListener(v -> postUserData());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Locatia a fost permisa", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE
            );
        }
    }

    public void checkUser() {
        if (appRepository.getRealmUser() == null) {
            showPopUpWindow();
        }
    }

    private void showPopUpWindow() {
        LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View customView = layoutInflater.inflate(R.layout.pop_up, null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        popupWindow = new PopupWindow(customView, width, height, true);
        findViewById(R.id.main_activity).post(() -> popupWindow.showAtLocation(findViewById(R.id.main_activity), Gravity.CENTER, 0, 0));

        EditText editText = customView.findViewById(R.id.edit_text_phone);
        Button saveUser = customView.findViewById(R.id.save_user_btn);
        saveUser.setOnClickListener(v -> {
            appRepository.saveUserToRealm(editText.getText().toString());
            popupWindow.dismiss();
            postUser();
        });
    }

    void getLocation() {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                        @Override
                        public boolean isCancellationRequested() {
                            return false;
                        }

                        @NonNull
                        @Override
                        public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                            return null;
                        }
                    })
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            appRepository.saveDateToRealm(location.getLongitude(), location.getLatitude(), location.getAltitude());
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE
            );
        }

    }

    private void postUser() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = Utilities.getProperty("post_user_url", getApplicationContext());

        User appUser = appRepository.readUserFromRealm();
        JSONObject jsonUser = new JSONObject();
        try {
            jsonUser.put("userId", appUser.getUserCode());
            jsonUser.put("userPhone", appUser.getUserPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonView.setText(jsonUser.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonUser,
                response -> Log.i("onResponse() called. ", "User was posted successfully"),
                error -> Log.e("onErrorResponse: ", error.toString()));

        requestQueue.add(jsonObjectRequest);
        Log.e(TAG, "postUser: called");
    }

    public void postUserData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String url = Utilities.getProperty("post_user_url", getApplicationContext());

        User appUser = appRepository.readUserFromRealm();
        List<Date> dates = appRepository.readDatesFromRealm();
        List<DateDto> dateDtoList = Mapper.convertListSaveDateToDateDto(dates);

        UserDto userDto = new UserDto();
        userDto.setUserId(appUser.getUserCode());
        userDto.setUserPhone(appUser.getUserPhone());
        userDto.setUserStatus(appUser.isUserStatus());
        userDto.setDates(dateDtoList);

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
        Log.e(TAG, "postUserData: called");
    }
}