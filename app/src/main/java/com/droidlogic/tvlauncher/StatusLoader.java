package com.droidlogic.tvlauncher;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.storage.VolumeInfo;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.Log;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class StatusLoader {
    private ConnectivityManager mConnectivityManager;
    private Context mContext;
    private StorageManager mStorageManager;
    private WifiManager mWifiManager;
    private final String STORAGE_PATH = "/storage";
    private final String SDCARD_FILE_NAME = "sdcard";
    private final String UDISK_FILE_NAME = "udisk";

    public StatusLoader(Context context) {
        this.mContext = context;
        this.mConnectivityManager = (ConnectivityManager) this.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.mWifiManager = (WifiManager) this.mContext.getSystemService(Context.WIFI_SERVICE);
        this.mStorageManager = (StorageManager) this.mContext.getSystemService(Context.STORAGE_SERVICE);
    }

    public List<ArrayMap<String, Object>> getStatusData() {
        ArrayList arrayList = new ArrayList();
        ArrayMap arrayMap = new ArrayMap();
        int wifiLevel = getWifiLevel();
        if (wifiLevel != -1) {
            int i = wifiLevel + 1;
            if (i == 1) {
                arrayMap.put("item_icon", Integer.valueOf((int) R.drawable.wifi2));
            } else if (i == 2) {
                arrayMap.put("item_icon", Integer.valueOf((int) R.drawable.wifi3));
            } else if (i == 3) {
                arrayMap.put("item_icon", Integer.valueOf((int) R.drawable.wifi4));
            } else if (i == 4) {
                arrayMap.put("item_icon", Integer.valueOf((int) R.drawable.wifi5));
            }
            arrayList.add(arrayMap);
        }
        if (isSdcardExist()) {
            ArrayMap arrayMap2 = new ArrayMap();
            arrayMap2.put("item_icon", Integer.valueOf((int) R.drawable.img_status_sdcard));
            arrayList.add(arrayMap2);
        }
        if (!isSdcardExist()) {
         //   control_led("/sys/devices/platform/soc/soc@03000000:meson-vfd/attr/cardled", 0);
        }
        if (isUdiskExist()) {
            ArrayMap arrayMap3 = new ArrayMap();
            arrayMap3.put("item_icon", Integer.valueOf((int) R.drawable.img_status_usb));
            arrayList.add(arrayMap3);
        }
        if (!isUdiskExist()) {
         //   control_led("/sys/devices/platform/soc/soc@03000000:meson-vfd/attr/usbled", 0);
        }
        if (isEthernetOn()) {
            ArrayMap arrayMap4 = new ArrayMap();
            arrayMap4.put("item_icon", Integer.valueOf((int) R.drawable.img_status_ethernet));
            arrayList.add(arrayMap4);
        }
        return arrayList;
    }

    private boolean isSdcardExist() {
/*        List<StorageVolume> volumes = this.mStorageManager.getStorageVolumes();
        Collections.sort(volumes, StorageVolume.);
        for (VolumeInfo volumeInfo : volumes) {
            if (volumeInfo != null && volumeInfo.isMountedReadable() && volumeInfo.getType() == 0 && volumeInfo.getDisk().isSd()) {
            //    control_led("/sys/devices/platform/soc/soc@03000000:meson-vfd/attr/cardled", 1);
                return true;
            }
        }*/
/*        List<StorageVolume> volumes = this.mStorageManager.getStorageVolumes();
        for (StorageVolume volume : volumes) {
            if (volume.isRemovable() && volume.) == )
        }*/
        return false;
    }

    private boolean isUdiskExist() {
/*        List<StorageVolume> volumes = this.mStorageManager.getStorageVolumes();
        Collections.sort(volumes, StorageVolume.getDescriptionComparator());
        for (StorageVolume volumeInfo : volumes) {
            if (volumeInfo != null && volumeInfo.isMountedReadable() && volumeInfo.getType() == 0 && volumeInfo.getDisk().isUsb()) {
            //    control_led("/sys/devices/platform/soc/soc@03000000:meson-vfd/attr/usbled", 1);
                return true;
            }
        }*/
        return false;
    }

    private int getWifiLevel() {
        if (this.mConnectivityManager.getNetworkInfo(1).isConnected()) {
            return WifiManager.calculateSignalLevel(this.mWifiManager.getConnectionInfo().getRssi(), 4);
        }
        return -1;
    }

    private boolean isEthernetOn() {
        NetworkInfo networkInfo = this.mConnectivityManager.getNetworkInfo(9);
        return networkInfo != null && networkInfo.isConnected();
    }
/*
    public static boolean control_led(String str, int i) {
        Process process;
        DataOutputStream dataOutputStream = null;
        try {
            String str2 = "echo " + i + " > " + str;
            process = Runtime.getRuntime().exec("sh");
            try {
                try {
                    DataOutputStream dataOutputStream2 = new DataOutputStream(process.getOutputStream());
                    try {
                        dataOutputStream2.writeBytes(str2 + "\n");
                        dataOutputStream2.writeBytes("exit\n");
                        dataOutputStream2.flush();
                        process.waitFor();
                        try {
                            dataOutputStream2.close();
                            process.destroy();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    } catch (Exception e2) {
                        //    e = e2;
                        dataOutputStream = dataOutputStream2;
                        Log.d("runtime_exception:", e2.getMessage());
                        if (dataOutputStream != null) {
                            try {
                                dataOutputStream.close();
                            } catch (Exception e3) {
                                e3.printStackTrace();
                                return false;
                            }
                        }
                        process.destroy();
                        return false;
                    } catch (Throwable th) {
                        th = th;
                        dataOutputStream = dataOutputStream2;
                        if (dataOutputStream != null) {
                            try {
                                dataOutputStream.close();
                            } catch (Exception e4) {
                                e4.printStackTrace();
                                throw th;
                            }
                        }
                        process.destroy();
                        throw th;
                    }
                } catch (Throwable th2) {
                    //  th = th2;
                }
            } catch (Exception e5) {
                // e = e5;
            }
        } catch (Exception e6) {
            // e = e6;
            process = null;
        } catch (Throwable th3) {
            //th = th3;
            process = null;
        }
    }
*/

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
