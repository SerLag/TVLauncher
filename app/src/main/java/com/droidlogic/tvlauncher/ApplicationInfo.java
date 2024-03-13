package com.droidlogic.tvlauncher;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
class ApplicationInfo {
    /**
     * The application name.
     */
    CharSequence title;

    /**
     * A bitmap of the application's text in the bubble.
     */
    Bitmap titleBitmap;
    /**
     * The intent used to start the application.
     */
    Intent intent;

    /**
     * A bitmap version of the application icon.
     */
    Bitmap iconBitmap;
    /**
     * The application icon.
     */
    Drawable icon;

    /**
     * When set to true, indicates that the icon has been resized.
     */
    boolean filtered;

    ComponentName componentName;

    final void setActivity(ComponentName className, int launchFlags) {
        intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(className);
        intent.setFlags(launchFlags);
        componentName = className;
    }
}
