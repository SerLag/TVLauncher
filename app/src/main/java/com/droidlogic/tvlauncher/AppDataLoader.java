package com.droidlogic.tvlauncher;

import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.Context;
import android.content.ComponentName;

import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.text.Collator;


public class AppDataLoader {
    private final static String TAG = "AppDataLoader";

    private Context mContext;
    private PackageManager mPackageManager;

    List<String> list_homeShortcut;
    List<String> list_localShortcut;

    List<ArrayMap<String, Object>> appShortCuts = new ArrayList<ArrayMap<String, Object>>();
    List<ArrayMap<String, Object>> localShortCuts = new ArrayList<ArrayMap<String, Object>>();

    private boolean isLoaded = false;
    private final Object mLock;

    public AppDataLoader(Context context) {
        mContext = context;
        mPackageManager = mContext.getPackageManager();
        getShortcuts();
        mLock = ((Launcher) mContext).getLock();
    }

    public void update() {
        isLoaded = false;
        new Thread(new Runnable() {
            public void run() {
                synchronized (mLock) {
                    loadShortcutList();
                    isLoaded = true;
                }
            }
        }).start();
    }

    public void saveShortcuts(){
        String str = "";
        File mFile;
        for ( String localShortCut :list_localShortcut) {
            str = str +  localShortCut + ";";
        }
        mFile = new File(mContext.getFilesDir(), "local_shortcuts");
        try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(mFile));
                bw.write(str);
                bw.close();
            }
            catch (Exception e) {
                Log.e(TAG, "Failed write local_shortcuts:" + e);
            }
    }

    private void getShortcuts() {
        String str;
        File mFile;
        mFile = new File(mContext.getFilesDir(), "home_shortcuts");
        try {
            BufferedReader br = new BufferedReader(new FileReader(mFile));
            str = br.readLine();
            list_homeShortcut = new ArrayList<>(Arrays.asList(str.split(";")));
        } catch (IOException e) {
            Log.e(TAG, "Failed read home_shortcuts:" + e);
        }
        mFile = new File(mContext.getFilesDir(), "local_shortcuts");
        try {
            BufferedReader br = new BufferedReader(new FileReader(mFile));
            str = br.readLine();
            list_localShortcut = new ArrayList<>(Arrays.asList(str.split(";")));
        } catch (IOException e) {
            Log.e(TAG, "Failed read local_shortcuts:" + e);
        }
    }

    private void loadShortcutList() {
        appShortCuts.clear();
        localShortCuts.clear();

        Intent AppsIntent = new Intent(Intent.ACTION_MAIN, null);
        AppsIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> apps = mPackageManager.queryIntentActivities(AppsIntent, 0);

        if (apps != null) {
            for (ResolveInfo app : apps) {
                ApplicationInfo application = new ApplicationInfo();
                application.title = app.loadLabel(mPackageManager);
                application.icon = app.loadIcon(mPackageManager);
                application.setActivity(new ComponentName(app.activityInfo.applicationInfo.packageName, app.activityInfo.name),
                        Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                if (list_localShortcut != null) {
                    for (String localShortcut : list_localShortcut) {
                        if (app.activityInfo.packageName.equals(localShortcut)) {
                            localShortCuts.add(buildShortcutMap(application.title.toString(),
                                    application.intent, application.icon, application.componentName));
                            break;
                        }
                    }
                }

                appShortCuts.add(buildShortcutMap(application.title.toString(),
                        application.intent, application.icon, application.componentName));
                application.icon.setCallback(null);
            }
        }
        localShortCuts.add(buildAddMap());
    }

    private ArrayMap<String, Object> buildShortcutMap(String name, Intent i, Drawable icon, ComponentName c) {
        ArrayMap<String, Object> map = new ArrayMap<String, Object>();
        map.put("name", name);
        map.put("intent", i);
        map.put("icon", icon);
        map.put("component", c);
        return map;
    }

    private ArrayMap<String, Object> buildAddMap(){
        ArrayMap<String, Object> map = new ArrayMap<String, Object>();
        map.put("name", mContext.getResources().getString(R.string.str_add));
        map.put("intent", null);
        map.put("icon", R.drawable.item_img_add);
        return map;
    }

    public List<ArrayMap<String, Object>> getappShortCuts() {
        return appShortCuts;
    }

    public List<ArrayMap<String, Object>> getlocalShortCuts() {
         return localShortCuts;
    }

    public boolean isDataLoaded() {
        return isLoaded;
    }
}
