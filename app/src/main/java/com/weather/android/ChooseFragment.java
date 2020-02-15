package com.weather.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.weather.android.json.Json;
import com.weather.android.json.Url;
import com.weather.android.table.City;
import com.weather.android.table.Country;
import com.weather.android.table.Pronvince;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ChooseFragment extends Fragment {
    private static final int LEVEL_PROVINCE=0;
    private static final int LEVEL_CITY=1;
    private static final int LEVEL_COUNTY=2;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private RecyclerView recyclerView;
    private Adapter adapter;

    private ArrayList<String> dataList = new ArrayList<>();

    private int currentLevel;
    private Pronvince selectedProvince;
    private City selectedCity;

    private List<Pronvince> provinceList;
    private List<City> cityList;
    private List<Country> countryList ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose,container,false);
        titleText = (TextView)view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        adapter = new Adapter(getContext(),dataList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                    if (currentLevel == LEVEL_PROVINCE){
                        selectedProvince = provinceList.get(position);
                        queryCities();
                    }
                    else if (currentLevel == LEVEL_CITY){
                        selectedCity = cityList.get(position);
                        queryCounties();
                    }else if (currentLevel == LEVEL_COUNTY){
                        String weatherId = countryList.get(position).getWeatherId();
                        if (getActivity() instanceof MainActivity){
                            Intent intent = new Intent(getActivity(),WeatherActivity.class);
                            intent.putExtra("weather_id",weatherId);
                            startActivity(intent);
                            getActivity().finish();
                        }else if (getActivity()instanceof   WeatherActivity){
                            WeatherActivity activity = (WeatherActivity) getActivity();
                            activity.drawerLayout.closeDrawers();
                            activity.swipeRefreshLayout .setRefreshing(true);
                            activity.requestWeather(weatherId);
                        }
                    }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCities();
                }
                else if (currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }
    private void queryCities() {
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceid=?",String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }
    private void queryFromServer(String address, final String type){
        showProgressDialog();
        String response = Url.get(address);
        if (response!=null){
            boolean result = false;
            if ("province".equals(type)){
                result = Json.handleProvinceResponse(response);
            }
            else if ("city".equals(type)){
                result = Json.handleCityResponse(response,selectedProvince.getId());
            }
            else if ("county".equals(type)){
                result = Json.handleCountyResponse(response, selectedCity.getId());
            }
            if (result)
            {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        switch (type) {
                            case "province":
                                queryProvinces();
                                break;
                            case "city":
                                queryCities();
                                break;
                            case "county":
                                queryCounties();
                                break;
                        }
                    }
                });
            }

            if ("city".equals(type)){
                result = Json.handleProvinceResponse(response);
            }
            if ("county".equals(type)){
                result = Json.handleProvinceResponse(response);
            }
        }
    }
    private void showProgressDialog() {
        if (progressDialog==null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);

        }
        progressDialog.show();
    }
    private void queryCounties() {
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countryList = LitePal.where("cityid = ?", String.valueOf(selectedCity.getId())).find(Country.class);
        if (countryList.size()>0){
            dataList.clear();
            for (Country country:countryList){
                dataList.add(country.getCountryName());
            }
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            currentLevel=LEVEL_COUNTY;
        }
        else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }
    private void queryProvinces() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Pronvince.class);
        if (provinceList.size()>0){
            dataList.clear();
            for (Pronvince province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            currentLevel = LEVEL_PROVINCE;
        }
        else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }
    private void closeProgressDialog() {
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    }
