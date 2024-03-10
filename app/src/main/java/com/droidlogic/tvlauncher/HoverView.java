package com.droidlogic.tvlauncher;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/* loaded from: classes.dex */
public class HoverView extends RelativeLayout {
    private ImageView hoverImage_home;
    private ImageView hoverImage_second;
    private Context mContext;
    private TextView textBottom;
    private TextView textTop;

    public HoverView(Context context) {
        super(context);
        this.mContext = context;
    }

    public HoverView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        initlayout();
    }

    public HoverView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private void initlayout() {
        RelativeLayout.inflate(this.mContext, R.layout.layout_hover, this);
        this.hoverImage_home = (ImageView) findViewById(R.id.img_hover_home);
        this.hoverImage_second = (ImageView) findViewById(R.id.img_hover_second);
        this.textTop = (TextView) findViewById(R.id.tx_hover_top);
        this.textBottom = (TextView) findViewById(R.id.tx_hover_bottom);
    }

    public void clear() {
        this.hoverImage_home.setBackgroundDrawable(null);
        this.hoverImage_home.setImageDrawable(null);
        this.hoverImage_second.setBackgroundDrawable(null);
        this.hoverImage_second.setImageDrawable(null);
        this.textTop.setText((CharSequence) null);
        this.textBottom.setText((CharSequence) null);
        setOutlineProvider(null);
        setElevation(0.0f);
        setViewPosition(this, new Rect(0, 0, 0, 0));
    }

    public static void setViewPosition(View view, Rect rect) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(rect.width(), rect.height());
        layoutParams.setMargins(rect.left, rect.top, 0, 0);
        view.setLayoutParams(layoutParams);
    }
}
