package com.droidlogic.tvlauncher;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewOutlineProvider;
import android.graphics.drawable.Drawable;
import android.graphics.Rect;
import android.graphics.Outline;
import android.util.AttributeSet;


public class HoverView extends RelativeLayout{
    private final static String TAG = "HoverView";
    private final static int TYPE_ANIM_IN = 0;
    private final static int TYPE_ANIM_OUT = 1;
    private final static float ALPHA_HOVER = 200;
    private final static float RADIUS = 20;
    private final static int animDuration = 70;
    private final static int animDelay = 0;

    private Context mContext;
    private ImageView hoverImage_home;
    private ImageView hoverImage_second;
    private TextView textTop;
    private TextView textBottom;

    public HoverView(Context context){
        super(context);
        mContext = context;
    }

    public HoverView(Context context, AttributeSet attrs){
        super(context, attrs);
        mContext = context;
        initlayout();
    }

    public HoverView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    private void initlayout() {
        inflate(mContext, R.layout.layout_hover, this);
        hoverImage_home = (ImageView)findViewById(R.id.img_hover_home);
        hoverImage_second = (ImageView)findViewById(R.id.img_hover_second);
        textBottom = (TextView)findViewById(R.id.tx_hover_bottom);
    }

    private void startAnimation(){
        ScaleAnimation shadowAnim = new ScaleAnimation(0.95f, 1f, 0.95f, 1f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
        shadowAnim.setDuration(animDuration);
        shadowAnim.setStartTime(animDelay);
        shadowAnim.setAnimationListener(new ScaleAnimationListener());

        this.startAnimation(shadowAnim);
    }

    public void clear() {
        hoverImage_home.setBackground(null);
        hoverImage_home.setImageDrawable(null);
        hoverImage_second.setBackground(null);
        hoverImage_second.setImageDrawable(null);
        textBottom.setText(null);
        this.setOutlineProvider(null);
        this.setElevation(0);
        setViewPosition(this, new Rect(0, 0, 0, 0));
    }

    public void setHover(MyRelativeLayout focusView){
        Rect layoutRect;
        Rect imgRect = new Rect();
        final float scale = focusView.getScale();
        final float elevation = focusView.getElevation();
        final float shadowScale = focusView.getShadowScale();

        focusView.getGlobalVisibleRect(imgRect);

        int scale_w = (int)((scale - 1) * imgRect.width() / 2);
        int scale_h = (int)((scale - 1) * imgRect.height() / 2);
        layoutRect = new Rect(imgRect.left-scale_w, imgRect.top-scale_h, imgRect.right+scale_w, imgRect.bottom+scale_h);
        setViewPosition(this, layoutRect);

        ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect((int)((1 - shadowScale) * view.getWidth() / 2),
                        (int)((1 - shadowScale) * view.getHeight() / 2),
                        (int)(((1 + shadowScale) / 2) * view.getWidth()),
                        (int)(((1 + shadowScale) / 2) * view.getHeight()),
                        RADIUS);
                outline.setAlpha(ALPHA_HOVER);
            }
        };
        this.setOutlineProvider(viewOutlineProvider);
        this.setBackground(getResources().getDrawable(R.color.item_bg_color, null));
        this.setElevation(elevation);

        setHoverImage(focusView, (ImageView)(focusView.getChildAt(0)));
        if (focusView.getChildAt(1) instanceof TextView) {
            setHoverText(focusView, ((TextView)focusView.getChildAt(1)).getText().toString());
        } else {
            setHoverText(focusView, null);
        }

        if (focusView.getVisibility() == View.VISIBLE) {
            startAnimation();
        }
    }

    private void setHoverImage(MyRelativeLayout focusView, ImageView source) {
        if (focusView.getType() != Launcher.TYPE_APP_SHORTCUT) {
            hoverImage_home.setBackground(source.getBackground());
            hoverImage_home.setImageDrawable(source.getDrawable());
            hoverImage_second.setBackground(null);
            hoverImage_second.setImageDrawable(null);
        } else {
            hoverImage_home.setBackground(null);
            hoverImage_home.setImageDrawable(null);
            hoverImage_second.setBackground(source.getBackground());
            hoverImage_second.setImageDrawable(source.getDrawable());
        }
    }

    private void setHoverText(MyRelativeLayout focusView, String s){
        if (focusView.getType() != Launcher.TYPE_APP_SHORTCUT) {
            textBottom.setText(null);
        } else {
            textBottom.setText(s);
        }
    }

    public static void setViewPosition(View view, Rect rect){
        android.widget.FrameLayout.LayoutParams para;
        para = new android.widget.FrameLayout.LayoutParams(rect.width(), rect.height());

        para.setMargins(rect.left, rect.top, 0, 0);
        view.setLayoutParams(para);
    }

    private class ScaleAnimationListener implements AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {
        }
        @Override
        public void onAnimationEnd(Animation animation) {
            setBackground(getResources().getDrawable(R.color.item_sel_color, null));
            //(this.setVisibility(View.VISIBLE);
        }
        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }
}

