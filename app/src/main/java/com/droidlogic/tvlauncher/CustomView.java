package com.droidlogic.tvlauncher;

import android.content.Intent;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;

import android.view.View;
import android.view.KeyEvent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.graphics.drawable.Drawable;
import android.graphics.Rect;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.GridView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AccelerateInterpolator;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.view.ViewGroup;
import android.util.ArrayMap;
import android.util.Log;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import android.widget.ListAdapter;

public class CustomView extends FrameLayout implements AdapterView.OnItemClickListener, OnGlobalLayoutListener {
    private final static String TAG = "CustomView";
    private static boolean allowAnimation = true;
    private ImageView img_screen_shot = null;
    private ImageView img_screen_shot_keep = null;
    private ImageView img_dim = null;
    private GridView gv = null;
    private CustomView thisView = null;
    private Context mContext = null;
    private List<String> custom_apps;
    private int transY = 0;
    private int homeShortcutCount;
    final static Object mLock = new Object[0];
    private View mSource;
    private int mMode = -1;

    public CustomView(Context context, View source, int mode){

        super(context);
        mContext = context;
        mSource = source;
        mMode = mode;
        inflate(this.mContext, R.layout.layout_custom, this);
        thisView = this;
        gv = (GridView) findViewById(R.id.grid_add_apps);
//        gv.setBackground(this.mContext.getResources().getDrawable(R.drawable.bg_add_apps, null));
        gv.setBackgroundColor(0xFF373778);
        gv.setOnItemClickListener(this);
        displayView();
        custom_apps = ((Launcher)mContext).getAppDataLoader().list_localShortcut;
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        Rect rect = new Rect();
        mSource.getGlobalVisibleRect(rect);
        if (rect.top > mContext.getResources().getDisplayMetrics().heightPixels / 2) {
            transY = -getHeight();
        } else {
            transY = getHeight();
        }
        setCustomView();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private void setCustomView(){
        ((Launcher)mContext).getMainView().setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        View view = ((Launcher)mContext).getMainView();//getWindow().getDecorView();
        view.animate().
                translationY(transY).
                setDuration(300).
                alpha(0.5f).
                start();
        Rect rect = new Rect();
        gv.getGlobalVisibleRect(rect);
        gv.requestFocus();
    }


    public void recoverMainView() {
        Launcher launcher = (Launcher) this.mContext;
        ViewGroup mainView = launcher.getMainView();
        setVisibility(VISIBLE);
        if (allowAnimation) {
            allowAnimation = false;
            TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -this.transY);
            translateAnimation.setDuration(300);
            translateAnimation.setInterpolator(new AccelerateInterpolator());
            this.gv.startAnimation(translateAnimation);
            mainView.animate().translationY(0.0f).setDuration(300).alpha(1.0f).setInterpolator(new AccelerateInterpolator()).setListener(new mAnimatorListener()).start();
            // save home_shortcuts
            launcher.getAppDataLoader().update();
            launcher.getAppDataLoader().saveShortcuts();
        }
    }

    private void displayView() {
        List<ArrayMap<String, Object>> list = new ArrayList<ArrayMap<String, Object>>();
        List<ArrayMap<String, Object>> list_all = ((Launcher)mContext).getAppDataLoader().getappShortCuts();
        List<ArrayMap<String, Object>> list_current = ((Launcher)mContext).getAppDataLoader().getlocalShortCuts();
        homeShortcutCount = 0;

        for (int i = 0; i < list_all.size(); i++) {
            ArrayMap<String, Object> map = new ArrayMap<String, Object>();
            map.put("item_selection", R.drawable.item_img_unsel);
            for (int j = 0; j < list_current.size() - 1; j++) {
                if (TextUtils.equals(list_all.get(i).get("component").toString(),
                        list_current.get(j).get("component").toString())) {
                    map.put("item_selection", R.drawable.item_img_sel);
                    if (mMode == Launcher.MODE_HOME) {
                        homeShortcutCount++;
                    }
                    break;
                }
            }
            map.put("item_name", list_all.get(i).get("name"));
            map.put("item_icon", list_all.get(i).get("icon"));
            map.put("item_background", parseItemBackground(i));
            map.put("component", list_all.get(i).get("component"));
            list.add(map);
        }
        this.gv.setAdapter((ListAdapter) new LocalAdapter(this.mContext, list, R.layout.add_apps_grid_item, new String[]{"item_icon", "item_name", "item_selection", "item_background"}, new int[]{R.id.item_type, R.id.item_name, R.id.item_sel, R.id.relative_layout}));
    }

    private void updateView() {
        ((BaseAdapter)gv.getAdapter()).notifyDataSetChanged();
    }

    public boolean dispatchKeyEvent (KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    recoverMainView();
                    break;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    private int  parseItemBackground(int num){
        switch (num % 20 + 1) {
            case 1:
                return R.drawable.item_child_1;
            case 2:
                return R.drawable.item_child_2;
            case 3:
                return R.drawable.item_child_3;
            case 4:
                return R.drawable.item_child_4;
            case 5:
                return R.drawable.item_child_5;
            case 6:
                return R.drawable.item_child_6;
            case 7:
                return R.drawable.item_child_3;
            case 8:
                return R.drawable.item_child_4;
            case 9:
                return R.drawable.item_child_1;
            case 10:
                return R.drawable.item_child_2;
            case 11:
                return R.drawable.item_child_6;
            case 12:
                return R.drawable.item_child_5;
            case 13:
                return R.drawable.item_child_6;
            case 14:
                return R.drawable.item_child_2;
            case 15:
                return R.drawable.item_child_5;
            case 16:
                return R.drawable.item_child_3;
            case 17:
                return R.drawable.item_child_1;
            case 18:
                return R.drawable.item_child_4;
            case 19:
                return R.drawable.item_child_2;
            case 0:
                return R.drawable.item_child_3;
            default:
                return R.drawable.item_child_1;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        ArrayMap arrayMap = (ArrayMap) adapterView.getItemAtPosition(i);
        synchronized (mLock) {
            if (arrayMap.get("item_selection").equals((int) R.drawable.item_img_unsel)) {
                if (this.mMode == 0 && this.homeShortcutCount >= Launcher.HOME_SHORTCUT_COUNT) {
                    Toast.makeText(this.mContext, this.mContext.getResources().getString(R.string.str_nospace), Toast.LENGTH_LONG).show();
                    return;
                }
                this.custom_apps.add(((ComponentName) arrayMap.get("component")).getPackageName());
                ((ArrayMap) adapterView.getItemAtPosition(i)).put("item_selection", (int) R.drawable.item_img_sel);
                updateView();
                if (this.mMode == 0) {
                    this.homeShortcutCount++;
                }
            } else {
                this.custom_apps.remove(((ComponentName) arrayMap.get("component")).getPackageName());
                ((ArrayMap) adapterView.getItemAtPosition(i)).put("item_selection", (int) R.drawable.item_img_unsel);
                updateView();
                if (this.mMode == 0) {
                    this.homeShortcutCount--;
                }
            }
        }
    }

    public class mAnimatorListener implements Animator.AnimatorListener {
        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
        }

        private mAnimatorListener() {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            boolean unused = CustomView.allowAnimation = true;
            ((Launcher) CustomView.this.mContext).getRootView().removeView(CustomView.this.thisView);
            ((Launcher) CustomView.this.mContext).getMainView().animate().setListener(null);
            ((Launcher) CustomView.this.mContext).recoverFromCustom();
        }
    }
}
