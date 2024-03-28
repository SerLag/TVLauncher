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
    private final static String TAG = "AppLayout";
    public final static int ANIM_LEFT = 0;
    public final static int ANIM_RIGHT = 1;
    private final static int TYPE_ANIM_IN = 0;
    private final static int TYPE_ANIM_OUT = 1;

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

    public AppLayout(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
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

    public void setImageAndText(int resIcon, int resPrompt, int resTitle) {
        icon.setImageDrawable(mContext.getResources().getDrawable(resIcon, null));
        prompt.setImageDrawable(mContext.getResources().getDrawable(resPrompt, null));
        title.setText(resTitle);
    }

    public void setLayoutWithAnim(int animType, int mode, List<ArrayMap<String, Object>> list) {
        setLayout(mode, list);
        if (animType == ANIM_LEFT) {
            animLeftIn.setAnimationListener(new MyAnimationListener(TYPE_ANIM_IN, password));
            startAnimation(animLeftIn);
        } else {
            animRightIn.setAnimationListener(new MyAnimationListener(TYPE_ANIM_IN, password));
            startAnimation(animRightIn);
        }
        password++;
    }

    public class MyAnimationListener implements Animation.AnimationListener {
        private int mPassword = -1;
        private int mType = -1;

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
        public MyAnimationListener(int type, int password) {
            mType = type;
            mPassword = password;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            ((Launcher) AppLayout.this.mContext).getHoverView().setVisibility(INVISIBLE);
            AppLayout.this.currenPassword = this.mPassword;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (this.mType == TYPE_ANIM_IN && AppLayout.this.currenPassword == this.mPassword) {
                ((Launcher) AppLayout.this.mContext).getHoverView().setVisibility(VISIBLE);
            }
        }
    }
}

