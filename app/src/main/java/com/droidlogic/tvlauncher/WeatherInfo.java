package com.droidlogic.tvlauncher;


import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.NumberFormat;
import java.util.Locale;

public class WeatherInfo {
    private final static String TAG = "WeatherInfo";
    private Context mContext;
    private String urlCity;
    private RequestQueue mQueue;
    private long LastWeatherInfo;

    public WeatherInfo(Context context, String City, String Metric, String AppId) {

        mContext = context;
        LastWeatherInfo = 0;
        String lang = Locale.getDefault().getLanguage();
        urlCity = "https://api.openweathermap.org/data/2.5/weather?q=" + City +
                "&units=" + Metric + "&lang=" + lang + "&appid=" + AppId;
        mQueue = Volley.newRequestQueue(mContext);
    }

    public void getData() {
        this.mQueue.add(new JsonObjectRequest(Request.Method.GET, this.urlCity, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jSONObject) {
                NumberFormat localNumberFormat = NumberFormat.getInstance();
                localNumberFormat.setMaximumFractionDigits(1);
                try {
                    String str2 = jSONObject.getString("name").toString();
                    String string = jSONObject.getJSONObject("main").getString("temp").replaceAll("(\\.\\d*)", "");
                    String string1 = jSONObject.getJSONArray("weather").getJSONObject(0).getString("description");
                    String string2 = jSONObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                    Launcher.tx_city.setText(str2);
                    Launcher.tx_temp.setText("" + string + mContext.getResources().getString(R.string.str_temp));
                    Launcher.tx_condition.setText(string1);
                    Launcher.img_weather.setImageResource(parseIcon(string2));
                    Log.d(TAG, "city=" + str2 + ";temp=" + string + ";condition=" + string1 + ";icon=" + string2);
                    LastWeatherInfo = SystemClock.elapsedRealtime();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        }));
    }

    private int parseIcon(String str) {
        if (str == null) {
            return -1;
        }
        return ("01d".equals(str) ? R.drawable.ya_clear_d : ("01n".equals(str) ? R.drawable.ya_clear_n :
                ("02d".equals(str) ? R.drawable.ya_clouds1_d : ("02n".equals(str) ? R.drawable.ya_clear_n :
                        ("03n".equals(str) || "03d".equals(str) ? R.drawable.ya_clouds2 :
                                ("04n".equals(str) || "04d".equals(str) ? R.drawable.ya_clouds3 :
                                        ("09n".equals(str) || "09d".equals(str) ? R.drawable.ya_heavy_rain :
                                                ("10d".equals(str) ? R.drawable.ya_rain_d : ("10n".equals(str) ? R.drawable.ya_rain_n :
                                                        ("11n".equals(str) || "11d".equals(str) ? R.drawable.ya_thunderstorm :
                                                                ("13n".equals(str) || "13d".equals(str) ? R.drawable.ya_snow : R.drawable.ya_mist)))))))))));
    }

    public long getLastWeatherInfo() {
        return LastWeatherInfo;
    }
}
