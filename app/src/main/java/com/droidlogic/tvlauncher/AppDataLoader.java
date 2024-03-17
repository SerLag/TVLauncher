package com.droidlogic.tvlauncher;

import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
/*import android.os.Handler;
import android.os.Message;*/
import android.content.Context;
/*import android.content.ComponentName;*/
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
/*import java.io.InputStream;*/
import java.io.InputStreamReader;
/*import java.io.FileInputStream;
import java.io.FileOutputStream;*/
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
/*import java.util.Collections;*/
import java.text.Collator;


public class AppDataLoader {
    private final static String TAG = "AppDataLoader";
    public final static String NAME = "name";
    public final static String ICON = "icon";
    public final static String INTENT = "intent";

    public final static String SHORTCUT_PATH = "/data/data/com.droidlogic.tvlauncher/shortcut.cfg";
  //  public final static int DEFAULT_SHORTCUR_PATH = R.raw.default_shortcut;
    public final static String HOME_SHORTCUT_HEAD = "Home_Shortcut:";
    public final static String LOCAL_SHORTCUT_HEAD = "Local_Shortcut:";

    private Context mContext;
/*    private LauncherApps mLauncherApps;
    private ActivityManager mActivityManager;*/
  //    private String str_homeShortcut;
  //  private String str_localShortcut;

    ///////////////
    private PackageManager mPackageManager;

    private String[] list_homeShortcut;
    private String[] list_localShortcut;

    List<ArrayMap<String, Object>> homeShortCuts = new ArrayList<ArrayMap<String, Object>>();
    List<ArrayMap<String, Object>> appShortCuts = new ArrayList<ArrayMap<String, Object>>();
    List<ArrayMap<String, Object>> localShortCuts = new ArrayList<ArrayMap<String, Object>>();

    private boolean isLoaded = false;
    private Object mLock;

    public AppDataLoader(Context context) {
        mContext = context;
        mPackageManager = mContext.getPackageManager();
        getShortcuts();
/*        mLauncherApps = (LauncherApps)mContext.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        mActivityManager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);*/
        mLock = ((Launcher) mContext).getLock();
    }

    public void update() {
        isLoaded = false;
        new Thread(new Runnable() {
            public void run() {
                synchronized (mLock) {
  //                  loadCustomApps();
                    loadShortcutList();
                    isLoaded = true;
                }
            }
        }).start();
    }


/*
    private void loadCustomApps() {
        String[] list = null;
        File mFile = new File(mContext.getFilesDir(),"local_shortcuts");
        try {
            BufferedReader b = new BufferedReader(new FileReader(mFile));
            if (b.read() == -1) {
                getShortcuts();
            }
            if (b != null)
                b.close();
        } catch (IOException e) {
        }




        ////////////
        char[] str = new char[1024];
        BufferedReader br = null;
        File mFile = new File("home_shortcuts");
        try {
            if (mFile.exists()) {
                br = new BufferedReader(new FileReader(mFile));
            } else {
                br = new BufferedReader(new InputStreamReader(mContext.getResources().getIdentifier().openRawResource(R.raw.home_shortcuts)));
            }
            int i = br.read(str);
            list_homeShortcut = str.toString().split("\n");
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mFile = new File("local_shortcuts");
        try {
            if (mFile.exists()) {
                br = new BufferedReader(new FileReader(mFile));
            } else {
                br = new BufferedReader(new InputStreamReader(mContext.getResources().openRawResource(R.raw.local_shortcuts)));
            }
            int i = br.read(str);
            list_localShortcut = str.toString().split("\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/

    public void saveShortcut(int mode, String str_apps){
        synchronized (mLock) {
            File mFile = new File(SHORTCUT_PATH);
            if (!mFile.exists()) {
                try {
                    mFile.createNewFile();
                }
                catch (Exception e) {
                    Log.e(TAG, e.getMessage().toString());
                }
            }

            BufferedReader br = null;
            BufferedWriter bw = null;
            try {
                br = new BufferedReader(new FileReader(mFile));
                String str = null;
                List list = new ArrayList();

                while ( (str=br.readLine()) != null ) {
                    list.add(str);
                }

                if (list.size() == 0) {
                    list.add(HOME_SHORTCUT_HEAD);
                    list.add(LOCAL_SHORTCUT_HEAD);
                }
                bw = new BufferedWriter(new FileWriter(mFile));
                for ( int i = 0;i < list.size(); i++ ) {
                    if (list.get(i).toString().startsWith(parseShortcutHead(mode))) {
                        str_apps =  parseShortcutHead(mode) + str_apps;
                        bw.write(str_apps);
                    } else {
                        bw.write(list.get(i).toString());
                    }
                    bw.newLine();
                }
                bw.flush();
                bw.close();
            }
            catch (Exception e) {
                Log.d(TAG, "   " + e);
            } finally {
                try {
                    if (br != null)
                        br.close();
                } catch (IOException e) {
                }
                try {
                    if (bw != null)
                        bw.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void getShortcuts() {
        String str;
        File mFile;
        mFile = new File(mContext.getFilesDir(),"home_shortcuts");
        try {
            BufferedReader br = new BufferedReader(new FileReader(mFile));
            str = br.readLine();
            list_homeShortcut = str.split(";");
        } catch (IOException e) {
            Log.e("MediaBoxLauncher", "Failed read home_shortcuts:" + e);
        }
        mFile = new File(mContext.getFilesDir(),"local_shortcuts");
        try {
            BufferedReader br = new BufferedReader(new FileReader(mFile));
            str = br.readLine();
            list_localShortcut = str.split(";");
        } catch (IOException e) {
            Log.e("MediaBoxLauncher", "Failed read local_shortcuts:" + e);
        }
    }

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
                application.label = app.activityInfo.packageName;
                if (list_homeShortcut != null) {
                    for (int j = 0; j < list_homeShortcut.length; j++) {
                        if (app.activityInfo.packageName.equals(list_homeShortcut[j])) {
                            homeShortCuts.add(buildShortcutMap(application.title.toString(),
                                    application.icon,  mPackageManager.getLaunchIntentForPackage(
                                            application.label.toString())));
                            break;
                        }
                    }
                }

                if (list_localShortcut != null) {
                    for (int j = 0; j < list_localShortcut.length; j++) {
                        if (app.activityInfo.packageName.equals(list_localShortcut[j])) {
                            localShortCuts.add(buildShortcutMap(application.title.toString(),
                                    application.icon, mPackageManager.getLaunchIntentForPackage(
                                            application.label.toString())));
                            break;
                        }
                    }
                }

                appShortCuts.add(buildShortcutMap(application.title.toString(),
                        application.icon, mPackageManager.getLaunchIntentForPackage(
                                application.label.toString())));
                application.icon.setCallback(null);
            }
        }
        homeShortCuts.add(buildAddMap());
        localShortCuts.add(buildAddMap());
    }

    private ArrayMap<String, Object> buildShortcutMap(String name, Drawable icon, Intent intent) {
        ArrayMap<String, Object> map = new ArrayMap<String, Object>();
        map.put(NAME, name);
        map.put(ICON, icon);
        map.put(INTENT, intent);

        return map;
    }

    private ArrayMap<String, Object> buildAddMap(){
        ArrayMap<String, Object> map = new ArrayMap<String, Object>();
        map.put(NAME, mContext.getResources().getString(R.string.str_add));
        map.put(ICON, R.drawable.item_img_add);

        return map;
    }

    public List<ArrayMap<String, Object>> getShortcutList(int mode) {
        synchronized (mLock) {
            switch (mode) {
                case Launcher.MODE_HOME:
                    return homeShortCuts;
                case Launcher.MODE_APP:
                    return appShortCuts;
                case Launcher.MODE_LOCAL:
                    return localShortCuts;
            }
        }
        return null;
    }

/*
    public String getShortcutString(int mode) {
        synchronized (mLock) {
            switch (mode) {
                case Launcher.MODE_HOME:
                    return str_homeShortcut;
                case Launcher.MODE_LOCAL:
                    return str_localShortcut;
            }
        }
        return null;
    }
*/

    public boolean isDataLoaded() {
        return isLoaded;
    }

    private String parseShortcutHead (int mode) {
        switch (mode) {
            case Launcher.MODE_HOME:
                return HOME_SHORTCUT_HEAD;
            case Launcher.MODE_LOCAL:
                return LOCAL_SHORTCUT_HEAD;
        }
        return null;
    }

/*    private int parsePackageIcon(String packageName){
        if (packageName.equals("com.droidlogic.FileBrower")) {
            return R.drawable.icon_filebrowser;
        } else if (packageName.equals("com.android.browser")) {
            return R.drawable.icon_browser;
        } else if (packageName.equals("com.droidlogic.appinstall")) {
            return R.drawable.icon_appinstaller;
        } else if (packageName.equals("com.android.tv.settings")) {
            return R.drawable.icon_setting;
        } else if (packageName.equals("com.droidlogic.mediacenter")){
            return R.drawable.icon_mediacenter;
        } else if (packageName.equals("com.droidlogic.otaupgrade")) {
            return R.drawable.icon_backupandupgrade;
        } else if (packageName.equals("com.android.gallery3d")) {
            return R.drawable.icon_pictureplayer;
        } else if (packageName.equals("com.droidlogic.miracast")) {
            return R.drawable.icon_miracast;
        } else if (packageName.equals("com.droidlogic.PPPoE")) {
            return R.drawable.icon_pppoe;
        } else if (packageName.equals("com.android.music")) {
            return R.drawable.icon_music;
        } else if (packageName.equals("com.android.camera2")) {
            return R.drawable.icon_camera;
        }
        return -1;
    }*/
}
