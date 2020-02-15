package com.weather.android.json;

import android.text.TextUtils;


import com.weather.android.table.City;
import com.weather.android.table.Country;
import com.weather.android.table.Forecast;
import com.weather.android.table.Pronvince;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Json {
    private static String cityName;
    private static String cityId;
    private static String carWash;
    private static String comfort;
    private static String loc;
    private static String aqi;
    private static String pm25;
    private static String tem;
    private static String info;
    private static String sport;
    private static String dir;
    private static String sc;
    private static String qlty;
    public static List<Forecast> forecastList = new ArrayList<>();


    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Pronvince pronvince = new Pronvince();
                    pronvince.setProvinceName(provinceObject.getString("name"));
                    pronvince.setProvinceCode(provinceObject.getInt("id"));
                    pronvince.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i=0;i<allCities.length();i++){
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCountyResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i=0;i<allCounties.length();i++){
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    Country country = new Country();
                    country.setCountryName(countyObject.getString("name"));
                    country.setWeatherId(countyObject.getString("weather_id"));
                    country.setCityId(cityId);
                    country.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleWeatherResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                cityName = null;
                cityId = null;
                loc = null;
                aqi = null;
                pm25 = null;
                tem = null;
                info = null;
                carWash = null;
                comfort = null;
                sport = null;
                dir = null;
                sc = null;
                JSONObject jsonObject = new JSONObject(response);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                JSONObject basic = jsonObject1.getJSONObject("basic");
                JSONObject update = basic.getJSONObject("update");
                JSONObject aqiobject = jsonObject1.getJSONObject("aqi");
                JSONObject city = aqiobject.getJSONObject("city");
                JSONObject now = jsonObject1.getJSONObject("now");
                JSONObject cond = now.getJSONObject("cond");
                JSONObject suggestion = jsonObject1.getJSONObject("suggestion");
                JSONObject comf = suggestion.getJSONObject("comf");
                JSONObject sportobject = suggestion.getJSONObject("sport");
                JSONObject cw = suggestion.getJSONObject("cw");
                JSONArray daily_forecast = jsonObject1.getJSONArray("daily_forecast");
                cityName = basic.getString("city");
                cityId = basic.getString("id");
                loc = update.getString("loc");
                aqi = city.getString("aqi");
                pm25 = city.getString("pm25");
                qlty = city.getString("qlty");
                tem = now.getString("tmp");
                dir = now.getString("wind_dir");
                sc = now.getString("wind_sc");
                info = cond.getString("txt");
                carWash = cw.getString("txt");
                comfort = comf.getString("txt");
                sport = sportobject.getString("txt");
                if (forecastList!=null) {
                    forecastList.clear();
                    for (int i = 0; i < daily_forecast.length(); i++) {
                        JSONObject forecast = daily_forecast.getJSONObject(i);
                        JSONObject cond1 = forecast.getJSONObject("cond");
                        JSONObject tmp = forecast.getJSONObject("tmp");
                        Forecast forecast1 = new Forecast();
                        forecast1.setDate(forecast.getString("date"));
                        forecast1.setInfo(cond1.getString("txt_d"));
                        forecast1.setMaxtem(tmp.getString("max"));
                        forecast1.setMintem(tmp.getString("min"));
                        forecastList.add(forecast1);
                    }
                }
                return true;
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static String getCityName() {
        return cityName;
    }

    public static String getCityId() {
        return cityId;
    }

    public static String getCarWash() {
        return carWash;
    }

    public static String getComfort() {
        return comfort;
    }

    public static String getLoc() {
        return loc;
    }

    public static String getAqi() {
        return aqi;
    }

    public static String getPm25() {
        return pm25;
    }

    public static String getTem() {
        return tem;
    }

    public static String getInfo() {
        return info;
    }

    public static String getSport() {
        return sport;
    }

    public static String getDir() {
        return dir;
    }

    public static String getSc() {
        return sc;
    }

    public static String getQlty() {
        return qlty;
    }
}
