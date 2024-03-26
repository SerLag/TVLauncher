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

    private Context mContext;
    private PackageManager mPackageManager;

    List<String> list_homeShortcut;
    List<String> list_localShortcut;

    List<ArrayMap<String, Object>> homeShortCuts = new ArrayList<ArrayMap<String, Object>>();
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
        list_localShortcut.clear();
        for ( ArrayMap localShortCut :localShortCuts) {
            if (!localShortCut.get("name").equals(mContext.getResources().getString(R.string.str_add))) {
                str = str + localShortCut.get("label").toString() + ";";
                list_localShortcut.add(str);
            }
        }
        mFile = new File(mContext.getFilesDir(), "local_shortcuts");
        try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(mFile));
                bw.write(str);
                bw.close();
            }
            catch (Exception e) {
                Log.e("MediaBoxLauncher", "Failed write local_shortcuts:" + e);
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
            Log.e("MediaBoxLauncher", "Failed read home_shortcuts:" + e);
        }
        mFile = new File(mContext.getFilesDir(), "local_shortcuts");
        try {
            BufferedReader br = new BufferedReader(new FileReader(mFile));
            str = br.readLine();
            list_localShortcut = new ArrayList<>(Arrays.asList(str.split(";")));
        } catch (IOException e) {
            Log.e("MediaBoxLauncher", "Failed read local_shortcuts:" + e);
        }
    }

/*
    private static final Comparator<LauncherActivityInfo> getAppNameComparator() {
        final Collator collator = Collator.getInstance();
        return new Comparator<LauncherActivityInfo>() {
            public final int compare(LauncherActivityInfo a, LauncherActivityInfo b) {
                if (a.getUser().equals(b.getUser())) {
                    int result = collator.compare(a.getLabel().toString(), b.getLabel().toString());
                    if (result == 0) {
                        result = a.getName().compareTo(b.getName());
                    }
                    return result;
                } else {
                    // TODO: Order this based on profile type rather than string compares.
                    return a.getUser().toString().compareTo(b.getUser().toString());
                }
            }
        };
    }
*/

    private void loadShortcutList() {
        homeShortCuts.clear();
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
                if (list_homeShortcut != null) {
                    for (String homeShortcut : list_homeShortcut) {
                        if (app.activityInfo.packageName.equals(homeShortcut)) {
                            homeShortCuts.add(buildShortcutMap(application.title.toString(),
                                    application.intent, application.icon, application.componentName));
                            break;
                        }
                    }
                }

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
