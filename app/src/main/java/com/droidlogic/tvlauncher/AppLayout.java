package com.droidlogic.tvlauncher;

import android.content.Context;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;

public class AppLayout extends RelativeLayout {
    Animation animLeftIn;
    Animation animLeftOut;
    Animation animRightIn;
    Animation animRightOut;
    private int currenPassword;
    private MyGridLayout grid_layout;
    private ImageView icon;
    private Context mContext;
    private int password;
    private ImageView prompt;
    private TextView title;

    public AppLayout(Context context) {
        super(context);
        this.password = 0;
        this.currenPassword = 0;
        this.mContext = context;
    }

    public AppLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.password = 0;
        this.currenPassword = 0;
        this.mContext = context;
        initlayout();
    }

    public AppLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.password = 0;
        this.currenPassword = 0;
    }

    private void initlayout() {
        RelativeLayout.inflate(this.mContext, R.layout.layout_second_screen, this);
        this.icon = (ImageView) findViewById(R.id.image_icon);
        this.prompt = (ImageView) findViewById(R.id.image_prompt);
        this.title = (TextView) findViewById(R.id.tx_title);
        this.grid_layout = (MyGridLayout) findViewById(R.id.gl_shortcut);
        this.animLeftIn = AnimationUtils.loadAnimation(this.mContext, R.anim.push_left_in);
        this.animLeftOut = AnimationUtils.loadAnimation(this.mContext, R.anim.push_left_out);
        this.animRightIn = AnimationUtils.loadAnimation(this.mContext, R.anim.push_right_in);
        this.animRightOut = AnimationUtils.loadAnimation(this.mContext, R.anim.push_right_out);
    }

    public void setLayout(int i, List<ArrayMap<String, Object>> list) {
        if (i == 1) {
            setImageAndText(R.drawable.video, R.drawable.prompt_video, R.string.str_video);
        } else if (i == 2) {
            setImageAndText(R.drawable.recommend, R.drawable.prompt_recommend, R.string.str_recommend);
        } else if (i == 3) {
            setImageAndText(R.drawable.music, R.drawable.prompt_music, R.string.str_music);
        } else if (i == 4) {
            setImageAndText(R.drawable.app, R.drawable.prompt_app, R.string.str_app);
        } else if (i == 5) {
            setImageAndText(R.drawable.local, R.drawable.prompt_local, R.string.str_test);
        }
        this.grid_layout.clearFocus();
        this.grid_layout.setLayoutView(i, list);
        if (i == 0 || this.grid_layout.getChildCount() <= 0) {
            return;
        }
        ((MyRelativeLayout) this.grid_layout.getChildAt(0)).requestFocus();
    }

    public void setImageAndText(int i, int i2, int i3) {
        this.icon.setImageDrawable(this.mContext.getResources().getDrawable(i));
        this.prompt.setImageDrawable(this.mContext.getResources().getDrawable(i2));
        this.title.setText(i3);
    }

    public void setLayoutWithAnim(int i, int i2, List<ArrayMap<String, Object>> list) {
        setLayout(i2, list);
        if (i == 0) {
            this.animLeftIn.setAnimationListener(new MyAnimationListener(0, this.password));
            startAnimation(this.animLeftIn);
        } else {
            this.animRightIn.setAnimationListener(new MyAnimationListener(0, this.password));
            startAnimation(this.animRightIn);
        }
        this.password++;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MyAnimationListener implements Animation.AnimationListener {
        private int mPassword;
        private int mType;

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationRepeat(Animation animation) {
        }

        public MyAnimationListener(int i, int i2) {
            this.mType = -1;
            this.mPassword = -1;
            this.mType = i;
            this.mPassword = i2;
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationStart(Animation animation) {
            ((Launcher) AppLayout.this.mContext).getHoverView().setVisibility(INVISIBLE);
            AppLayout.this.currenPassword = this.mPassword;
        }

        @Override // android.view.animation.Animation.AnimationListener
        public void onAnimationEnd(Animation animation) {
            if (this.mType == 0 && AppLayout.this.currenPassword == this.mPassword) {
                ((Launcher) AppLayout.this.mContext).getHoverView().setVisibility(VISIBLE);
            }
        }
    }
}
