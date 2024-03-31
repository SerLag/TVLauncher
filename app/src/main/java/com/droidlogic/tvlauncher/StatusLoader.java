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
    private ConnectivityManager mConnectivityManager;
    private Context mContext;
    private StorageManager mStorageManager;
    private WifiManager mWifiManager;
    private final String STORAGE_PATH = "/storage";
    private final String SDCARD_FILE_NAME = "sdcard";
    private final String UDISK_FILE_NAME = "udisk";

    public StatusLoader(Context context) {
        mContext = context;
        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
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

        if (isEthernetOn()) {
            map = new ArrayMap<String, Object>();
            map.put("item_icon", R.drawable.img_status_ethernet);
            list.add(map);
        }

        // Check removable drives
        List<StorageVolume> volumeList =  mStorageManager.getStorageVolumes();
        for (StorageVolume volume : volumeList) {
            if (null != volume && volume.isRemovable()) {
/*
                String label = volume.getDescription(mContext); // This is actually the name of the U disk
                String status = volume.getState(); // The state is mounted, such as: mounted, unmounted
                Boolean isemulated = volume.isEmulated(); // Is it an internal storage device
                Boolean isremovable = volume.isRemovable(); // is whether it is a removable external storage device
                String mPath = ""; // Path of the device
                String mId = "";
*/
                String mId = "";
                try {
                    Class myclass = Class.forName(volume.getClass().getName());
                    Method getId = myclass.getDeclaredMethod("getId", null);
                    getId.setAccessible(true);
                    mId = (String) getId.invoke(volume);
                }catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
                if (mId.startsWith("public:179")) {
                    map = new ArrayMap<String, Object>();
                    map.put("item_icon", R.drawable.img_status_sdcard);
                    list.add(map);
                }
                if (mId.startsWith("public:8")) {
                    map = new ArrayMap<String, Object>();
                    map.put("item_icon", R.drawable.img_status_usb);
                    list.add(map);
                }

/*                    Log.i(TAG, "name:" + label);
                    Log.i(TAG, "status:" + status);
                    Log.i(TAG, "isEmulated:" + isemulated);
                    Log.i(TAG, "isRemovable:" + isremovable);
                    Log.i(TAG, "mPath:" + mPath);
                    Log.i(TAG, "mId:" + mId);*/
            }
        }

/*
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


*/

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
        List<StorageVolume> volumeList =  manager.getStorageVolumes();
        for (StorageVolume volume : volumeList) {
            if (null != volume && volume.isRemovable()) {
                String label = volume.getDescription(mContext); // This is actually the name of the U disk
                String status = volume.getState (); // The state is mounted, such as: mounted, unmounted
                Boolean isemulated = volume.isEmulated (); // Is it an internal storage device
                Boolean isremovable = volume.isRemovable (); // is whether it is a removable external storage device
                String mPath = ""; // Path of the device
                String mId = "";


                try {
                    Class myclass = Class.forName(volume.getClass().getName());
                    Method getPath =  myclass.getDeclaredMethod("getPath",null);
                    getPath.setAccessible(true);
                    mPath = (String) getPath.invoke(volume);

                    Method getId =  myclass.getDeclaredMethod("getId",null);
                    getId.setAccessible(true);
                    mId = (String) getId.invoke(volume);

                    Log.i(TAG,"name:"+label);
                    Log.i(TAG,"status:"+status);
                    Log.i(TAG,"isEmulated:"+isemulated);
                    Log.i(TAG,"isRemovable:"+isremovable);
                    Log.i(TAG,"mPath:"+mPath);
                    Log.i(TAG,"mId:"+mId);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }catch (InvocationTargetException e) {
                    e.printStackTrace();
                }catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
/*
        try {
            Method method_volumeList = StorageManager.class.getMethod("getVolumeList");
            method_volumeList.setAccessible(true);
            Object[] volumeList = (Object[]) method_volumeList.invoke(manager);
            String xxx = (String) volumeList[1].getClass().getMethod("getPath").invoke(volumeList[1]);
            String yyy = (String) volumeList[1].getClass().getMethod("getId").invoke(volumeList[1]);


            Log.d("IKE", xxx + yyy);
        }catch (Exception e1) {
            e1.printStackTrace();
        }
        for (StorageVolume sv : volumes) {
            Log.d("IKE", "");
*/
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
       // }
        return false;
    }

    private int getWifiLevel() {
   //     ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = mConnectivityManager.getActiveNetworkInfo();
        if (mWifi != null && mWifi.getType() == ConnectivityManager.TYPE_WIFI) {
 //           WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
            int wifi_rssi = mWifiInfo.getRssi();
            return WifiManager.calculateSignalLevel(wifi_rssi, 4);
        }
        return -1;
    }

    private boolean isEthernetOn() {
   //     ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
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
