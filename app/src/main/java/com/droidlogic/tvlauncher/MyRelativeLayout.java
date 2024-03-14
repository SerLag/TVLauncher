package com.droidlogic.tvlauncher;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;

/* loaded from: classes.dex */
public class MyRelativeLayout extends RelativeLayout implements ViewTreeObserver.OnGlobalLayoutListener {
    private boolean isSwitchAnimRunning;
    private boolean layoutCompleted;
    private Context mContext;
    private float mElevation;
    private Intent mIntent;
    private boolean mIsAddButton;
    private int mNumber;
    private float mScale;
    private float mShadowScale;
    private int mType;

    private void setHoverView() {
    }

    public MyRelativeLayout(Context context) {
        super(context);
        this.mContext = null;
        this.layoutCompleted = false;
        this.mType = -1;
        this.mIntent = null;
        this.mNumber = -1;
        this.mIsAddButton = false;
        this.isSwitchAnimRunning = false;
    }

    public MyRelativeLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = null;
        this.layoutCompleted = false;
        this.mType = -1;
        this.mIntent = null;
        this.mNumber = -1;
        this.mIsAddButton = false;
        this.isSwitchAnimRunning = false;
        this.mContext = context;
        setFocusable(true);
        setFocusableInTouchMode(true);
        setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public MyRelativeLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = null;
        this.layoutCompleted = false;
        this.mType = -1;
        this.mIntent = null;
        this.mNumber = -1;
        this.mIsAddButton = false;
        this.isSwitchAnimRunning = false;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.widget.RelativeLayout, android.view.ViewGroup, android.view.View
    protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
        super.onLayout (changed, left, top, right, bottom);
    }

    @Override
    protected void onDetachedFromWindow() {
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
                                ((Launcher) mContext).startTvApp();
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
                                        ((Launcher) this.mContext).startTvApp();
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
                                ((Launcher) this.mContext).startTvApp();
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

    @Override // android.view.View
    protected void onFocusChanged (boolean gainFocus, int direction, Rect previouslyFocusedRect){
        if (this.layoutCompleted) {
            setFocusViewBg(gainFocus, this);
        }
        super.onFocusChanged(gainFocus, direction,  previouslyFocusedRect);
    }

    public void setType(int i) {
        this.mType = i;
        switch (this.mType) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
                this.mScale = 1.07f;
                this.mElevation = 40.0f;
                this.mShadowScale = 1.0f;
                return;
            case 6:
                this.mScale = 1.1f;
                this.mElevation = 30.0f;
                this.mShadowScale = 0.9f;
                return;
            case 7:
                this.mScale = 1.1f;
                this.mElevation = 36.0f;
                this.mShadowScale = 0.9f;
                return;
            case 8:
            case 9:
            default:
                return;
        }
    }

    public void setIntent(Intent intent) {
        this.mIntent = intent;
    }

    private void showSecondScreen(int i) {
        ((Launcher) this.mContext).setHomeViewVisible(false);
        ((Launcher) this.mContext).setShortcutScreen(i);
    }

    private void setFocusViewBg(boolean z, View view) {
        if (z) {
            view.setBackgroundColor(0xFFFFFF00);
        } else {
            view.setBackground(null);
        }
    }

    public void setNumber(int number) {
        mNumber = number;
    }

    public void setAddButton(boolean z) {
        this.mIsAddButton = z;
    }

    @Override // android.view.View
    public float getElevation() {
        return this.mElevation;
    }

    private void startActivityFromPackage(String str) {
        Intent launchIntentForPackage = this.mContext.getPackageManager().getLaunchIntentForPackage(str);
        if (launchIntentForPackage != null) {
            this.mContext.startActivity(launchIntentForPackage);
        }
    }
}
