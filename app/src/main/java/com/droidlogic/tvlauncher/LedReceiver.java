package com.droidlogic.tvlauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.storage.StorageManager;
/*import android.os.storage.VolumeInfo;*/
import android.util.Log;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public class LedReceiver extends BroadcastReceiver {
    private StorageManager mStorageManager;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            return;
        }
        intent.getData().getPath();
     //   String string = context.getResources().getString(R.string.usb_led);
     //   String string2 = context.getResources().getString(R.string.card_led);
        Log.d("LedReceiver", "~~~~~~~~~~OnReceive!!!");
        if (isSdcardExist()) {
    //        LedControl.control_led_status(string2, true);
        } else {
     //       LedControl.control_led_status(string2, false);
        }
        if (isUdiskExist()) {
    //        LedControl.control_led_status(string, true);
        } else {
     //       LedControl.control_led_status(string, false);
        }
    }

    private boolean isSdcardExist() {
/*        List<VolumeInfo> volumes = this.mStorageManager.getVolumes();
        Collections.sort(volumes, VolumeInfo.getDescriptionComparator());
        for (VolumeInfo volumeInfo : volumes) {
            if (volumeInfo != null && volumeInfo.isMountedReadable() && volumeInfo.getType() == 0 && volumeInfo.getDisk().isSd()) {
                return true;
            }
        }*/
        return false;
    }

    private boolean isUdiskExist() {
/*        List<VolumeInfo> volumes = this.mStorageManager.getVolumes();
        Collections.sort(volumes, VolumeInfo.getDescriptionComparator());
        for (VolumeInfo volumeInfo : volumes) {
            if (volumeInfo != null && volumeInfo.isMountedReadable() && volumeInfo.getType() == 0 && volumeInfo.getDisk().isUsb()) {
                return true;
            }
        }*/
        return false;
    }
}
