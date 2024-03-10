package com.droidlogic.tvlauncher;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.util.ArrayMap;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* loaded from: classes.dex */
public class AppDataLoader {
    private String[] list_homeShortcut;
    private String[] list_localShortcut;
    private String[] list_musicShortcut;
    private String[] list_recommendShortcut;
    private String[] list_videoShortcut;
    private ActivityManager mActivityManager;
    private Context mContext;
    private LauncherApps mLauncherApps;
    private Object mLock;
    private String str_homeShortcut;
    private String str_localShortcut;
    private String str_musicShortcut;
    private String str_recommendShortcut;
    private String str_videoShortcut;
    List<ArrayMap<String, Object>> homeShortCuts = new ArrayList();
    List<ArrayMap<String, Object>> videoShortCuts = new ArrayList();
    List<ArrayMap<String, Object>> recommendShorts = new ArrayList();
    List<ArrayMap<String, Object>> appShortCuts = new ArrayList();
    List<ArrayMap<String, Object>> musicShortCuts = new ArrayList();
    List<ArrayMap<String, Object>> localShortCuts = new ArrayList();
    private boolean isLoaded = false;

    private String parseShortcutHead(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        if (i != 5) {
                            return null;
                        }
                        return "Local_Shortcut:";
                    }
                    return "Music_shortcut:";
                }
                return "Recommend_Shortcut:";
            }
            return "Video_Shortcut:";
        }
        return "Home_Shortcut:";
    }

    public AppDataLoader(Context context) {
        this.mContext = context;
        this.mLauncherApps = (LauncherApps) this.mContext.getSystemService("launcherapps");
        this.mActivityManager = (ActivityManager) this.mContext.getSystemService("activity");
        this.mLock = ((Launcher) this.mContext).getLock();
    }

    public void update() {
        this.isLoaded = false;
        new Thread(new Runnable() { // from class: com.droidlogic.tvlauncher.AppDataLoader.1
            @Override // java.lang.Runnable
            public void run() {
                synchronized (AppDataLoader.this.mLock) {
                    AppDataLoader.this.loadCustomApps();
                    AppDataLoader.this.loadShortcutList();
                    AppDataLoader.this.isLoaded = true;
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x00f9, code lost:
        if (r6 == null) goto L45;
     */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0100 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public java.lang.String[] loadCustomApps() {
        /*
            Method dump skipped, instructions count: 260
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.droidlogic.tvlauncher.AppDataLoader.loadCustomApps():java.lang.String[]");
    }

    /* JADX WARN: Code restructure failed: missing block: B:48:0x00df, code lost:
        if (r5 == null) goto L50;
     */
    /* JADX WARN: Removed duplicated region for block: B:71:0x00ed A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:75:0x00e8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void saveShortcut(int r8, java.lang.String r9) {
        /*
            Method dump skipped, instructions count: 244
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.droidlogic.tvlauncher.AppDataLoader.saveShortcut(int, java.lang.String):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:55:0x00a6 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x00a1 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void getShortcutFromDefault(int r5, java.lang.String r6) {
        /*
            r4 = this;
            java.io.File r0 = new java.io.File
            r0.<init>(r6)
            boolean r6 = r0.exists()
            java.lang.String r1 = "AppDataLoader"
            if (r6 != 0) goto L1d
            r0.createNewFile()     // Catch: java.lang.Exception -> L11
            goto L1d
        L11:
            r6 = move-exception
            java.lang.String r6 = r6.getMessage()
            java.lang.String r6 = r6.toString()
            android.util.Log.e(r1, r6)
        L1d:
            r6 = 0
            java.io.BufferedReader r2 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L7a java.lang.Exception -> L7d
            java.io.InputStreamReader r3 = new java.io.InputStreamReader     // Catch: java.lang.Throwable -> L7a java.lang.Exception -> L7d
            android.content.Context r4 = r4.mContext     // Catch: java.lang.Throwable -> L7a java.lang.Exception -> L7d
            android.content.res.Resources r4 = r4.getResources()     // Catch: java.lang.Throwable -> L7a java.lang.Exception -> L7d
            java.io.InputStream r4 = r4.openRawResource(r5)     // Catch: java.lang.Throwable -> L7a java.lang.Exception -> L7d
            r3.<init>(r4)     // Catch: java.lang.Throwable -> L7a java.lang.Exception -> L7d
            r2.<init>(r3)     // Catch: java.lang.Throwable -> L7a java.lang.Exception -> L7d
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch: java.lang.Throwable -> L74 java.lang.Exception -> L76
            r4.<init>()     // Catch: java.lang.Throwable -> L74 java.lang.Exception -> L76
        L37:
            java.lang.String r5 = r2.readLine()     // Catch: java.lang.Throwable -> L74 java.lang.Exception -> L76
            if (r5 == 0) goto L41
            r4.add(r5)     // Catch: java.lang.Throwable -> L74 java.lang.Exception -> L76
            goto L37
        L41:
            java.io.BufferedWriter r5 = new java.io.BufferedWriter     // Catch: java.lang.Throwable -> L74 java.lang.Exception -> L76
            java.io.FileWriter r3 = new java.io.FileWriter     // Catch: java.lang.Throwable -> L74 java.lang.Exception -> L76
            r3.<init>(r0)     // Catch: java.lang.Throwable -> L74 java.lang.Exception -> L76
            r5.<init>(r3)     // Catch: java.lang.Throwable -> L74 java.lang.Exception -> L76
            r6 = 0
        L4c:
            int r0 = r4.size()     // Catch: java.lang.Throwable -> L70 java.lang.Exception -> L72
            if (r6 >= r0) goto L63
            java.lang.Object r0 = r4.get(r6)     // Catch: java.lang.Throwable -> L70 java.lang.Exception -> L72
            java.lang.String r0 = r0.toString()     // Catch: java.lang.Throwable -> L70 java.lang.Exception -> L72
            r5.write(r0)     // Catch: java.lang.Throwable -> L70 java.lang.Exception -> L72
            r5.newLine()     // Catch: java.lang.Throwable -> L70 java.lang.Exception -> L72
            int r6 = r6 + 1
            goto L4c
        L63:
            r5.flush()     // Catch: java.lang.Throwable -> L70 java.lang.Exception -> L72
            r5.close()     // Catch: java.lang.Throwable -> L70 java.lang.Exception -> L72
            r2.close()     // Catch: java.io.IOException -> L6c
        L6c:
            r5.close()     // Catch: java.io.IOException -> L9b
            goto L9b
        L70:
            r4 = move-exception
            goto L9e
        L72:
            r4 = move-exception
            goto L78
        L74:
            r4 = move-exception
            goto L9f
        L76:
            r4 = move-exception
            r5 = r6
        L78:
            r6 = r2
            goto L7f
        L7a:
            r4 = move-exception
            r2 = r6
            goto L9f
        L7d:
            r4 = move-exception
            r5 = r6
        L7f:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L9c
            r0.<init>()     // Catch: java.lang.Throwable -> L9c
            java.lang.String r2 = "   "
            r0.append(r2)     // Catch: java.lang.Throwable -> L9c
            r0.append(r4)     // Catch: java.lang.Throwable -> L9c
            java.lang.String r4 = r0.toString()     // Catch: java.lang.Throwable -> L9c
            android.util.Log.d(r1, r4)     // Catch: java.lang.Throwable -> L9c
            if (r6 == 0) goto L98
            r6.close()     // Catch: java.io.IOException -> L98
        L98:
            if (r5 == 0) goto L9b
            goto L6c
        L9b:
            return
        L9c:
            r4 = move-exception
            r2 = r6
        L9e:
            r6 = r5
        L9f:
            if (r2 == 0) goto La4
            r2.close()     // Catch: java.io.IOException -> La4
        La4:
            if (r6 == 0) goto La9
            r6.close()     // Catch: java.io.IOException -> La9
        La9:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.droidlogic.tvlauncher.AppDataLoader.getShortcutFromDefault(int, java.lang.String):void");
    }

    private static final Comparator<LauncherActivityInfo> getAppNameComparator() {
        final Collator collator = Collator.getInstance();
        return new Comparator<LauncherActivityInfo>() { // from class: com.droidlogic.tvlauncher.AppDataLoader.2
            @Override // java.util.Comparator
            public final int compare(LauncherActivityInfo launcherActivityInfo, LauncherActivityInfo launcherActivityInfo2) {
                if (launcherActivityInfo.getUser().equals(launcherActivityInfo2.getUser())) {
                    int compare = collator.compare(launcherActivityInfo.getLabel().toString(), launcherActivityInfo2.getLabel().toString());
                    return compare == 0 ? launcherActivityInfo.getName().compareTo(launcherActivityInfo2.getName()) : compare;
                }
                return launcherActivityInfo.getUser().toString().compareTo(launcherActivityInfo2.getUser().toString());
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadShortcutList() {
        this.homeShortCuts.clear();
        this.videoShortCuts.clear();
        this.recommendShorts.clear();
        this.musicShortCuts.clear();
        this.appShortCuts.clear();
        this.localShortCuts.clear();
        List<LauncherActivityInfo> activityList = this.mLauncherApps.getActivityList(null, Process.myUserHandle());
        Collections.sort(activityList, getAppNameComparator());
        int launcherLargeIconDensity = this.mActivityManager.getLauncherLargeIconDensity();
        if (activityList != null) {
            for (int i = 0; i < activityList.size(); i++) {
                ApplicationInfo applicationInfo = new ApplicationInfo();
                LauncherActivityInfo launcherActivityInfo = activityList.get(i);
                applicationInfo.title = launcherActivityInfo.getLabel().toString();
                applicationInfo.setActivity(launcherActivityInfo.getComponentName(), 270532608);
                applicationInfo.icon = launcherActivityInfo.getBadgedIcon(launcherLargeIconDensity);
                if (!launcherActivityInfo.getComponentName().getPackageName().equals("com.android.gallery3d") || !applicationInfo.intent.toString().contains("camera")) {
                    if (this.list_homeShortcut != null) {
                        int i2 = 0;
                        while (true) {
                            if (i2 >= this.list_homeShortcut.length) {
                                break;
                            } else if (launcherActivityInfo.getComponentName().getPackageName().equals(this.list_homeShortcut[i2])) {
                                this.homeShortCuts.add(buildShortcutMap(applicationInfo.title.toString(), applicationInfo.intent, applicationInfo.icon, applicationInfo.componentName));
                                break;
                            } else {
                                i2++;
                            }
                        }
                    }
                    if (this.list_videoShortcut != null) {
                        int i3 = 0;
                        while (true) {
                            if (i3 >= this.list_videoShortcut.length) {
                                break;
                            } else if (launcherActivityInfo.getComponentName().getPackageName().equals(this.list_videoShortcut[i3])) {
                                this.videoShortCuts.add(buildShortcutMap(applicationInfo.title.toString(), applicationInfo.intent, applicationInfo.icon, applicationInfo.componentName));
                                break;
                            } else {
                                i3++;
                            }
                        }
                    }
                    if (this.list_recommendShortcut != null) {
                        int i4 = 0;
                        while (true) {
                            if (i4 >= this.list_recommendShortcut.length) {
                                break;
                            } else if (launcherActivityInfo.getComponentName().getPackageName().equals(this.list_recommendShortcut[i4])) {
                                this.recommendShorts.add(buildShortcutMap(applicationInfo.title.toString(), applicationInfo.intent, applicationInfo.icon, applicationInfo.componentName));
                                break;
                            } else {
                                i4++;
                            }
                        }
                    }
                    if (this.list_musicShortcut != null) {
                        int i5 = 0;
                        while (true) {
                            if (i5 >= this.list_musicShortcut.length) {
                                break;
                            } else if (launcherActivityInfo.getComponentName().getPackageName().equals(this.list_musicShortcut[i5])) {
                                this.musicShortCuts.add(buildShortcutMap(applicationInfo.title.toString(), applicationInfo.intent, applicationInfo.icon, applicationInfo.componentName));
                                break;
                            } else {
                                i5++;
                            }
                        }
                    }
                    if (this.list_localShortcut != null) {
                        int i6 = 0;
                        while (true) {
                            if (i6 >= this.list_localShortcut.length) {
                                break;
                            } else if (launcherActivityInfo.getComponentName().getPackageName().equals(this.list_localShortcut[i6])) {
                                this.localShortCuts.add(buildShortcutMap(applicationInfo.title.toString(), applicationInfo.intent, applicationInfo.icon, applicationInfo.componentName));
                                break;
                            } else {
                                i6++;
                            }
                        }
                    }
                    this.appShortCuts.add(buildShortcutMap(applicationInfo.title.toString(), applicationInfo.intent, applicationInfo.icon, applicationInfo.componentName));
                    applicationInfo.icon.setCallback(null);
                }
            }
        }
        this.homeShortCuts.add(buildAddMap());
        this.videoShortCuts.add(buildAddMap());
        this.recommendShorts.add(buildAddMap());
        this.musicShortCuts.add(buildAddMap());
        this.localShortCuts.add(buildAddMap());
    }

    private ArrayMap<String, Object> buildShortcutMap(String str, Intent intent, Drawable drawable, ComponentName componentName) {
        ArrayMap<String, Object> arrayMap = new ArrayMap<>();
        arrayMap.put("name", str);
        arrayMap.put("intent", intent);
        int parsePackageIcon = parsePackageIcon(componentName.getPackageName());
        if (parsePackageIcon == -1) {
            arrayMap.put("icon", drawable);
        } else {
            arrayMap.put("icon", this.mContext.getResources().getDrawable(parsePackageIcon));
        }
        arrayMap.put("component name", componentName);
        return arrayMap;
    }

    private ArrayMap<String, Object> buildAddMap() {
        ArrayMap<String, Object> arrayMap = new ArrayMap<>();
        arrayMap.put("name", this.mContext.getResources().getString(R.string.str_add));
        arrayMap.put("intent", null);
        arrayMap.put("icon", Integer.valueOf((int) R.drawable.item_img_add));
        return arrayMap;
    }

    public List<ArrayMap<String, Object>> getShortcutList(int i) {
        synchronized (this.mLock) {
            try {
                if (i == 0) {
                    return this.homeShortCuts;
                } else if (i == 1) {
                    return this.videoShortCuts;
                } else if (i == 2) {
                    return this.recommendShorts;
                } else if (i == 3) {
                    return this.musicShortCuts;
                } else if (i == 4) {
                    return this.appShortCuts;
                } else if (i != 5) {
                    return null;
                } else {
                    return this.localShortCuts;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public String getShortcutString(int i) {
        synchronized (this.mLock) {
            try {
                if (i == 0) {
                    return this.str_homeShortcut;
                } else if (i == 1) {
                    return this.str_videoShortcut;
                } else if (i == 2) {
                    return this.str_recommendShortcut;
                } else if (i == 3) {
                    return this.str_musicShortcut;
                } else if (i != 5) {
                    return null;
                } else {
                    return this.str_localShortcut;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public boolean isDataLoaded() {
        return this.isLoaded;
    }

    private int parsePackageIcon(String str) {
        if (str.equals("com.droidlogic.FileBrower")) {
            return R.drawable.icon_filebrowser;
        }
        if (str.equals("org.chromium.webview_shell")) {
            return R.drawable.icon_browser;
        }
        if (str.equals("com.droidlogic.appinstall")) {
            return R.drawable.icon_appinstaller;
        }
        if (str.equals("com.android.tv.settings")) {
            return R.drawable.icon_setting;
        }
        if (str.equals("com.droidlogic.mediacenter")) {
            return R.drawable.icon_mediacenter;
        }
        if (str.equals("com.droidlogic.otaupgrade")) {
            return R.drawable.icon_backupandupgrade;
        }
        if (str.equals("com.droidlogic.miracast")) {
            return R.drawable.icon_miracast;
        }
        if (str.equals("com.droidlogic.PPPoE")) {
            return R.drawable.icon_pppoe;
        }
        return -1;
    }
}
