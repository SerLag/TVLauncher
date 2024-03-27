package com.droidlogic.tvlauncher;

import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.view.ViewGroup;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.KeyEvent;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.graphics.drawable.Drawable;
import android.graphics.Rect;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.Character;


public class MyRelativeLayout extends RelativeLayout implements OnGlobalLayoutListener{
    private final static String TAG="MyRelativeLayout";

    private final static float ELEVATION_HOVER_MIN = 30;
    private final static float ELEVATION_HOVER_MID = 36;
    private final static float ELEVATION_HOVER_MAX = 40;
    public final static float ELEVATION_ABOVE_HOVER = 51;
    public final static float ELEVATION_UNDER_HOVER = 10;
    private final static float ALPHA_HOVER = 200;
    private final static float SCALE_PARA_SMALL = 1.07f;
    private final static float SCALE_PARA_BIG = 1.1f;
    private final static float SHADOW_SMALL = 0.9f;
    private final static float SHADOW_BIG = 1.0f;
    private float mScale;
    private float mElevation;
    private float mShadowScale;

    public final static int COLUMN_NUMBER = 6;
    private static final int animDuration = 70;
    private static final int animDelay = 0;

    private Context mContext = null;
    private static Rect imgRect;
    private boolean layoutCompleted = false;
    private int mType = -1;
    private Intent mIntent = null;
    private int mNumber = -1;
    private boolean mIsAddButton = false;
    private boolean isSwitchAnimRunning = false;

    public MyRelativeLayout(Context context){
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext = context;
        setFocusable(true);
        setFocusableInTouchMode(true);
        setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        super.onLayout (changed, left, top, right, bottom);
    }

    @Override
    protected void onDetachedFromWindow () {
        super.onDetachedFromWindow();
    }

    public  void onGlobalLayout () {
        layoutCompleted = true;
        if (isFocused()) {
            setHoverView();
        }
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    try {
                        switch (mType) {
                            case Launcher.TYPE_HOME:
                            case Launcher.TYPE_SETTINGS:
                            case Launcher.TYPE_FILEMANAGER:
                                if (mIntent != null) {
                                    mContext.startActivity(mIntent);
                                }
                                break;
                            case Launcher.TYPE_APPS:
                                showSecondScreen(Launcher.MODE_APP);
                            case Launcher.TYPE_APP_SHORTCUT:
                                showSecondScreen(Launcher.MODE_LOCAL);
                            case Launcher.TYPE_HOME_SHORTCUT:
                                if (mIntent != null) {
                                    mContext.startActivity(mIntent);
                                } else if (mIsAddButton) {
                                    ((Launcher) mContext).startCustomScreen(this);
                                }
                                break;
                        }
                    } catch (Exception e) {
                        Log.e("MediaBoxLauncher", "Failed start activity:" + e);
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN && event.getAction() == MotionEvent.ACTION_UP) {
            try {
                switch (mType) {
                    case Launcher.TYPE_HOME:
                        if (mIntent != null) {
                            mContext.startActivity(mIntent);
                        }
                        break;
                    case Launcher.TYPE_SETTINGS:

                    case Launcher.TYPE_FILEMANAGER:

                    case Launcher.TYPE_APPS:

                    case Launcher.TYPE_APP_SHORTCUT:
                    case Launcher.TYPE_HOME_SHORTCUT:
                        if (mIntent != null) {
                            mContext.startActivity(mIntent);
                        } else if (mIsAddButton) {
                            ((Launcher) mContext).startCustomScreen(this);
                        }
                        break;
                }
            } catch (Exception e) {
                Log.e("MediaBoxLauncher", "Failed start activity:" + e);
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onFocusChanged (boolean gainFocus, int direction, Rect previouslyFocusedRect){
        if (gainFocus) {
            if (layoutCompleted) {
                setHoverView();
            }
        } else {
            ScaleAnimation anim = new ScaleAnimation(1.07f, 1f, 1.07f, 1f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setZAdjustment(Animation.ZORDER_TOP);
            anim.setDuration(animDuration);
            anim.setStartTime(animDelay);
            this.startAnimation(anim);
        }
        super.onFocusChanged(gainFocus, direction,  previouslyFocusedRect);
    }

    public void setType(int type) {
        mType = type;
        switch (mType) {
            case Launcher.TYPE_HOME:
            case Launcher.TYPE_SETTINGS:
            case Launcher.TYPE_FILEMANAGER:
            case Launcher.TYPE_APPS:
                mScale = SCALE_PARA_SMALL;
                mElevation = ELEVATION_HOVER_MAX;
                mShadowScale = SHADOW_BIG;
                break;
            case Launcher.TYPE_APP_SHORTCUT:
                mScale = SCALE_PARA_BIG;
                mElevation = ELEVATION_HOVER_MID;
                mShadowScale = SHADOW_SMALL;
                break;
            case Launcher.TYPE_HOME_SHORTCUT:
                mScale = SCALE_PARA_BIG;
                mElevation = ELEVATION_HOVER_MIN;
                mShadowScale = SHADOW_SMALL;
                break;
        }
    }

    public void setIntent(Intent intent) {
        mIntent = intent;
    }

    private void showSecondScreen(int mode){
        ((Launcher)mContext).setHomeViewVisible(false);
        ((Launcher)mContext).setShortcutScreen(mode);
    }

    private void setHoverView(){
        ((Launcher)mContext).getHoverView().setHover(this);
    }

    public void setNumber(int number) {
        mNumber = number;
    }

    public void setAddButton(boolean isAdd) {
        mIsAddButton = isAdd;
    }

    public boolean isAddButton() {
        return mIsAddButton;
    }

    public int getType() {
        return mType;
    }

    public float getScale() {
        return mScale;
    }

    public float getElevation() {
        return mElevation;
    }

    public float getShadowScale() {
        return mShadowScale;
    }

}
