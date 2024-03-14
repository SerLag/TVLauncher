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
    private String str_custom_apps;
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
        initLayout();
    }

    private void initLayout() {
        inflate(this.mContext, R.layout.layout_custom, this);
        this.thisView = this;
        this.gv = (GridView) findViewById(R.id.grid_add_apps);
        this.gv.setBackground(this.mContext.getResources().getDrawable(R.drawable.bg_add_apps));
        this.gv.setOnItemClickListener(this);
        displayView();
        this.str_custom_apps = ((Launcher) this.mContext).getAppDataLoader().getShortcutString(this.mMode);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        Rect rect = new Rect();
        this.mSource.getGlobalVisibleRect(rect);
        if (rect.top > this.mContext.getResources().getDisplayMetrics().heightPixels / 2) {
            this.transY = -getHeight();
        } else {
            this.transY = getHeight();
        }
        setCustomView();
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
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
            launcher.getAppDataLoader().saveShortcut(this.mMode, this.str_custom_apps);
        }
    }

    private List<ArrayMap<String, Object>> getAppList() {
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
            map.put("item_background", parseItemBackground(i));
            map.put("intent", list_all.get(i).get("intent"));
            list.add(map);
        }

        return list;
    }

    private void displayView() {
        this.gv.setAdapter((ListAdapter) new LocalAdapter(this.mContext, getAppList(), R.layout.add_apps_grid_item, new String[]{"item_icon", "item_name", "item_selection", "item_background"}, new int[]{R.id.item_type, R.id.item_name, R.id.item_sel, R.id.relative_layout}));
    }

    private void updateView() {
        ((BaseAdapter) this.gv.getAdapter()).notifyDataSetChanged();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0 && keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            recoverMainView();
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    private int parseItemBackground(int i) {
        switch ((i % 20) + 1) {
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
            case 18:
            case 19:
            default:
                return R.drawable.item_child_6;
        }
    }

    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        ArrayMap arrayMap = (ArrayMap) adapterView.getItemAtPosition(i);
        synchronized (mLock) {
            if (arrayMap.get("item_selection").equals(Integer.valueOf((int) R.drawable.item_img_unsel))) {
                if (this.mMode == 0 && this.homeShortcutCount >= Launcher.HOME_SHORTCUT_COUNT) {
                    Toast.makeText(this.mContext, this.mContext.getResources().getString(R.string.str_nospace), Toast.LENGTH_LONG).show();
                    return;
                }
                this.str_custom_apps += ((ComponentName) arrayMap.get("intent")).getPackageName() + ";";
                ((ArrayMap) adapterView.getItemAtPosition(i)).put("item_selection", Integer.valueOf((int) R.drawable.item_img_sel));
                updateView();
                if (this.mMode == 0) {
                    this.homeShortcutCount++;
                }
            } else {
                String packageName = ((ComponentName) arrayMap.get("intent")).getPackageName();
                this.str_custom_apps = this.str_custom_apps.replaceAll(packageName + ";", "");
                ((ArrayMap) adapterView.getItemAtPosition(i)).put("item_selection", Integer.valueOf((int) R.drawable.item_img_unsel));
                updateView();
                if (this.mMode == 0) {
                    this.homeShortcutCount--;
                }
            }
        }
    }

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
