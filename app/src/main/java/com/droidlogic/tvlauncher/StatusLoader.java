package com.droidlogic.tvlauncher;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatusLoader {
    private final static String TAG = "StatusLoader";
    public static final String ICON ="item_icon";

    private Context mContext;
    private ConnectivityManager mConnectivityManager;
    private WifiManager mWifiManager;
    private StorageManager mStorageManager;
    private int wifi_level;
    private boolean isSdcardExist;
    private boolean isUdiskExist;
    private boolean isEthernetOn;

    private List<ArrayMap<String, Object>> storage;

    public StatusLoader(Context context) {
        mContext = context;
        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        storage = new ArrayList<ArrayMap<String, Object>>();
    }

    public  List<ArrayMap<String, Object>> getStatusData() {
        List<ArrayMap<String, Object>> list = new ArrayList<ArrayMap<String, Object>>();
        ArrayMap<String, Object> map = new ArrayMap<String, Object>();
        updateNetworkStatus();
        updateStorageStatus();

        if (wifi_level != -1) {
            switch (wifi_level + 1) {
                case 1:
                    map.put(ICON, R.drawable.wifi2);
                    break;
                case 2:
                    map.put(ICON, R.drawable.wifi3);
                    break;
                case 3:
                    map.put(ICON, R.drawable.wifi4);
                    break;
                case 4:
                    map.put(ICON, R.drawable.wifi5);
                    break;
                default:
                    break;
            }
            list.add(map);
        }

        if (isSdcardExist) {
            map = new ArrayMap<String, Object>();
            map.put(ICON, R.drawable.img_status_sdcard);
            list.add(map);
        }

        if (isUdiskExist) {
            map = new ArrayMap<String, Object>();
            map.put(ICON, R.drawable.img_status_usb);
            list.add(map);
        }

        boolean is_ethernet_on = isEthernetOn;
        if (is_ethernet_on == true) {
            map = new ArrayMap<String, Object>();
            map.put(ICON, R.drawable.img_status_ethernet);
            list.add(map);
        }

        return list;
    }

    private void updateStorageStatus() {
        List<StorageVolume> volumeList =  mStorageManager.getStorageVolumes();
        ArrayMap<String, Object> map = new ArrayMap<String, Object>();
        String mId = "";
        isSdcardExist = false;
        isUdiskExist = false;
        storage.clear();

        for (StorageVolume volume : volumeList) {
            if (null != volume && volume.isRemovable()) {
                try {
                    Class myclass = Class.forName(volume.getClass().getName());
                    Method getId = myclass.getDeclaredMethod("getId", null);
                    getId.setAccessible(true);
                    mId = (String) getId.invoke(volume);
                }catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                if (mId.startsWith("public:179")) {
                    isSdcardExist = true;
                }
                if (mId.startsWith("public:8")) {
                    isUdiskExist = true;
                }
                map.put("title", volume.getDescription(mContext));
                map.put("id", mId);
                storage.add(map);
            }
        }
    }

    private void updateNetworkStatus() {
        isEthernetOn = false;
        wifi_level = -1;
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info != null && info.getType() == ConnectivityManager.TYPE_ETHERNET) {
            isEthernetOn = true;
        }
        if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
            wifi_level = WifiManager.calculateSignalLevel(mWifiInfo.getRssi(), 4);
        }
    }

    public  String getTime(){
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        if (!DateFormat.is24HourFormat(mContext) && hour > 12) {
            hour = hour - 12;
        }

        String time = "";
        if (hour >= 10) {
            time +=  Integer.toString(hour);
        }else {
            time += "0" + Integer.toString(hour);
        }
        time += ":";

        if (minute >= 10) {
            time +=  Integer.toString(minute);
        }else {
            time += "0" +  Integer.toString(minute);
        }

        return time;
    }

    public String getDate(){
        final Calendar c = Calendar.getInstance();
        int int_Month = c.get(Calendar.MONTH);
        String mDay = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        int int_Week = c.get(Calendar.DAY_OF_WEEK) -1;
        String str_week =  mContext.getResources().getStringArray(R.array.week)[int_Week];
        String mMonth =  mContext.getResources().getStringArray(R.array.month)[int_Month];

        String date;
        if (Locale.getDefault().getLanguage().equals("zh")) {
            date = str_week + ", " + mMonth + " " + mDay + mContext.getResources().getString(R.string.str_day);
        }else if (Locale.getDefault().getLanguage().equals("ru")) {
            date = str_week + ", " + mDay + " " + mMonth;
        }else {
            date = str_week + ", " + mMonth + " " + mDay;
        }
        return date;
    }

    public List<ArrayMap<String, Object>> getStorage() {
        return storage;
    }
}
