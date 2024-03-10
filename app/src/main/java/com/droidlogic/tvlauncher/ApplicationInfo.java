package com.droidlogic.tvlauncher;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

/* loaded from: classes.dex */
class ApplicationInfo {
    ComponentName componentName;
    Drawable icon;
    Intent intent;
    CharSequence title;

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setActivity(ComponentName componentName, int i) {
        this.intent = new Intent("android.intent.action.MAIN");
        this.intent.addCategory("android.intent.category.LAUNCHER");
        this.intent.setComponent(componentName);
        this.intent.setFlags(i);
        this.componentName = componentName;
    }
}
