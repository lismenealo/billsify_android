package com.example.billsyfy.ui.BillsGallery;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.HashMap;

import androidx.core.app.ActivityCompat;

public class Helper {


    static final String KEY_CATEGORY = "category";
    static final String KEY_PATH = "path";
    static final String KEY_TIMESTAMP = "timestamp";
    static final String KEY_DESC = "description";
    static final String KEY_AMOUNT = "amount";

    //Verify permissions
    public static  boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public static HashMap<String, String> mappingInbox(String category, String path, String timestamp, String description, String amount)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(KEY_CATEGORY, category);
        map.put(KEY_PATH, path);
        map.put(KEY_TIMESTAMP, timestamp);
        map.put(KEY_DESC, description);
        map.put(KEY_AMOUNT, amount);
        return map;
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

}
