package com.droidlogic.tvlauncher;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.lang.ref.SoftReference;
import java.util.List;

/* loaded from: classes.dex */
public class MyGridLayout extends GridLayout {
    private Context mContext;
    private Object mLock;

    public MyGridLayout(Context context) {
        super(context);
    }

    public MyGridLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        this.mLock = ((Launcher) this.mContext).getLock();
    }

    public MyGridLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        if ((motionEvent.getSource() & 2) != 0 && motionEvent.getAction() == 8) {
            ((Launcher) this.mContext).getHoverView().clear();
        }
        return super.onGenericMotionEvent(motionEvent);
    }

    @Override // android.widget.GridLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    public void setLayoutView(int i, List<ArrayMap<String, Object>> list) {
        MyRelativeLayout myRelativeLayout;
        synchronized (this.mLock) {
            if (getChildCount() > 0) {
                removeAllViews();
            }
            for (int i2 = 0; i2 < list.size(); i2++) {
                if (i == 0) {
                    myRelativeLayout = (MyRelativeLayout) View.inflate(this.mContext, R.layout.homegrid_item, null);
                    myRelativeLayout.setType(6);
                } else {
                    myRelativeLayout = (MyRelativeLayout) View.inflate(this.mContext, R.layout.childgrid_item, null);
                    myRelativeLayout.setPadding(5, 5, 5, 5);
                    ((TextView) myRelativeLayout.getChildAt(1)).setText((String) list.get(i2).get("name"));
                    myRelativeLayout.setType(7);
                    myRelativeLayout.setNumber(i2);
                }
                ImageView imageView = (ImageView) myRelativeLayout.getChildAt(0);
                imageView.setBackgroundDrawable((Drawable) new SoftReference(this.mContext.getResources().getDrawable(parseItemBackground(i2, i))).get());
                if (list.get(i2).get("icon") instanceof Drawable) {
                    imageView.setImageDrawable((Drawable) new SoftReference((Drawable) list.get(i2).get("icon")).get());
                    myRelativeLayout.setIntent((Intent) list.get(i2).get("intent"));
                } else {
                    imageView.setImageDrawable((Drawable) new SoftReference(this.mContext.getResources().getDrawable(R.drawable.item_img_add)).get());
                    imageView.setContentDescription("img_add");
                    myRelativeLayout.setAddButton(true);
                }
                addView(myRelativeLayout);
            }
        }
    }

    private int parseItemBackground(int i, int i2) {
        if (i2 == 0) {
            switch (i % 11) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                default:
                    return R.drawable.item_child_6;
            }
        }
        switch (i % 18) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            default:
                return R.drawable.item_child_6;
        }
    }
}
