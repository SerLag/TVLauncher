package com.droidlogic.tvlauncher;

import android.app.ActivityManager;
import android.content.Context;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class MemoryManager {
    public static long getTotalMemory() {
        try {
            FileReader fileReader = new FileReader("/proc/meminfo");
            BufferedReader bufferedReader = new BufferedReader(fileReader, 8192);
            long intValue = Integer.valueOf(bufferedReader.readLine().split("\\s+")[1]).intValue();
            bufferedReader.close();
            fileReader.close();
            return intValue / 1024;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e2) {
            e2.printStackTrace();
            return 0;
        }
    }

    public static long getAvailMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem / 1048576;
    }

    public static void cleanMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        if (runningAppProcesses != null) {
            for (int i = 0; i < runningAppProcesses.size(); i++) {
                ActivityManager.RunningAppProcessInfo runningAppProcessInfo = runningAppProcesses.get(i);
                String[] strArr = runningAppProcessInfo.pkgList;
                if (runningAppProcessInfo.importance > 300) {
                    for (String str : strArr) {
                        activityManager.killBackgroundProcesses(str);
                    }
                }
            }
        }
    }
}
