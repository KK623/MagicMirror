package com.example.administrator.magicmirror;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.magicmirror.gson.Weather;
import com.example.administrator.magicmirror.util.HttpUtil;
import com.example.administrator.magicmirror.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends ActionBarActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView dressingText;
    private TextView uvText;
    private TextView fluText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化控件
        weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
        titleCity = (TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView)findViewById(R.id.pm25_text);
        dressingText = (TextView)findViewById(R.id.dressing_text);
        uvText = (TextView)findViewById(R.id.uv_text);
        fluText = (TextView)findViewById(R.id.flu_text);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if (weatherString != null){
            //有缓存
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }
        else {
            //无缓存
            String weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }
    //根据weatherId请求城市天气信息
    public void requestWeather(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid=CN101190104&key=7821b2513b8a48c8abab236f7fab58fa";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }
                        else {
                            Toast.makeText(MainActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
    //展示Weather类数据
    private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = "上次更新时间" + weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        if (weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String dressing = weather.suggestion.dressing.info;
        String flu = weather.suggestion.flu.info;
        String uv = weather.suggestion.uv.info;
        dressingText.setText(dressing);
        fluText.setText(flu);
        uvText.setText(uv);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
