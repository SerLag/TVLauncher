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


public class MyRelativeLayout extends RelativeLayout implements OnGlobalLayoutListener {

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

    private boolean isSwitchAnimRunning = false;
    private boolean layoutCompleted = false;
    private Context mContext = null;
    private Intent mIntent = null;
    private boolean mIsAddButton = false;
    private int mNumber = -1;
    private int mType = -1;

/*    private void setHoverView() {
    }*/

    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
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

    @Override
    public void onGlobalLayout() {
        this.layoutCompleted = true;
        if (isFocused()) {
            setHoverView();
            setFocusViewBg(true, this);
        }
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        try {
            if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        if (this.mNumber != -1 && this.mNumber % 6 == 0) {
                            this.isSwitchAnimRunning = true;
                            ((Launcher) this.mContext).switchSecondScren(0);
                            break;
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        if (this.mNumber != -1 && (this.mNumber % 6 == 5 || this.mNumber == ((ViewGroup) getParent()).getChildCount() - 1)) {
                            this.isSwitchAnimRunning = true;
                            ((Launcher) this.mContext).switchSecondScren(1);
                            break;
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        if (this.mType != Launcher.TYPE_APP_SHORTCUT) {
                            ((Launcher) this.mContext).saveHomeFocus(this);
                        }
                        switch (mType) {
                            case Launcher.TYPE_VIDEO:
      //                          ((Launcher) mContext).startTvApp();
                                break;
                            case Launcher.TYPE_RECOMMEND:
                                showSecondScreen(Launcher.MODE_RECOMMEND);
                                break;
                            case Launcher.TYPE_MUSIC:
                                showSecondScreen(Launcher.MODE_MUSIC);
                                break;
                            case Launcher.TYPE_APP:
                                showSecondScreen(Launcher.MODE_APP);
                                break;
                            case Launcher.TYPE_LOCAL:
                                showSecondScreen(Launcher.MODE_LOCAL);
                                break;
                            case Launcher.TYPE_SETTINGS:
                                if (this.mIntent != null) {
                                    this.mContext.startActivity(this.mIntent);
                                    if (this.mIntent.getComponent().flattenToString().equals("com.android.tv.settings/com.android.tv.settings.MainSettings")) {
                                        Launcher.isLaunchingTvSettings = true;
                                        break;
                                    }
                                }
                                break;
                            case Launcher.TYPE_APP_SHORTCUT:
                            case Launcher.TYPE_HOME_SHORTCUT:
                                ComponentName componentName = new ComponentName("com.android.camera2", "com.android.camera.CameraLauncher");
                                if (this.mIntent != null) {
                                    if (this.mIntent.getComponent().flattenToString().equals(Launcher.COMPONENT_TV_APP)) {
     //                                   ((Launcher) this.mContext).startTvApp();
                                        break;
                                    } else if (this.mIntent.getComponent().equals(componentName)) {
                                        if (this.mContext.getPackageManager().getComponentEnabledSetting(componentName) != 2) {
                                            try {
                                                this.mContext.startActivity(this.mIntent);
                                                break;
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                break;
                                            }
                                        }
                                    } else {
                                        this.mContext.startActivity(this.mIntent);
                                        String[] split = this.mIntent.getComponent().flattenToString().split("/");
                                        if (split.length > 0 && split[0].equals("com.android.gl2jni")) {
                                            Launcher.isLaunchingThomasroom = true;
                                            break;
                                        }
                                    }
                                } else if (this.mIsAddButton) {
                                    ((Launcher) this.mContext).startCustomScreen(this);
                                    break;
                                }
                                break;
/*                            case 10:
                                startActivityFromPackage("com.android.vending");
                                break;
                            case 11:
                                startActivityFromPackage("org.xbmc.kodi");
                                break;
                            case 12:
                                startActivityFromPackage("com.android.chrome");
                                break;
                            case 14:
                                startActivityFromPackage("com.google.android.youtube.tv");
                                break;
                            case 15:
                                startActivityFromPackage("ru.mts.mtstv");
                                break;
                            case 16:
                            case 17:
                                if (this.mIntent != null) {
                                    this.mContext.startActivity(this.mIntent);
                                    break;
                                }
                                break; */
                        }

                }

            }
        }
         catch (Exception e2) {
             e2.printStackTrace();
         }
        return super.dispatchKeyEvent(keyEvent);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        try {
            if (motionEvent.getAction() != 0 && motionEvent.getAction() == 1) {
                switch (this.mType) {
                    case 0:
                        showSecondScreen(1);
                        return false;
                    case 1:
                        showSecondScreen(2);
                        return false;
                    case 2:
                        showSecondScreen(3);
                        return false;
                    case 3:
                        showSecondScreen(4);
                        return false;
                    case 4:
                        showSecondScreen(5);
                        return false;
                    case 5:
                        if (this.mIntent != null) {
                            this.mContext.startActivity(this.mIntent);
                            return false;
                        }
                        return false;
                    case 6:
                    case 7:
                        ComponentName componentName = new ComponentName("com.android.camera2", "com.android.camera.CameraLauncher");
                        if (this.mIntent != null) {
                            if (this.mIntent.getComponent().flattenToString().equals(Launcher.COMPONENT_TV_APP)) {
     //                           ((Launcher) this.mContext).startTvApp();
                                return false;
                            } else if (this.mIntent.getComponent().equals(componentName)) {
                                if (this.mContext.getPackageManager().getComponentEnabledSetting(componentName) != 2) {
                                    try {
                                        this.mContext.startActivity(this.mIntent);
                                        return false;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        return false;
                                    }
                                }
                                return false;
                            } else {
                                this.mContext.startActivity(this.mIntent);
                                return false;
                            }
                        } else if (this.mIsAddButton) {
                            ((Launcher) this.mContext).startCustomScreen(this);
                            return false;
                        } else {
                            return false;
                        }
                    case 8:
                    case 9:
                    case 13:
                    default:
                        return false;
                    case 10:
                        startActivityFromPackage("com.android.vending");
                        return false;
                    case 11:
                        startActivityFromPackage("org.xbmc.kodi");
                        return false;
                    case 12:
                        startActivityFromPackage("com.android.chrome");
                        return false;
                    case 14:
                        startActivityFromPackage("com.google.android.youtube.tv");
                        return false;
                    case 15:
                        startActivityFromPackage("ru.mts.mtstv");
                        return false;
                    case 16:
                    case 17:
                        if (this.mIntent != null) {
                            this.mContext.startActivity(this.mIntent);
                            return false;
                        }
                        return false;
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return true;
    }

    private void startActivityFromPackage(String str) {
        Intent launchIntentForPackage = this.mContext.getPackageManager().getLaunchIntentForPackage(str);
        if (launchIntentForPackage != null) {
            this.mContext.startActivity(launchIntentForPackage);
        }
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

/*    @Override // android.view.View
    protected void onFocusChanged (boolean gainFocus, int direction, Rect previouslyFocusedRect){
        if (this.layoutCompleted) {
            setFocusViewBg(gainFocus, this);
        }
        super.onFocusChanged(gainFocus, direction,  previouslyFocusedRect);
    }*/

    public void setType(int type) {
        mType = type;
        switch (mType) {
            case Launcher.TYPE_HOME:
            case Launcher.TYPE_SETTINGS:
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




/*    private void setFocusViewBg(boolean z, View view) {
        if (z) {
            view.setBackgroundColor(0xFFFFFF00);
        } else {
            view.setBackground(null);
        }
    }*/

}
