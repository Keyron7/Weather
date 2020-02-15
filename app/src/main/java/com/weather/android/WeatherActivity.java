package com.weather.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.weather.android.json.Json;
import com.weather.android.json.Url;
import com.weather.android.table.Forecast;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private TextView dirText;
    private TextView scText;
    private TextView qltyText;
    private ImageView bingImg;
    private LinearLayout forecastLayout;
    public SwipeRefreshLayout swipeRefreshLayout;
    public DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        dirText = (TextView) findViewById(R.id.win_dir);
        scText = (TextView) findViewById(R.id.win_sc);
        bingImg = (ImageView) findViewById(R.id.bing_pic_img);
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        forecastLayout = (LinearLayout)findViewById(R.id.forecasrt_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfore_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        qltyText = (TextView) findViewById(R.id.qlty);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        Button navButton = (Button) findViewById(R.id.nav_button);
        final String weatherId = getIntent().getStringExtra("weather_id");
        weatherLayout.setVisibility(View.INVISIBLE);
        requestWeather(weatherId);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(Json.getCityId());
            }
        });
            loadPic();
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public void requestWeather(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=d186ff51ed1b4544be0d54431d8cb709";
        final String responseText = Url.get(weatherUrl);
        Json.handleWeatherResponse(responseText);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Json.handleWeatherResponse(responseText)){
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                    editor.putString("weather",responseText);
                    editor.apply();
                    showWeatherInfo();
                }else if (!Json.handleWeatherResponse(responseText)){
                    Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        loadPic();
    }
    private void loadPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        final String bingpic = Url.get(requestBingPic);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
        editor.putString("bing_pic", bingpic);
        editor.apply();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Glide.with(WeatherActivity.this).load(bingpic).into(bingImg);
            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void showWeatherInfo(){
        String cityName = Json.getCityName();
        String updateTime = Json.getLoc();
        String degree = Json.getTem() + "℃";
        String weatherInfo = Json.getInfo();
        String dir = Json.getDir();
        String sc = Json.getSc();
        String qlty = Json.getQlty();
        qltyText.setText(qlty);
        dirText.setText(dir);
        scText.setText(sc+"级");
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : Json.forecastList){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.getDate());
            infoText.setText(forecast.getInfo());
            maxText.setText(forecast.getMaxtem()+"℃");
            minText.setText(forecast.getMintem()+"℃"+"/");
            forecastLayout.addView(view);
        }
        if (Json.getAqi() != null){
            aqiText.setText(Json.getAqi());
            pm25Text.setText(Json.getPm25());
        }
        String comfort = "舒适度:"+ Json.getComfort();
        String carWash = "洗车指数:"+ Json.getCarWash();
        String sport = "运动建议:" + Json.getSport();
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}