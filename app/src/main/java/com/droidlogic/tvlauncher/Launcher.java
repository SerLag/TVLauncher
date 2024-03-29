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
import android.media.tv.TvView;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;


public class Launcher extends Activity{
    private final static String TAG="MediaBoxlauncher";
    public static float endX = 0.0f;
    public static float startX;
    private Animation animation;
    private EditText etCity;
    private ImageView home_line;
    private ImageView img_weather;
    private GridView lv_status;
    private AppDataLoader mAppDataLoader;
    private MyRelativeLayout mAppView;
    private MyRelativeLayout mFilemanager;

    private HoverView mHoverView;

    private FrameLayout mMainFrameLayout;


    private MyRelativeLayout[] mAction;

    private RequestQueue mQueue;

    private MyRelativeLayout mSettingsView;
    private StatusLoader mStatusLoader;

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
    public static int HOME_SHORTCUT_COUNT = 10;
    public static TextView memory_used = null;
    public static ImageView memory_circle = null;
    public static MyRelativeLayout mMemory;

    public static final int TYPE_HOME                            = 0;
    public static final int TYPE_SETTINGS                        = 1;
    public static final int TYPE_FILEMANAGER                     = 2;
    public static final int TYPE_APPS                            = 3;
    public static final int TYPE_HOME_SHORTCUT                   = 4;
    public static final int TYPE_APP_SHORTCUT                    = 5;

    public static final int MODE_HOME                            = 0;
    public static final int MODE_APP                             = 1;
    public static final int MODE_LOCAL                           = 2;

    private static final int MSG_REFRESH_SHORTCUT                = 0;
    private static final int MSG_RECOVER_HOME                    = 1;
    private static final int animDuration                        = 70;
    private static final int animDelay                           = 0;

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
    private Object mlock = new Object();
    private boolean mTvStartPlaying = false;
    private LinearInterpolator lin = null;
    private long totalMemory = 0;
    private long availMemory = 0;
    private Handler handler = new Handler();

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_SHORTCUT:
                    resetShortcutScreen(msg.arg1);
                    break;
                case MSG_RECOVER_HOME:
                    resetShortcutScreen(current_screen_mode);
                    break;
                default:
                    break;
            }
        }
    };
    private BroadcastReceiver mediaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG,intent.getAction().toString());
            Launcher.this.displayStatus();
            Launcher.this.updateStatus();
        }
    };
    private BroadcastReceiver netReceiver = new BroadcastReceiver() { // from class: com.droidlogic.tvlauncher.Launcher.12
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo;
            String action = intent.getAction();
            Log.i(TAG,"netReceiver" + action.toString());
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
                    Log.d(TAG, "send BroadcastReceiver++++++++=");
                }
            }
            if (action.equals("com.example.perference.shared_id")) {
                String string = intent.getExtras().getString("city");
                Log.d(TAG, "weatherCity    is" + string);
                if (string != null) {
                    try {
                        Launcher.this.urlcity = "https://api.openweathermap.org/data/2.5/weather?q=" + string + "&units=metric&appid=b7587b51674093373a847ac72e033c85";
                        Launcher.this.getData(Launcher.this.urlcity);
                        Log.d(TAG, "############Weather City is#######" + Launcher.this.urlcity);
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
            Log.i(TAG,"appReceiver" + intent.getAction().toString());
            if ("android.intent.action.PACKAGE_CHANGED".equals(action) || "android.intent.action.PACKAGE_REMOVED".equals(action) || "android.intent.action.PACKAGE_ADDED".equals(action)) {
                Launcher.this.updateAppList(intent);
            }
        }
    };

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.main);
        Log.d(TAG, "------onCreate");

        copyResources(R.raw.home_shortcuts);
        copyResources(R.raw.local_shortcuts);
        copyResources(R.drawable.img_video);
        copyResources(R.drawable.img_youtube);
        copyResources(R.drawable.img_kodi);
        copyResources(R.drawable.img_miracast);
        copyResources(R.drawable.img_chrome);
        copyResources(R.drawable.img_google);

        this.mAction = new MyRelativeLayout[6];
        this.mMainFrameLayout = (FrameLayout) findViewById(R.id.layout_main);
        this.mMainFrameLayout.setVisibility(View.VISIBLE);
        this.mAppDataLoader = new AppDataLoader(this);
        this.mStatusLoader = new StatusLoader(this);
        initChildViews();
//        initWeather();
//        initMemory();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!getFilesDir().canWrite()) {
                    Launcher.this.handler.postDelayed(this, 500L);
                    return;
                }
                Log.i(TAG, "Runnable() : wFile.canWrite() = true");
                Launcher.this.mAppDataLoader.update();
                Launcher launcher = Launcher.this;
                launcher.setShortcutScreen(launcher.current_screen_mode);
            }
        }).start();
        setFirstFocus();
    }

    private void setFirstFocus() {
        this.mAction[0].requestFocus();
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
        Log.d(TAG, "cleanMemory");
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
                        Log.d(TAG, "etCity========" + obj);
                        Launcher.this.getData("https://api.openweathermap.org/data/2.5/weather?q=" + obj + "&units=metric&appid=b7587b51674093373a847ac72e033c85");
                        return;
                    }
                    Toast.makeText(Launcher.this, "Please enter a city name", Toast.LENGTH_LONG).show();
                    return;
            }
        }
    }

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
                    Log.d(TAG, "weather mtemperature=====" + Double.parseDouble(string));
                    if (!Locale.getDefault().getLanguage().equals("en")) {
                        Launcher.this.tx_temp.setText("" + string + Launcher.this.getResources().getString(R.string.str_temp));
                    } else {
                        DecimalFormat decimalFormat = new DecimalFormat("#####0.0");
                        Launcher.this.tx_temp.setText("" + decimalFormat.format((Double.parseDouble(string) * 1.8d) + 32.0d) + Launcher.this.getResources().getString(R.string.str_eu_temp));
                    }
                    String string2 = jSONObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                    Log.d(TAG, "weather iconString=====" + string2);
                    Launcher.this.img_weather.setImageResource(Launcher.this.parseIcon(string2));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() { // from class: com.droidlogic.tvlauncher.Launcher.7
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }));
    }

    public int parseIcon(String str) {
        if (str == null) {
            return -1;
        }
        return ("01n".equals(str) || "01d".equals(str)) ? R.drawable.sunny : "04n".equals(str) ? R.drawable.partly_cloudy : ("04d".equals(str) || "02n".equals(str) || "02d".equals(str) || "03n".equals(str) || "03d".equals(str)) ? R.drawable.overcast : ("09n".equals(str) || "09d".equals(str)) ? R.drawable.thunder : ("10n".equals(str) || "10d".equals(str)) ? R.drawable.light_rain : ("11n".equals(str) || "11d".equals(str) || "13n".equals(str) || "13d".equals(str)) ? R.drawable.snow : (!"50n".equals(str) && "50d".equals(str)) ? R.drawable.smoke : R.drawable.sunny;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "------onResume");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.MEDIA_EJECT");
        filter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        filter.addAction("android.intent.action.MEDIA_MOUNTED");
        filter.addDataScheme("file");
        registerReceiver(this.mediaReceiver, filter);

        filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.RSSI_CHANGED");
        filter.addAction("android.intent.action.TIME_TICK");
        filter.addAction("android.intent.action.TIME_SET");
        registerReceiver(this.netReceiver, filter);

        filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addAction("android.intent.action.PACKAGE_CHANGED");
        filter.addDataScheme("package");
        registerReceiver(this.appReceiver, filter);

        displayShortcuts();
        displayStatus();
        displayDate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "------onPause");
        unregisterReceiver(this.mediaReceiver);
        unregisterReceiver(this.netReceiver);
        unregisterReceiver(this.appReceiver);
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
                    Log.d(TAG, "++++onkey down APP");
//                    LedControl.control_led_status(getResources().getString(R.string.app_led), false);
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
 //               startTvSource();
                return true;
            } else if (i == 139) {
                startMemoryAnimation();
            }
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void displayStatus() {
        this.lv_status.setAdapter((ListAdapter) new LocalAdapter(this,
                this.mStatusLoader.getStatusData(), R.layout.homelist_item,
                new String[]{"item_icon"}, new int[]{R.id.item_type}));
    }

    public void displayDate() {
        TextView textView = (TextView) findViewById(R.id.tx_time);
        TextView textView2 = (TextView) findViewById(R.id.tx_date);
        textView.setText(this.mStatusLoader.getTime());
        textView2.setText(this.mStatusLoader.getDate());
    }

    private void initChildViews() {
        // TODO переделать
        this.lv_status = (GridView) findViewById(R.id.list_status);
        this.lv_status.setFocusable(false);
        this.lv_status.setFocusableInTouchMode(false);
/*        this.lv_status.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return motionEvent.getAction() == 2;
            }
        });*/

        mHoverView = (HoverView) findViewById(R.id.hover_view);
        mHomeView = (ViewGroup) findViewById(R.id.layout_homepage);
        mSecondScreen = (AppLayout) findViewById(R.id.second_screen);
        mHomeShortcutView = (MyGridLayout) findViewById(R.id.gv_shortcut);

        setBigBackgroundDrawable();
        mAction[0] = (MyRelativeLayout) findViewById(R.id.layout_video);
        mAction[1] = (MyRelativeLayout) findViewById(R.id.layout_youtube);
        mAction[2] = (MyRelativeLayout) findViewById(R.id.layout_kodi);
        mAction[3] = (MyRelativeLayout) findViewById(R.id.layout_miracast);
        mAction[4] = (MyRelativeLayout) findViewById(R.id.layout_browser);
        mAction[5] = (MyRelativeLayout) findViewById(R.id.layout_playstore);

        for (int i = 0; i < 6; i++) {
            mAction[i].setType(TYPE_HOME);
            mAction[i].setIntent(getPackageManager().getLaunchIntentForPackage(mAppDataLoader.list_homeShortcut.get(i)));
        }
        Intent intent = new Intent();
        mSettingsView = (MyRelativeLayout) findViewById(R.id.layout_setting);
        mSettingsView.setType(TYPE_SETTINGS);
        intent.setComponent(ComponentName.unflattenFromString("com.android.tv.settings/com.android.tv.settings.MainSettings"));
        mSettingsView.setIntent(intent);
        mFilemanager = (MyRelativeLayout) findViewById(R.id.layout_filemanager);
        mFilemanager.setType(TYPE_FILEMANAGER);
        intent.setComponent(ComponentName.unflattenFromString("com.softwinner.TvdFileManager/.MainUI"));
        mFilemanager.setIntent(intent);
        mAppView = (MyRelativeLayout) findViewById(R.id.layout_app);
        mAppView.setType(TYPE_APPS);
        mAppView.setIntent(null);
        mChildScreens = childScreens;
    }

    private void setBigBackgroundDrawable() {
        ((ImageView) findViewById(R.id.img_video)).setImageDrawable(Drawable.createFromPath(getFilesDir() + "/img_video"));
        ((ImageView) findViewById(R.id.img_youtube)).setImageDrawable(Drawable.createFromPath(getFilesDir() + "/img_youtube"));
        ((ImageView) findViewById(R.id.img_kodi)).setImageDrawable(Drawable.createFromPath(getFilesDir() + "/img_kodi"));
        ((ImageView) findViewById(R.id.img_browser)).setImageDrawable(Drawable.createFromPath(getFilesDir() + "/img_miracast"));
        ((ImageView) findViewById(R.id.img_playstore)).setImageDrawable(Drawable.createFromPath(getFilesDir() + "/img_chrome"));
        ((ImageView) findViewById(R.id.img_miracast)).setImageDrawable(Drawable.createFromPath(getFilesDir() + "/img_google"));
        ((ImageView) findViewById(R.id.img_setting)).setImageDrawable(getResources().getDrawable(R.drawable.img_setting, null));
        ((ImageView) findViewById(R.id.img_filemanager)).setImageDrawable(getResources().getDrawable(R.drawable.img_filemanager, null));
        ((ImageView) findViewById(R.id.img_app)).setImageDrawable(getResources().getDrawable(R.drawable.img_app, null));
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

    public void updateStatus() {
        ((BaseAdapter) this.lv_status.getAdapter()).notifyDataSetChanged();
    }

    public int getCurrentScreenMode() {
        return current_screen_mode;
    }
    public void setShortcutScreen(int mode) {
        resetShortcutScreen(mode);
        current_screen_mode = mode;
    }

    public void resetShortcutScreen(int mode) {
        this.mHandler.removeMessages(MSG_REFRESH_SHORTCUT);
        Log.d(TAG, "resetShortcutScreen mode is " + mode);
        if (this.mAppDataLoader.isDataLoaded()) {
            if (mode == MODE_HOME) {
                this.mHomeShortcutView.setLayoutView(mode, this.mAppDataLoader.getlocalShortCuts());
                return;
            } else {
                this.mSecondScreen.setLayout(mode, this.mAppDataLoader.getappShortCuts());
                return;
            }
        }
        Message message = new Message();
        message.what = MSG_REFRESH_SHORTCUT;
        message.arg1 = mode;
        this.mHandler.sendMessageDelayed(message, 100);
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
        mSecondScreen.setLayoutWithAnim(animType, mode, mAppDataLoader.getlocalShortCuts());
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

    public void updateAppList(Intent intent) {
        String schemeSpecificPart;
        if (intent.getData() == null || !((schemeSpecificPart = intent.getData().getSchemeSpecificPart()) == null || schemeSpecificPart.length() == 0 || schemeSpecificPart.equals("com.android.provision"))) {
            displayShortcuts();
        }
    }

    @Override // android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        Log.d(TAG, "onActivityResult requestCode = " + i + ", resultCode = " + i2);
        if (i == 3 && i2 == -1 && intent != null) {
            if (this.mTvStartPlaying) {
     //           releasePlayingTv();
            }
            try {
                startActivity(intent);
                finish();
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, " can't start LiveTv:" + e);
            }
        }
    }

    public void startCustomScreen(View view) {
        if (this.current_screen_mode == MODE_LOCAL) {
            return;
        }
        this.mHoverView.clear();
        this.saveModeBeforeCustom = current_screen_mode;
        this.mCustomView = new CustomView(this, view, current_screen_mode);
        this.current_screen_mode = MODE_LOCAL;
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        if (rect.top > getResources().getDisplayMetrics().heightPixels / 2) {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            getRootView().addView(this.mCustomView, layoutParams);
        } else {
            getRootView().addView(this.mCustomView);
        }
        getMainView().bringToFront();
    }

    public void recoverFromCustom() {
        this.mHandler.sendEmptyMessage(MSG_RECOVER_HOME);
        getMainView().setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        getMainView().requestFocus();
    }

     public void copyResources(int resId) {

        InputStream in = getResources().openRawResource(resId);
        String filename = getResources().getResourceEntryName(resId);

        File outfile = new File(getFilesDir(), filename);

        if(!outfile.exists()) {
            try {
                OutputStream out = new FileOutputStream(outfile);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                    out.write(buffer, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to copy Resources " + e);
            }
        }
    }

}