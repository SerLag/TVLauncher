package com.droidlogic.tvlauncher;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.tv.TvInputManager;
import android.media.tv.TvView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class Launcher extends Activity {
    public static String COMPONENT_LIVE_TV = "com.android.tv/com.android.tv.TvActivity";
    public static String COMPONENT_TV_APP = "com.droidlogic.tvsource/com.droidlogic.tvsource.DroidLogicTv";
    public static String COMPONENT_TV_FILEMANAGER = "com.softwinner.TvdFileManager/.MainUI";
    public static String COMPONENT_TV_MIRACAST = "com.softwinner.miracastReceiver/.Miracast";
    public static String COMPONENT_TV_SETTINGS = "com.android.tv.settings/com.android.tv.settings.MainSettings";
    public static String COMPONENT_TV_SOURCE = "com.droidlogic.tv.settings/com.droidlogic.tv.settings.TvSourceActivity";
    public static float endX = 0.0f;
    public static boolean isLaunchingThomasroom = false;
    public static boolean isLaunchingTvSettings = false;
    public static float startX;
    private Animation animation;
    private EditText etCity;
    private ImageView home_line;
    private ImageView img_weather;
    private GridView lv_status;
    private AppDataLoader mAppDataLoader;
    private MyRelativeLayout mAppView;
    private FrameLayout mBlackFrameLayout;
    private MyRelativeLayout mBrowser;
    private MyRelativeLayout mFilemanager;
    private MyRelativeLayout mGoogleplay;
    private HoverView mHoverView;
    private MyRelativeLayout mKodi;
    private MyRelativeLayout mLocalView;
    private FrameLayout mMainFrameLayout;
    private MyRelativeLayout mMemory;
    private MyRelativeLayout mMiracast;
    private MyRelativeLayout mMusicView;
    private MyRelativeLayout mNetflix;
    private RequestQueue mQueue;
    private MyRelativeLayout mRecommendView;
    private MyRelativeLayout mSettingsView;
    private StatusLoader mStatusLoader;
    private TvInputManager mTvInputManager;
    private MyRelativeLayout mVideoView;
    private MyRelativeLayout mYoutube;
    private ImageButton pg_favorite;
    private ImageButton pg_home;
    private ImageButton query_button;
    private TextView tx_city;
    private TextView tx_condition;
    private TextView tx_temp;
    private String urlcity;
    private WifiManager wifiManager;
    private static final int[] childScreens = {1, 2, 4, 3, 5};
    private static final int[] childScreensTv = {2, 4, 3, 5};
    public static int HOME_SHORTCUT_COUNT = 11;
    public static TextView memory_used = null;
    public static ImageView memory_circle = null;
    private final String net_change_action = "android.net.conn.CONNECTIVITY_CHANGE";
    private final String wifi_signal_action = "android.net.wifi.RSSI_CHANGED";
    private final String outputmode_change_action = "android.amlogic.settings.CHANGE_OUTPUT_MODE";
    private final String DROIDVOLD_MEDIA_UNMOUNTED_ACTION = "com.droidvold.action.MEDIA_UNMOUNTED";
    private final String DROIDVOLD_MEDIA_EJECT_ACTION = "com.droidvold.action.MEDIA_EJECT";
    private final String DROIDVOLD_MEDIA_MOUNTED_ACTION = "com.droidvold.action.MEDIA_MOUNTED";

    public static final int TYPE_VIDEO                           = 0;
    public static final int TYPE_RECOMMEND                       = 1;
    public static final int TYPE_MUSIC                           = 2;
    public static final int TYPE_APP                             = 3;
    public static final int TYPE_LOCAL                           = 4;
    public static final int TYPE_SETTINGS                        = 5;
    public static final int TYPE_HOME_SHORTCUT                   = 6;
    public static final int TYPE_APP_SHORTCUT                    = 7;

    public static final int MODE_HOME                            = 0;
    public static final int MODE_VIDEO                           = 1;
    public static final int MODE_RECOMMEND                       = 2;
    public static final int MODE_MUSIC                           = 3;
    public static final int MODE_APP                             = 4;
    public static final int MODE_LOCAL                           = 5;

    private int current_screen_mode = 0;
    private int saveModeBeforeCustom = 0;
    private int[] mChildScreens = childScreens;
    private ViewGroup mHomeView = null;
    private AppLayout mSecondScreen = null;
    private View saveHomeFocusView = null;
    private MyGridLayout mHomeShortcutView = null;
    private CustomView mCustomView = null;
    private TvView tvView = null;
    private TextView tvPrompt = null;
    public int tvViewMode = -1;
    private int mTvTop = -1;
    private boolean isRadioChannel = false;
    private boolean isChannelBlocked = false;
    private boolean isAvNoSignal = false;
    private Object mlock = new Object();
    private boolean mTvStartPlaying = false;
    private final String weather_receive_action = "com.example.perference.shared_id";
    private LinearInterpolator lin = null;
    private long totalMemory = 0;
    private long availMemory = 0;
    private Handler handler = new Handler();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                Launcher.this.resetShortcutScreen(message.arg1);
            } else if (i == 1) {
                Launcher launcher = Launcher.this;
                launcher.resetShortcutScreen(launcher.current_screen_mode);
            } else if (i != 2) {
            } else {
                Launcher.this.CustomScreen((View) message.obj);
            }
        }
    };
    private BroadcastReceiver mediaReceiver = new BroadcastReceiver() { // from class: com.droidlogic.tvlauncher.Launcher.11
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            if ("android.intent.action.MEDIA_EJECT".equals(action) || "android.intent.action.MEDIA_UNMOUNTED".equals(action) || "android.intent.action.MEDIA_MOUNTED".equals(action) || action.equals("com.droidvold.action.MEDIA_UNMOUNTED") || action.equals("com.droidvold.action.MEDIA_EJECT") || action.equals("com.droidvold.action.MEDIA_MOUNTED")) {
                Launcher.this.displayStatus();
                Launcher.this.updateStatus();
            }
        }
    };
    private BroadcastReceiver netReceiver = new BroadcastReceiver() { // from class: com.droidlogic.tvlauncher.Launcher.12
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo;
            String action = intent.getAction();
            if (action == null) {
                return;
            }
            if (action.equals("android.intent.action.TIME_SET")) {
                Launcher.this.displayDate();
            }
            if (action.equals("android.net.conn.CONNECTIVITY_CHANGE") && (networkInfo = ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE)).getNetworkInfo(1)) != null && networkInfo.isAvailable()) {
                networkInfo.getTypeName();
                if (networkInfo.getType() == 1) {
                    Intent intent2 = new Intent();
                    intent2.setAction("com.weather.broadcast");
                    intent2.putExtra("launcher", "weather");
                    Launcher.this.sendBroadcast(intent2);
                    Log.d("MediaBoxlauncher", "send BroadcastReceiver++++++++=");
                }
            }
            if (action.equals("com.example.perference.shared_id")) {
                String string = intent.getExtras().getString("city");
                Log.d("MediaBoxlauncher", "weatherCity    is" + string);
                if (string != null) {
                    try {
                        Launcher.this.urlcity = "https://api.openweathermap.org/data/2.5/weather?q=" + string + "&units=metric&appid=b7587b51674093373a847ac72e033c85";
                        Launcher.this.getData(Launcher.this.urlcity);
                        Log.d("MediaBoxlauncher", "############Weather City is#######" + Launcher.this.urlcity);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent3 = new Intent();
                    intent3.setAction("com.send.info");
                    Launcher.this.sendBroadcast(intent3);
                }
            }
            if (action.equals("android.intent.action.TIME_TICK")) {
                Launcher.this.displayDate();
            } else if ("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(action) || "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(action)) {
                Launcher.this.updateAppList(intent);
            } else {
                Launcher.this.displayStatus();
                Launcher.this.updateStatus();
            }
        }
    };
    private BroadcastReceiver appReceiver = new BroadcastReceiver() { // from class: com.droidlogic.tvlauncher.Launcher.13
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.PACKAGE_CHANGED".equals(action) || "android.intent.action.PACKAGE_REMOVED".equals(action) || "android.intent.action.PACKAGE_ADDED".equals(action)) {
                Launcher.this.updateAppList(intent);
            }
        }
    };
    private BroadcastReceiver instabootReceiver = new BroadcastReceiver() { // from class: com.droidlogic.tvlauncher.Launcher.14
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("com.droidlogic.instaboot.RELOAD_APP_COMPLETED".equals(intent.getAction())) {
                Log.e("MediaBoxLauncher", "reloadappcompleted");
                Launcher.this.displayShortcuts();
            }
        }
    };
    private Handler mTvHandler = new Handler() { // from class: com.droidlogic.tvlauncher.Launcher.15
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                if (Launcher.this.isBootvideoStopped()) {
                    Log.d("MediaBoxLauncher", "======== bootvideo is stopped, and tvapp released, start tv play");
                    return;
                }
                Log.d("MediaBoxLauncher", "======== bootvideo is not stopped, or tvapp not released, wait it");
                Launcher.this.mTvHandler.sendEmptyMessageDelayed(0, 200L);
            } else if (i != 1) {
            } else {
                if (Launcher.this.isBootvideoStopped()) {
                    Log.d("MediaBoxLauncher", "======== bootvideo is stopped, start tv app");
                    Launcher.this.startTvApp();
                    Launcher.this.finish();
                    return;
                }
                Log.d("MediaBoxLauncher", "======== bootvideo is not stopped, wait it");
                Launcher.this.mTvHandler.sendEmptyMessageDelayed(1, 50L);
            }
        }
    };
    private final String ENGLISH = "en";
    private final String FRENCH = "fr";
    private final String ESPANOL = "es";
    private final String SPANISH = "sp";
    public final int ENGLISH_INDEX = 0;
    private final int FRENCH_INDEX = 1;
    private final int ESPANOL_INDEX = 2;

    private boolean checkNeedStartTvApp(boolean z) {
        return false;
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        skipUserSetup();
//        this.mTvInputManager = (TvInputManager) getSystemService(TV_INPUT_SERVICE);
//        if (this.mTvInputManager == null) {
//            setContentView(R.layout.main_box);
//        } else {
            setContentView(R.layout.main);
//        }
        Log.d("MediaBoxLauncher", "------onCreate");
        this.mMainFrameLayout = (FrameLayout) findViewById(R.id.layout_main);
        this.mBlackFrameLayout = (FrameLayout) findViewById(R.id.layout_black);
//        if (!checkNeedStartTvApp(false)) {
            this.mBlackFrameLayout.setVisibility(View.GONE);
            this.mMainFrameLayout.setVisibility(View.VISIBLE);
 //       }
 //       if (Build.VERSION.SDK_INT >= 25) {
//            COMPONENT_TV_APP = COMPONENT_LIVE_TV;
//        }
        this.mAppDataLoader = new AppDataLoader(this);
        this.mStatusLoader = new StatusLoader(this);
        initChildViews();
//        initWeather();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MEDIA_EJECT");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction("com.droidvold.action.MEDIA_UNMOUNTED");
        intentFilter.addAction("com.droidvold.action.MEDIA_MOUNTED");
        intentFilter.addAction("com.droidvold.action.MEDIA_EJECT");
        intentFilter.addDataScheme("file");
        registerReceiver(this.mediaReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter2.addAction("android.net.wifi.RSSI_CHANGED");
        intentFilter2.addAction("com.example.perference.shared_id");
        intentFilter2.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter2.addAction("android.intent.action.TIME_TICK");
        intentFilter2.addAction("android.intent.action.TIME_SET");
        intentFilter2.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter2.addAction("android.amlogic.settings.CHANGE_OUTPUT_MODE");
        intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
        intentFilter2.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        registerReceiver(this.netReceiver, intentFilter2);
        IntentFilter intentFilter3 = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter3.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter3.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter3.addDataScheme("package");
        registerReceiver(this.appReceiver, intentFilter3);
        IntentFilter intentFilter4 = new IntentFilter();
        intentFilter4.addAction("com.droidlogic.instaboot.RELOAD_APP_COMPLETED");
        registerReceiver(this.instabootReceiver, intentFilter4);
//        initMemory();
        new Thread(new Runnable() { // from class: com.droidlogic.tvlauncher.Launcher.1
            @Override // java.lang.Runnable
            public void run() {
                if (!new File("/data/data/com.droidlogic.tvlauncher").canWrite()) {
                    Launcher.this.handler.postDelayed(this, 500L);
                    return;
                }
                Log.i("MediaBoxLauncher", "Runnable() : wFile.canWrite() = true");
                Launcher.this.mAppDataLoader.update();
                Launcher launcher = Launcher.this;
                launcher.setShortcutScreen(launcher.current_screen_mode);
            }
        }).start();
        setFirstFocus();
    }

    private void setFirstFocus() {
        this.mNetflix.requestFocus();
    }

    private void initMemory() {
        memory_used = (TextView) findViewById(R.id.memory_used);
        memory_circle = (ImageView) findViewById(R.id.memory_circle);
        this.animation = AnimationUtils.loadAnimation(this, R.anim.memory_cleaner_recircle);
        this.lin = new LinearInterpolator();
        this.totalMemory = MemoryManager.getTotalMemory();
        this.availMemory = MemoryManager.getAvailMemory(this);
        initMemoryAction();
    }

    private void initMemoryAction() {
        memory_used.setText(FormatData.formatRate(this.totalMemory, this.availMemory));
        this.animation.setInterpolator(this.lin);
        new Thread() { // from class: com.droidlogic.tvlauncher.Launcher.2
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                Launcher.this.totalMemory = MemoryManager.getTotalMemory();
                Launcher launcher = Launcher.this;
                launcher.availMemory = MemoryManager.getAvailMemory(launcher);
                Launcher.memory_used.setText(FormatData.formatRate(Launcher.this.totalMemory, Launcher.this.availMemory));
                Launcher.this.handler.postDelayed(this, 400L);
            }
        }.start();
        this.mMemory.setOnClickListener(new View.OnClickListener() { // from class: com.droidlogic.tvlauncher.Launcher.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Launcher.this.startMemoryAnimation();
                MemoryManager.cleanMemory(Launcher.this);
                Launcher.memory_used.setText(FormatData.formatRate(Launcher.this.totalMemory, Launcher.this.availMemory));
            }
        });
        this.mMemory.setOnTouchListener(new View.OnTouchListener() { // from class: com.droidlogic.tvlauncher.Launcher.4
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Launcher.this.startMemoryAnimation();
                MemoryManager.cleanMemory(Launcher.this);
                Launcher.memory_used.setText(FormatData.formatRate(Launcher.this.totalMemory, Launcher.this.availMemory));
                return false;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startMemoryAnimation() {
        final long currentTimeMillis = System.currentTimeMillis();
        memory_circle.startAnimation(this.animation);
        Log.d("MediaBoxLauncher", "cleanMemory");
        new Thread() { // from class: com.droidlogic.xlauncher.Launcher.5
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                if (System.currentTimeMillis() - currentTimeMillis < 2500) {
                    Launcher.this.handler.postDelayed(this, 800L);
                } else {
                    Launcher.memory_circle.clearAnimation();
                }
            }
        }.start();
    }

    private void initWeather() {
        this.pg_home = (ImageButton) findViewById(R.id.pg_home);
        this.pg_favorite = (ImageButton) findViewById(R.id.pg_favorite);
        this.pg_home.setOnFocusChangeListener(new TitleOnFocus());
        this.pg_favorite.setOnFocusChangeListener(new TitleOnFocus());
        this.pg_home.setOnClickListener(new TitleClick());
        this.pg_favorite.setOnClickListener(new TitleClick());
        this.img_weather = (ImageView) findViewById(R.id.img_weather);
        this.home_line = (ImageView) findViewById(R.id.home_line);
        this.tx_temp = (TextView) findViewById(R.id.tx_temp);
        this.tx_city = (TextView) findViewById(R.id.tx_city);
        this.tx_condition = (TextView) findViewById(R.id.tx_condition);
        this.mQueue = Volley.newRequestQueue(this);
        this.etCity = (EditText) findViewById(R.id.eth_editext);
        this.query_button = (ImageButton) findViewById(R.id.query_button);
        this.etCity.setOnFocusChangeListener(new TitleOnFocus());
        this.query_button.setOnFocusChangeListener(new TitleOnFocus());
        this.query_button.setOnClickListener(new TitleClick());
        this.wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class TitleOnFocus implements View.OnFocusChangeListener {
        TitleOnFocus() {
        }

        @Override // android.view.View.OnFocusChangeListener
        public void onFocusChange(View view, boolean z) {
            if (z) {
                int id = view.getId();
                if (id != R.id.eth_editext) {
                    switch (id) {
                        case R.id.pg_favorite /* 2131034167 */:
                            Launcher.this.pg_favorite.setBackgroundResource(R.drawable.favorite_green);
                            Launcher.this.pg_home.setBackgroundResource(R.drawable.home_white);
                            Launcher.this.mHoverView.clear();
                            break;
                        case R.id.pg_home /* 2131034168 */:
                            Launcher.this.pg_home.setBackgroundResource(R.drawable.home_green);
                            Launcher.this.mHoverView.clear();
                            break;
                        case R.id.query_button /* 2131034169 */:
                            Launcher.this.mHoverView.clear();
                            Launcher.this.query_button.setBackgroundResource(R.drawable.search_change);
                            Launcher.this.etCity.setVisibility(View.VISIBLE);
                            Toast.makeText(Launcher.this, "Please enter a city name", Toast.LENGTH_LONG).show();
                            break;
                    }
                } else {
                    Launcher.this.mHoverView.clear();
                }
            } else {
                int id2 = view.getId();
                if (id2 != R.id.eth_editext) {
                    switch (id2) {
                        case R.id.pg_favorite /* 2131034167 */:
                            Launcher.this.pg_favorite.setBackgroundResource(R.drawable.favorite_white);
                            break;
                        case R.id.pg_home /* 2131034168 */:
                            Launcher.this.pg_home.setBackgroundResource(R.drawable.home_white);
                            break;
                        case R.id.query_button /* 2131034169 */:
                            Launcher.this.query_button.setBackgroundResource(R.drawable.search);
                            break;
                    }
                } else {
                    Launcher.this.etCity.setVisibility(View.GONE);
                }
            }
            if (!Launcher.this.etCity.hasFocus() && !Launcher.this.query_button.hasFocus()) {
                Launcher.this.etCity.setVisibility(View.GONE);
            }
            if (Launcher.this.pg_home.hasFocus() || Launcher.this.pg_favorite.hasFocus()) {
                return;
            }
            Launcher.this.pg_home.setBackgroundResource(R.drawable.home_green);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class TitleClick implements View.OnClickListener {
        TitleClick() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.pg_favorite /* 2131034167 */:
                    Launcher.this.showSecondScreen(5);
                    return;
                case R.id.pg_home /* 2131034168 */:
                default:
                    return;
                case R.id.query_button /* 2131034169 */:
                    Launcher.this.etCity.setVisibility(View.VISIBLE);
                    String obj = Launcher.this.etCity.getText().toString();
                    if (!obj.isEmpty()) {
                        Launcher.this.tx_city.setVisibility(View.GONE);
                        Log.d("MediaBoxLauncher", "etCity========" + obj);
                        Launcher.this.getData("https://api.openweathermap.org/data/2.5/weather?q=" + obj + "&units=metric&appid=b7587b51674093373a847ac72e033c85");
                        return;
                    }
                    Toast.makeText(Launcher.this, "Please enter a city name", Toast.LENGTH_LONG).show();
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showSecondScreen(int i) {
        setHomeViewVisible(false);
        setShortcutScreen(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getData(String str) {
        this.mQueue.add(new JsonObjectRequest(str, null, new Response.Listener<JSONObject>() { // from class: com.droidlogic.xlauncher.Launcher.6
            @Override // com.android.volley.Response.Listener
            public void onResponse(JSONObject jSONObject) {
                try {
                    String str2 = jSONObject.getString("name").toString();
                    String string = jSONObject.getJSONObject("main").getString("temp");
                    jSONObject.getJSONArray("weather").getJSONObject(0).getString("description");
                    Launcher.this.tx_city.setText(str2);
                    Launcher.this.tx_city.setVisibility(View.VISIBLE);
                    Launcher.this.home_line.setVisibility(View.VISIBLE);
                    Log.d("MediaBoxLauncher", "weather mtemperature=====" + Double.parseDouble(string));
                    if (!Locale.getDefault().getLanguage().equals("en")) {
                        Launcher.this.tx_temp.setText("" + string + Launcher.this.getResources().getString(R.string.str_temp));
                    } else {
                        DecimalFormat decimalFormat = new DecimalFormat("#####0.0");
                        Launcher.this.tx_temp.setText("" + decimalFormat.format((Double.parseDouble(string) * 1.8d) + 32.0d) + Launcher.this.getResources().getString(R.string.str_eu_temp));
                    }
                    String string2 = jSONObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                    Log.d("MediaBoxLauncher", "weather iconString=====" + string2);
                    Launcher.this.img_weather.setImageResource(Launcher.this.parseIcon(string2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { // from class: com.droidlogic.tvlauncher.Launcher.7
            @Override // com.android.volley.Response.ErrorListener
            public void onErrorResponse(VolleyError volleyError) {
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int parseIcon(String str) {
        if (str == null) {
            return -1;
        }
        return ("01n".equals(str) || "01d".equals(str)) ? R.drawable.sunny : "04n".equals(str) ? R.drawable.partly_cloudy : ("04d".equals(str) || "02n".equals(str) || "02d".equals(str) || "03n".equals(str) || "03d".equals(str)) ? R.drawable.overcast : ("09n".equals(str) || "09d".equals(str)) ? R.drawable.thunder : ("10n".equals(str) || "10d".equals(str)) ? R.drawable.light_rain : ("11n".equals(str) || "11d".equals(str) || "13n".equals(str) || "13d".equals(str)) ? R.drawable.snow : (!"50n".equals(str) && "50d".equals(str)) ? R.drawable.smoke : R.drawable.sunny;
    }

    private void releasePlayingTv() {
        Log.d("MediaBoxLauncher", "------releasePlayingTv");
        this.isChannelBlocked = false;
        recycleBigBackgroundDrawable();
        this.mTvHandler.removeMessages(0);
        releaseTvView();
        this.mTvStartPlaying = false;
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        Log.d("MediaBoxLauncher", "------onResume");
        if (checkNeedStartTvApp(true)) {
            this.mTvHandler.sendEmptyMessage(1);
            return;
        }
        if (this.mMainFrameLayout.getVisibility() != View.VISIBLE) {
            this.mBlackFrameLayout.setVisibility(View.GONE);
            this.mMainFrameLayout.setVisibility(View.VISIBLE);
        }
        setBigBackgroundDrawable();
        displayShortcuts();
        displayStatus();
        displayDate();
        TvView tvView = this.tvView;
        if (tvView != null) {
            tvView.setVisibility(View.INVISIBLE);
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        this.mHandler.removeMessages(2);
        Log.d("MediaBoxLauncher", "------onPause");
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        Log.d("MediaBoxLauncher", "------onStop");
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        unregisterReceiver(this.mediaReceiver);
        unregisterReceiver(this.netReceiver);
        unregisterReceiver(this.appReceiver);
        unregisterReceiver(this.instabootReceiver);
        super.onDestroy();
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if ("android.intent.action.MAIN".equals(intent.getAction())) {
            setHomeViewVisible(true);
            this.current_screen_mode = 0;
            ((MyRelativeLayout) findViewById(R.id.layout_netflix)).requestFocus();
        } else if (intent.getAction().equals("android.intent.action.ALLAPPS")) {
            Log.d("MediaBoxLauncher", " ----keycode f7 process all apps");
            setHomeViewVisible(false);
            setShortcutScreen(4);
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            startX = motionEvent.getX();
        } else if (motionEvent.getAction() == 1) {
            endX = motionEvent.getX();
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent motionEvent) {
        motionEvent.getAction();
        return true;
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i == 4) {
            switch (this.current_screen_mode) {
                case 4:
                    Log.d("MediaBoxLauncher", "++++onkey down APP");
                    LedControl.control_led_status(getResources().getString(R.string.app_led), false);
                    this.mSecondScreen.clearAnimation();
                case 1:
                case 2:
                case 3:
                case 5:
                    setHomeViewVisible(true);
                    break;
                case 6:
                    this.current_screen_mode = this.saveModeBeforeCustom;
                    this.mAppDataLoader.update();
                    break;
            }
            return true;
        }
        if (i != 23 && i != 66) {
            if (i == 84) {
                ComponentName globalSearchActivity = ((SearchManager) getSystemService(SEARCH_SERVICE)).getGlobalSearchActivity();
                if (globalSearchActivity == null) {
                    return false;
                }
                Intent intent = new Intent("android.search.action.GLOBAL_SEARCH");
                intent.addFlags(268435456);
                intent.setComponent(globalSearchActivity);
                Bundle bundle = new Bundle();
                bundle.putString("source", "launcher-search");
                intent.putExtra("app_data", bundle);
                startActivity(intent);
                return true;
            } else if (i == 178) {
                startTvSource();
                return true;
            } else if (i == 139) {
                startMemoryAnimation();
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayStatus() {
        this.lv_status.setAdapter((ListAdapter) new LocalAdapter(this, this.mStatusLoader.getStatusData(), R.layout.homelist_item, new String[]{"item_icon"}, new int[]{R.id.item_type}));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void displayDate() {
        TextView textView = (TextView) findViewById(R.id.tx_time);
        TextView textView2 = (TextView) findViewById(R.id.tx_date);
        textView.setText(this.mStatusLoader.getTime());
        textView2.setText(this.mStatusLoader.getDate());
    }

    private void initChildViews() {
        this.lv_status = (GridView) findViewById(R.id.list_status);
        this.lv_status.setFocusable(false);
        this.lv_status.setFocusableInTouchMode(false);
        this.lv_status.setOnTouchListener(new View.OnTouchListener() { // from class: com.droidlogic.tvlauncher.Launcher.8
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return motionEvent.getAction() == 2;
            }
        });
        this.mHoverView = (HoverView) findViewById(R.id.hover_view);
        this.mHomeView = (ViewGroup) findViewById(R.id.layout_homepage);
        this.mSecondScreen = (AppLayout) findViewById(R.id.second_screen);
        this.mHomeShortcutView = (MyGridLayout) findViewById(R.id.gv_shortcut);
        this.mVideoView = (MyRelativeLayout) findViewById(R.id.layout_video);
        this.mRecommendView = (MyRelativeLayout) findViewById(R.id.layout_recommend);
        this.mMusicView = (MyRelativeLayout) findViewById(R.id.layout_music);
        this.mAppView = (MyRelativeLayout) findViewById(R.id.layout_app);
        this.mLocalView = (MyRelativeLayout) findViewById(R.id.layout_local);
        this.mSettingsView = (MyRelativeLayout) findViewById(R.id.layout_setting);
        this.mGoogleplay = (MyRelativeLayout) findViewById(R.id.layout_googleplay);
        this.mKodi = (MyRelativeLayout) findViewById(R.id.layout_kodi);
        this.mBrowser = (MyRelativeLayout) findViewById(R.id.layout_browser);
        this.mMemory = (MyRelativeLayout) findViewById(R.id.layout_memory);
        this.mYoutube = (MyRelativeLayout) findViewById(R.id.layout_youtube);
        this.mNetflix = (MyRelativeLayout) findViewById(R.id.layout_netflix);
        this.mMiracast = (MyRelativeLayout) findViewById(R.id.layout_miracast);
        this.mFilemanager = (MyRelativeLayout) findViewById(R.id.layout_filemanager);
        setHomeRectType();
 //       this.tvView = (TvView) findViewById(R.id.tv_view);
 //       this.tvPrompt = (TextView) findViewById(R.id.tx_tv_prompt);
        this.mChildScreens = childScreens;
 //       this.tvPrompt.setVisibility(View.GONE);
    //    TvView tvView = this.tvView;
 //       if (tvView != null) {
  //          tvView.setVisibility(View.GONE);
 //       }
    }

    private void setBigBackgroundDrawable() {
        getMainView().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));
        ((ImageView) findViewById(R.id.img_video)).setImageDrawable(getResources().getDrawable(R.drawable.img_video));
        ((ImageView) findViewById(R.id.img_recommend)).setImageDrawable(getResources().getDrawable(R.drawable.img_recommend));
        ((ImageView) findViewById(R.id.img_music)).setImageDrawable(getResources().getDrawable(R.drawable.img_music));
        ((ImageView) findViewById(R.id.img_app)).setImageDrawable(getResources().getDrawable(R.drawable.img_app));
        ((ImageView) findViewById(R.id.img_local)).setImageDrawable(getResources().getDrawable(R.drawable.img_local));
        ((ImageView) findViewById(R.id.img_setting)).setImageDrawable(getResources().getDrawable(R.drawable.img_setting));
        ((ImageView) findViewById(R.id.img_googleplay)).setImageDrawable(getResources().getDrawable(R.drawable.img_googleplay));
        ((ImageView) findViewById(R.id.img_kodi)).setImageDrawable(getResources().getDrawable(R.drawable.img_kodi));
        ((ImageView) findViewById(R.id.img_browser)).setImageDrawable(getResources().getDrawable(R.drawable.img_browser));
        ((ImageView) findViewById(R.id.img_memory)).setImageDrawable(getResources().getDrawable(R.drawable.img_memory));
        ((ImageView) findViewById(R.id.img_youtube)).setImageDrawable(getResources().getDrawable(R.drawable.img_youtube));
        ((ImageView) findViewById(R.id.img_netflix)).setImageDrawable(getResources().getDrawable(R.drawable.img_netflix));
        ((ImageView) findViewById(R.id.img_miracast)).setImageDrawable(getResources().getDrawable(R.drawable.img_miracast));
        ((ImageView) findViewById(R.id.img_filemanager)).setImageDrawable(getResources().getDrawable(R.drawable.img_filemanager));
    }

    private void recycleBigBackgroundDrawable() {
        Drawable background = getMainView().getBackground();
        getMainView().setBackgroundResource(0);
        if (background != null) {
            background.setCallback(null);
        }
        Drawable drawable = ((ImageView) findViewById(R.id.img_video)).getDrawable();
        if (drawable != null) {
            drawable.setCallback(null);
            Log.d("MediaBoxLauncher", "recycle-img_video");
        }
        Drawable drawable2 = ((ImageView) findViewById(R.id.img_local)).getDrawable();
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        Drawable drawable3 = ((ImageView) findViewById(R.id.img_music)).getDrawable();
        if (drawable3 != null) {
            drawable3.setCallback(null);
        }
        Drawable drawable4 = ((ImageView) findViewById(R.id.img_recommend)).getDrawable();
        if (drawable4 != null) {
            drawable4.setCallback(null);
        }
        Drawable drawable5 = ((ImageView) findViewById(R.id.img_setting)).getDrawable();
        if (drawable5 != null) {
            drawable5.setCallback(null);
        }
        Drawable drawable6 = ((ImageView) findViewById(R.id.img_app)).getDrawable();
        if (drawable6 != null) {
            drawable6.setCallback(null);
        }
        Drawable drawable7 = ((ImageView) findViewById(R.id.img_googleplay)).getDrawable();
        if (drawable7 != null) {
            drawable7.setCallback(null);
        }
        Drawable drawable8 = ((ImageView) findViewById(R.id.img_kodi)).getDrawable();
        if (drawable8 != null) {
            drawable8.setCallback(null);
        }
        Drawable drawable9 = ((ImageView) findViewById(R.id.img_browser)).getDrawable();
        if (drawable9 != null) {
            drawable9.setCallback(null);
        }
        Drawable drawable10 = ((ImageView) findViewById(R.id.img_memory)).getDrawable();
        if (drawable10 != null) {
            drawable10.setCallback(null);
        }
        Drawable drawable11 = ((ImageView) findViewById(R.id.img_youtube)).getDrawable();
        if (drawable11 != null) {
            drawable11.setCallback(null);
        }
        Drawable drawable12 = ((ImageView) findViewById(R.id.img_netflix)).getDrawable();
        if (drawable12 != null) {
            drawable12.setCallback(null);
        }
        Drawable drawable13 = ((ImageView) findViewById(R.id.img_miracast)).getDrawable();
        if (drawable13 != null) {
            drawable13.setCallback(null);
        }
        ((ImageView) findViewById(R.id.img_filemanager)).getDrawable();
    }

    private void setHomeRectType() {
        this.mVideoView.setType(0);
        this.mMusicView.setType(2);
        this.mRecommendView.setType(1);
        this.mAppView.setType(3);
        this.mLocalView.setType(4);
        this.mMemory.setType(13);
        Intent intent = new Intent();
        intent.setComponent(ComponentName.unflattenFromString(COMPONENT_TV_SETTINGS));
        this.mSettingsView.setType(5);
        this.mSettingsView.setIntent(intent);
        this.mGoogleplay.setType(10);
        this.mKodi.setType(11);
        this.mBrowser.setType(12);
        this.mYoutube.setType(14);
        this.mNetflix.setType(15);
        Intent intent2 = new Intent();
        intent2.setComponent(ComponentName.unflattenFromString(COMPONENT_TV_MIRACAST));
        this.mMiracast.setType(16);
        this.mMiracast.setIntent(intent2);
        Intent intent3 = new Intent();
        intent3.setComponent(ComponentName.unflattenFromString(COMPONENT_TV_FILEMANAGER));
        this.mFilemanager.setType(17);
        this.mFilemanager.setIntent(intent3);
    }

    public void displayShortcuts() {
        this.mAppDataLoader.update();
        int i = this.current_screen_mode;
        if (i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5) {
            setShortcutScreen(this.current_screen_mode);
        } else {
            setShortcutScreen(this.saveModeBeforeCustom);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStatus() {
        ((BaseAdapter) this.lv_status.getAdapter()).notifyDataSetChanged();
    }

    public void setShortcutScreen(int i) {
        resetShortcutScreen(i);
        this.current_screen_mode = i;
        if (i == 4) {
            LedControl.control_led_status(getResources().getString(R.string.app_led), true);
        } else {
            LedControl.control_led_status(getResources().getString(R.string.app_led), false);
        }
    }

    public void resetShortcutScreen(int i) {
        this.mHandler.removeMessages(0);
        Log.d("MediaBoxLauncher", "resetShortcutScreen mode is " + i);
        if (this.mAppDataLoader.isDataLoaded()) {
            if (i == 0) {
                this.mHomeShortcutView.setLayoutView(i, this.mAppDataLoader.getShortcutList(i));
                return;
            } else {
                this.mSecondScreen.setLayout(i, this.mAppDataLoader.getShortcutList(i));
                return;
            }
        }
        Message message = new Message();
        message.what = 0;
        message.arg1 = i;
        this.mHandler.sendMessageDelayed(message, 100L);
    }

    private int getChildModeIndex() {
        int i = 0;
        while (true) {
            int[] iArr = this.mChildScreens;
            if (i >= iArr.length) {
                return -1;
            }
            if (this.current_screen_mode == iArr[i]) {
                return i;
            }
            i++;
        }
    }

    public AppDataLoader getAppDataLoader() {
        return this.mAppDataLoader;
    }

    public void switchSecondScren(int animType){
        int mode = -1;
        if (animType == AppLayout.ALIGN_LEFT) {
            mode = mChildScreens[(getChildModeIndex() + mChildScreens.length - 1) % mChildScreens.length];
        } else {
            mode = mChildScreens[(getChildModeIndex() + 1) % mChildScreens.length];
        }
        mSecondScreen.setLayoutWithAnim(animType, mode, mAppDataLoader.getShortcutList(mode));
        current_screen_mode = mode;
    }

    public void setHomeViewVisible(boolean z) {
        if (z) {
            CustomView customView = this.mCustomView;
            if (customView != null && this.current_screen_mode == 6) {
                customView.recoverMainView();
            }
            this.current_screen_mode = 0;
            this.mSecondScreen.setVisibility(View.GONE);
            this.mHomeView.setVisibility(View.VISIBLE);
//            memory_circle.setVisibility(View.VISIBLE);
//            memory_used.setVisibility(View.VISIBLE);
            //setTvViewPosition(0);
            return;
        }
        this.mHomeView.setVisibility(View.GONE);
//        memory_circle.setVisibility(View.GONE);
//        memory_used.setVisibility(View.GONE);
        this.mSecondScreen.setVisibility(View.VISIBLE);
    }

    public HoverView getHoverView() {
        return this.mHoverView;
    }

    public ViewGroup getMainView() {
        return (ViewGroup) findViewById(R.id.layout_main);
    }

    public ViewGroup getRootView() {
        return (ViewGroup) findViewById(R.id.layout_root);
    }

    public Object getLock() {
        return this.mlock;
    }

    public void saveHomeFocus(View view) {
        this.saveHomeFocusView = view;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAppList(Intent intent) {
        String schemeSpecificPart;
        if (intent.getData() == null || !((schemeSpecificPart = intent.getData().getSchemeSpecificPart()) == null || schemeSpecificPart.length() == 0 || schemeSpecificPart.equals("com.android.provision"))) {
            displayShortcuts();
        }
    }

    public void startTvSource() {
        try {
            Intent intent = new Intent();
            intent.setComponent(ComponentName.unflattenFromString(COMPONENT_TV_SOURCE));
            intent.putExtra("requestpackage", "com.droidlogic.tvlauncher");
            startActivityForResult(intent, 3);
        } catch (ActivityNotFoundException e) {
            Log.e("MediaBoxLauncher", " can't start TvSources:" + e);
        }
    }

    @Override // android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        Log.d("MediaBoxLauncher", "onActivityResult requestCode = " + i + ", resultCode = " + i2);
        if (i == 3 && i2 == -1 && intent != null) {
            if (this.mTvStartPlaying) {
                releasePlayingTv();
            }
            try {
                startActivity(intent);
                finish();
            } catch (ActivityNotFoundException e) {
                Log.e("MediaBoxLauncher", " can't start LiveTv:" + e);
            }
        }
    }

    public void startTvApp() {
        try {
            Intent intent = new Intent();
            intent.setComponent(ComponentName.unflattenFromString(COMPONENT_TV_APP));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e("MediaBoxLauncher", " can't start TvSettings:" + e);
        }
    }

    public void startCustomScreen(View view) {
        Message message = new Message();
        message.what = 2;
        message.obj = view;
        this.mHandler.sendMessageDelayed(message, 500L);
    }

    public void CustomScreen(View view) {
        if (this.current_screen_mode == 6) {
            return;
        }
        this.mHoverView.clear();
        int i = this.current_screen_mode;
        this.saveModeBeforeCustom = i;
        this.mCustomView = new CustomView(this, view, i);
        this.current_screen_mode = 6;
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        if (rect.top > getResources().getDisplayMetrics().heightPixels / 2) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
            layoutParams.addRule(12);
            getRootView().addView(this.mCustomView, layoutParams);
        } else {
            getRootView().addView(this.mCustomView);
        }
        getMainView().bringToFront();
    }

    public void recoverFromCustom() {
        this.mHandler.sendEmptyMessage(1);
        getMainView().setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        getMainView().requestFocus();
    }

    public static int dipToPx(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public void setTvViewPosition(int i) {
        int dipToPx;
        int dipToPx2;
        int dipToPx3;
        int dipToPx4;
        int i2;
        this.tvViewMode = i;
        int i3 = 0;
        if (i == 1) {
            i3 = -dipToPx(this, 545.0f);
        } else if (i != 2) {
            dipToPx = dipToPx(this, 120.0f);
            dipToPx2 = dipToPx(this, 197.0f);
            dipToPx3 = dipToPx(this, 310.0f) + dipToPx;
            dipToPx4 = dipToPx(this, 174.0f) + dipToPx2;
            i2 = 0;
//            HoverView.setViewPosition(this.tvView, new Rect(dipToPx, dipToPx2, dipToPx3, dipToPx4));
//            HoverView.setViewPosition(this.tvPrompt, new Rect(dipToPx, dipToPx2, dipToPx3, dipToPx4));
            float f = i3;
            long j = i2;
//            this.tvView.animate().translationY(f).setDuration(j).start();
//            this.tvPrompt.animate().translationY(f).setDuration(j).start();
        }
        dipToPx = dipToPx(this, 969.0f);
        dipToPx2 = dipToPx(this, 545.0f);
        dipToPx3 = dipToPx(this, 310.0f) + dipToPx;
        dipToPx4 = dipToPx(this, 174.0f) + dipToPx2;
        i2 = 500;
//        HoverView.setViewPosition(this.tvView, new Rect(dipToPx, dipToPx2, dipToPx3, dipToPx4));
//        HoverView.setViewPosition(this.tvPrompt, new Rect(dipToPx, dipToPx2, dipToPx3, dipToPx4));
        float f2 = i3;
        long j2 = i2;
//        this.tvView.animate().translationY(f2).setDuration(j2).start();
//        this.tvPrompt.animate().translationY(f2).setDuration(j2).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isBootvideoStopped() {
        return TextUtils.equals(SystemProperties.get("init.svc.bootanim", "running"), "stopped") && TextUtils.equals(SystemProperties.get("dev.bootcomplete", "0"), "1") && getContentResolver().acquireContentProviderClient("android.media.tv") != null;
    }

    private void releaseTvView() {
        this.tvView.setVisibility(View.GONE);
        this.tvView.reset();
    }

    private void skipUserSetup() {
        if (Settings.Secure.getInt(getContentResolver(), "tv_user_setup_complete", 0) == 0) {
            Log.d("MediaBoxLauncher", "force skip user setup, or we can't use home key");
            Settings.Global.putInt(getContentResolver(), "device_provisioned", 1);
            Settings.Secure.putInt(getContentResolver(), "user_setup_complete", 1);
            Settings.Secure.putInt(getContentResolver(), "tv_user_setup_complete", 1);
        }
    }
}
