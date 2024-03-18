package com.droidlogic.tvlauncher;

import android.animation.Animator;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class CustomView extends FrameLayout implements AdapterView.OnItemClickListener, ViewTreeObserver.OnGlobalLayoutListener {


    private static boolean allowAnimation = true;
    static final Object mLock = new Object[0];
    private GridView gv;
    private int homeShortcutCount;
    private ImageView img_dim;
    private ImageView img_screen_shot;
    private ImageView img_screen_shot_keep;
    private Context mContext;
    private int mMode;
    private View mSource;
    private List<String> custom_apps;
    private CustomView thisView;
    private int transY;

    public CustomView(Context context, View view, int i) {
        super(context);
        this.img_screen_shot = null;
        this.img_screen_shot_keep = null;
        this.img_dim = null;
        this.gv = null;
        this.thisView = null;
        this.mContext = null;
        this.transY = 0;
        this.mMode = -1;
        this.mContext = context;
        this.mSource = view;
        this.mMode = i;
        inflate(this.mContext, R.layout.layout_custom, this);
        this.thisView = this;
        this.gv = (GridView) findViewById(R.id.grid_add_apps);
        this.gv.setBackground(this.mContext.getResources().getDrawable(R.drawable.bg_add_apps));
        this.gv.setOnItemClickListener(this);
        displayView();
        this.custom_apps = ((Launcher)mContext).getAppDataLoader().list_localShortcut;
 //    ????   this.str_custom_apps = ((Launcher) this.mContext).getAppDataLoader().getShortcutString(this.mMode);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        Rect rect = new Rect();
        this.mSource.getGlobalVisibleRect(rect);
        if (rect.top > this.mContext.getResources().getDisplayMetrics().heightPixels / 2) {
            this.transY = -getHeight();
        } else {
            this.transY = getHeight();
        }
        setCustomView();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    private void setCustomView() {
        ((Launcher) this.mContext).getMainView().setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        ((Launcher) this.mContext).getMainView().animate().translationY(this.transY).setDuration(300L).alpha(0.5f).start();
        this.gv.getGlobalVisibleRect(new Rect());
        this.gv.requestFocus();
        TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, -this.transY, 0.0f);
        translateAnimation.setDuration(300L);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        this.gv.startAnimation(translateAnimation);
    }

    // возвращаем главный экран
    public void recoverMainView() {
        Launcher launcher = (Launcher) this.mContext;
        ViewGroup mainView = launcher.getMainView();
        setVisibility(VISIBLE);
        if (allowAnimation) {
            allowAnimation = false;
            TranslateAnimation translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -this.transY);
            translateAnimation.setDuration(300L);
            translateAnimation.setInterpolator(new AccelerateInterpolator());
            this.gv.startAnimation(translateAnimation);
            mainView.animate().translationY(0.0f).setDuration(300L).alpha(1.0f).setInterpolator(new AccelerateInterpolator()).setListener(new mAnimatorListener()).start();
 //           launcher.getAppDataLoader().saveShortcut(this.mMode, this.str_custom_apps);
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
                if (TextUtils.equals(list_all.get(i).get("label").toString(),
                        list_current.get(j).get("label").toString())) {
                    map.put("item_selection", R.drawable.item_img_sel);
                    if (mMode == Launcher.MODE_HOME) {
                        homeShortcutCount++;
                    }
                    break;
                }
            }
            map.put("item_name", list_all.get(i).get(AppDataLoader.NAME));
            map.put("item_icon", list_all.get(i).get(AppDataLoader.ICON));
            map.put("item_background", R.drawable.item_child_6);
            map.put("label", list_all.get(i).get("label"));
            list.add(map);
        }
        this.gv.setAdapter((ListAdapter) new LocalAdapter(this.mContext, list, R.layout.add_apps_grid_item, new String[]{"item_icon", "item_name", "item_selection", "item_background"}, new int[]{R.id.item_type, R.id.item_name, R.id.item_sel, R.id.relative_layout}));
    }

    private void updateView() {
        ((BaseAdapter) this.gv.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            recoverMainView();
        }
        return super.dispatchKeyEvent(keyEvent);
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
                this.custom_apps.add(arrayMap.get("label").toString());
                ((ArrayMap) adapterView.getItemAtPosition(i)).put("item_selection", (int) R.drawable.item_img_sel);
                updateView();
                if (this.mMode == 0) {
                    this.homeShortcutCount++;
                }
            } else {
                String packageName = arrayMap.get("label").toString();
                this.custom_apps.remove(packageName);
                ((ArrayMap) adapterView.getItemAtPosition(i)).put("item_selection", (int) R.drawable.item_img_unsel);
                updateView();
                if (this.mMode == 0) {
                    this.homeShortcutCount--;
                }
            }
        }
    }

/*    private List<ArrayMap<String, Object>> getAppList() {
        List<ArrayMap<String, Object>> list = new ArrayList<ArrayMap<String, Object>>();
        List<ArrayMap<String, Object>> list_all = ((Launcher)mContext).getAppDataLoader().getShortcutList(Launcher.MODE_APP);
        List<ArrayMap<String, Object>> list_current = ((Launcher)mContext).getAppDataLoader().getShortcutList(mMode);
        homeShortcutCount = 0;

        for (int i = 0; i < list_all.size(); i++) {
            ArrayMap<String, Object> map = new ArrayMap<String, Object>();
            map.put("item_selection", R.drawable.item_img_unsel);
            for (int j = 0; j < list_current.size() - 1; j++) {
                if (TextUtils.equals(list_all.get(i).get("intent").toString(),
                        list_current.get(j).get("intent").toString())) {
                    map.put("item_selection", R.drawable.item_img_sel);
                    if (mMode == Launcher.MODE_HOME) {
                        homeShortcutCount++;
                    }
                    break;
                }
            }
            map.put("item_name", list_all.get(i).get(AppDataLoader.NAME));
            map.put("item_icon", list_all.get(i).get(AppDataLoader.ICON));
            map.put("item_background", R.drawable.item_child_6);
            map.put("intent", list_all.get(i).get("intent"));
            list.add(map);
        }

        return list;
    }*/

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
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
