package com.weather.android.json;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Url {


    public static String get(final String url){
        final StringBuilder stringBuilder = new StringBuilder();
        FutureTask<String> task = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() {
                BufferedReader bufferedReader = null;
                InputStreamReader inputStreamReader = null;
                URLConnection urlConnection;
                try {
                    URL url1 = new URL(url);
                    urlConnection = url1.openConnection();
                    urlConnection.connect();
                    inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line ;
                    while ((line=bufferedReader.readLine())!=null){
                        stringBuilder.append(line);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if (bufferedReader!=null){
                        try {
                            bufferedReader.close();
                            inputStreamReader.close();
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }
                return stringBuilder.toString();
            }
        });
        new Thread(task).start();
        String s = null;
        try {
            s = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return s;
    }

}
