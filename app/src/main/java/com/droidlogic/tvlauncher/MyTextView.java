package com.droidlogic.tvlauncher;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyTextView extends TextView {
    @Override
    public boolean isFocused() {
        return true;
    }

    public MyTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public MyTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MyTextView(Context context) {
        super(context);
    }
}
