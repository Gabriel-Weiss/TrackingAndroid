package demo.tracker.utilities;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utilities {
    private static final String TAG = "Utilities";

    public static String getProperty(String key, Context context) {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream;
        try {
            inputStream = assetManager.open("configuration.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "getProperty() called. Error: ", e);
        }
        return properties.getProperty(key);

    }
}
