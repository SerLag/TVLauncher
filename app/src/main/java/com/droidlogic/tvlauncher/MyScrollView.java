package com.droidlogic.tvlauncher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;


public class MyScrollView extends ScrollView {
    private final static String TAG="MyScrollView";
    private Context mContext;

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }
}
