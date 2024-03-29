package com.droidlogic.tvlauncher;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class StatusLoader {
    private final static String TAG = "StatusLoader";
    private ConnectivityManager mConnectivityManager;
    private Context mContext;
    private StorageManager mStorageManager;
    private WifiManager mWifiManager;
    private final String STORAGE_PATH = "/storage";
    private final String SDCARD_FILE_NAME = "sdcard";
    private final String UDISK_FILE_NAME = "udisk";

    public StatusLoader(Context context) {
        this.mContext = context;
  //      this.mConnectivityManager = (ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
  //      this.mWifiManager = (WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE);
        this.mStorageManager = (StorageManager) this.mContext.getSystemService(Context.STORAGE_SERVICE);
    }

    public  List<ArrayMap<String, Object>> getStatusData() {
        List<ArrayMap<String, Object>> list = new ArrayList<>();
        ArrayMap<String, Object> map = new ArrayMap<>();
        int wifi_level = getWifiLevel();

        if (wifi_level != -1) {
            switch (wifi_level + 1) {
                case 1:
                    map.put("item_icon", R.drawable.wifi2);
                    break;
                case 2:
                    map.put("item_icon", R.drawable.wifi3);
                    break;
                case 3:
                    map.put("item_icon", R.drawable.wifi4);
                    break;
                case 4:
                    map.put("item_icon", R.drawable.wifi5);
                    break;
                default:
                    break;
            }
            list.add(map);
        }

        if (isSdcardExist()) {
            map = new ArrayMap<String, Object>();
            map.put("item_icon", R.drawable.img_status_sdcard);
            list.add(map);
        }

        if (isUdiskExist()) {
            map = new ArrayMap<String, Object>();
            map.put("item_icon", R.drawable.img_status_usb);
            list.add(map);
        }

        if (isEthernetOn()) {
            map = new ArrayMap<String, Object>();
            map.put("item_icon", R.drawable.img_status_ethernet);
            list.add(map);
        }

        return list;
    }

    private boolean isSdcardExist() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StorageManager manager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
            List<StorageVolume> volumes = manager.getStorageVolumes();
            for (StorageVolume sv : volumes) {
                Log.d("IKE", sv.getState());
/*            Log.d("IKE", sv.getUuid() + " " + sv.getState().);
            if (sv..getPath().equals(this.path)) {
                if (sv.getId().startsWith("public:179")) {
                    return true;
                }
                if (sv.getId().startsWith("public:8")) {
                    Log.d("IKE", "usb device plug in");
                    return false;
                }*/
                //        }
            }
            return false;
        }
        return false;
    }

    private boolean isUdiskExist() {
        StorageManager manager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> volumes =  manager.getStorageVolumes();
        for (StorageVolume sv : volumes) {
            Log.d("IKE", sv.getUuid());
/*            Log.d("IKE", sv.getId() + " " + sv.getPath());
            if (sv.getPath().equals(this.path)) {
                if (sv.getId().startsWith("public:179")) {
                    return false;
                }
                if (sv.getId().startsWith("public:8")) {
                    Log.d("IKE", "usb device plug in");
                    return true;
                }
            }*/
        }
        return false;
    }

    private int getWifiLevel() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connectivity.getActiveNetworkInfo();
        if (mWifi != null && mWifi.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
            int wifi_rssi = mWifiInfo.getRssi();
            return WifiManager.calculateSignalLevel(wifi_rssi, 4);
        }
        return -1;
    }

    private boolean isEthernetOn() {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info != null && info.getType() == ConnectivityManager.TYPE_ETHERNET) {
            return true;
        }
        return false;
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
        }else {
            date = str_week + ", " + mMonth + " " + mDay;
        }

        return date;
    }
}
