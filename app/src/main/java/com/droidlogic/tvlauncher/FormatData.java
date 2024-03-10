package com.droidlogic.tvlauncher;

/* loaded from: classes.dex */
public class FormatData {
    public static String formatRate(long j, long j2) {
        double d = ((j - j2) / j) * 100.0d;
        return (((int) (d * 100.0d)) / 100.0f) + "%";
    }
}
