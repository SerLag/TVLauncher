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
    private final static String TAG="MyGridLayout";
    private Context mContext;
    private Object mLock;

    public MyGridLayout(Context context) {
        super(context);
    }

    public MyGridLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
        if(!isInEditMode()) {
            this.mLock = ((Launcher) this.mContext).getLock();
        }
    }

    public MyGridLayout(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @Override
    public void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
    }

    @Override //!!!!
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        if ((motionEvent.getSource() & 2) != 0 && motionEvent.getAction() == 8) {
            ((Launcher) this.mContext).getHoverView().clear();
        }
        return super.onGenericMotionEvent(motionEvent);
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        super.onLayout (changed, left, top, right, bottom);
    }

    public void setLayoutView(int mode, List<ArrayMap<String, Object>> list) {
        MyRelativeLayout myRelativeLayout;
        synchronized (this.mLock) {
            if (getChildCount() > 0) {
                removeAllViews();
            }
            for (int i = 0; i < list.size(); i++) {
                if (mode == Launcher.MODE_HOME) {
                    myRelativeLayout = (MyRelativeLayout) View.inflate(this.mContext, R.layout.homegrid_item, null);
                    myRelativeLayout.setType(Launcher.TYPE_HOME_SHORTCUT);
                } else {
                    myRelativeLayout = (MyRelativeLayout) View.inflate(this.mContext, R.layout.childgrid_item, null);
                    myRelativeLayout.setPadding(5, 5, 5, 5);
                    ((TextView) myRelativeLayout.getChildAt(1)).setText((String) list.get(i).get("name"));
                    myRelativeLayout.setType(Launcher.TYPE_APP_SHORTCUT);
                    myRelativeLayout.setNumber(i);
                }
                ImageView imageView = (ImageView) myRelativeLayout.getChildAt(0);
                imageView.setBackgroundDrawable((Drawable) new SoftReference(this.mContext.getResources().getDrawable(parseItemBackground(i, i))).get());
                if (list.get(i).get("icon") instanceof Drawable) {
                    imageView.setImageDrawable((Drawable) new SoftReference((Drawable) list.get(i).get("icon")).get());
                    myRelativeLayout.setIntent(mContext.getPackageManager().getLaunchIntentForPackage(list.get(i).get("label").toString()));
                } else {
                    imageView.setImageDrawable((Drawable) new SoftReference(this.mContext.getResources().getDrawable(R.drawable.item_img_add)).get());
                    imageView.setContentDescription("img_add");
                    myRelativeLayout.setAddButton(true);
                }
                addView(myRelativeLayout);
            }
        }
    }

    private int parseItemBackground(int num, int mode) {
        if (mode == Launcher.MODE_HOME) {
            switch (num % 11) {
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
        switch (num % 18) {
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
