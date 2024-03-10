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
            control_led("/sys/devices/platform/soc/soc@03000000:meson-vfd/attr/cardled", 0);
        }
        if (isUdiskExist()) {
            ArrayMap arrayMap3 = new ArrayMap();
            arrayMap3.put("item_icon", Integer.valueOf((int) R.drawable.img_status_usb));
            arrayList.add(arrayMap3);
        }
        if (!isUdiskExist()) {
            control_led("/sys/devices/platform/soc/soc@03000000:meson-vfd/attr/usbled", 0);
        }
        if (isEthernetOn()) {
            ArrayMap arrayMap4 = new ArrayMap();
            arrayMap4.put("item_icon", Integer.valueOf((int) R.drawable.img_status_ethernet));
            arrayList.add(arrayMap4);
        }
        return arrayList;
    }

    private boolean isSdcardExist() {
        List<VolumeInfo> volumes = this.mStorageManager.getStorageVolumes();
        Collections.sort(volumes, VolumeInfo.getDescriptionComparator());
        for (VolumeInfo volumeInfo : volumes) {
            if (volumeInfo != null && volumeInfo.isMountedReadable() && volumeInfo.getType() == 0 && volumeInfo.getDisk().isSd()) {
                control_led("/sys/devices/platform/soc/soc@03000000:meson-vfd/attr/cardled", 1);
                return true;
            }
        }
        return false;
    }

    private boolean isUdiskExist() {
        List<StorageVolume> volumes = this.mStorageManager.getStorageVolumes();
        Collections.sort(volumes, VolumeInfo.getDescriptionComparator());
        for (StorageVolume volumeInfo : volumes) {
            if (volumeInfo != null && volumeInfo.isMountedReadable() && volumeInfo.getType() == 0 && volumeInfo.getDisk().isUsb()) {
                control_led("/sys/devices/platform/soc/soc@03000000:meson-vfd/attr/usbled", 1);
                return true;
            }
        }
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
                        e = e2;
                        dataOutputStream = dataOutputStream2;
                        Log.d("runtime_exception:", e.getMessage());
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
                    th = th2;
                }
            } catch (Exception e5) {
                e = e5;
            }
        } catch (Exception e6) {
            e = e6;
            process = null;
        } catch (Throwable th3) {
            th = th3;
            process = null;
        }
    }

    public String getTime() {
        String str;
        Calendar calendar = Calendar.getInstance();
        int i = calendar.get(11);
        int i2 = calendar.get(12);
        if (!DateFormat.is24HourFormat(this.mContext) && i > 12) {
            i -= 12;
        }
        if (i >= 10) {
            str = "" + Integer.toString(i);
        } else {
            str = "0" + Integer.toString(i);
        }
        String str2 = str + ":";
        if (i2 >= 10) {
            return str2 + Integer.toString(i2);
        }
        return str2 + "0" + Integer.toString(i2);
    }

    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        int i = calendar.get(2);
        String num = Integer.toString(calendar.get(1));
        String num2 = Integer.toString(calendar.get(5));
        String str = this.mContext.getResources().getStringArray(R.array.week)[calendar.get(7) - 1];
        String str2 = this.mContext.getResources().getStringArray(R.array.month)[i];
        if (Locale.getDefault().getLanguage().equals("zh")) {
            return str + ", " + str2 + " " + num2 + this.mContext.getResources().getString(R.string.str_day);
        }
        return num + "." + str2 + "." + num2 + " " + str;
    }
}
