package com.bytedance.androidcamp.network.demo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bytedance.androidcamp.network.demo.model.Cat;
import com.bytedance.androidcamp.network.demo.newtork.ICatService;
import com.bytedance.androidcamp.network.demo.utils.NetworkUtils;
import com.bytedance.androidcamp.network.lib.util.ImageHelper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static String CAT_JSON =
            "{\"breeds\":[],\"id\":\"293\",\"url\":\"https://cdn2.thecatapi.com/images/293.jpg\",\"width\":429,\"height\":500}";

    private Retrofit retrofit;
    private ICatService catService;
    private Gson gson;

    public TextView tvOut;
    public ImageView ivOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvOut = findViewById(R.id.tv_out);
        ivOut = findViewById(R.id.iv_out);
    }

    public void testJSONObject(View view) {
        // TODO 1: Parse CAT_JSON using JSONObject
        String id = "";
        try {
            JSONObject cat = new JSONObject(CAT_JSON);
            id = cat.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tvOut.setText(id);
    }

    public void testGson(View view) {
        // TODO 2: Parse CAT_JSON using Gson
        String url = "";
        Cat cat = getGson().fromJson(CAT_JSON, Cat.class);
        url = cat.getUrl();
        //Log.d("GSON", url);
        ImageHelper.displayWebImage(url, ivOut);
    }

    public void testHttpURLConnectionSync(View view) {
        // TODO 4: Fix crash of NetworkOnMainThreadException
        new Thread(){
            @Override
            public void run() {
                final String s = NetworkUtils.getResponseWithHttpURLConnection(ICatService.HOST + ICatService.PATH);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvOut.setText(s);
                    }
                });
            }
        }.start();

    }

    public void testRetrofitSync(View view) throws Exception {
        // TODO 5: Making request in retrofit
        Cat cat = null;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ICatService.HOST)
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build();
        ICatService iCatService = retrofit.create(ICatService.class);
        Response<List<Cat>> response = iCatService.randomCat(1).execute();
        List<Cat> body = response == null ? null : response.body();
        if (body != null) {
            cat = body.get(0);
            tvOut.setText(cat.toString());
        }
    }

    public void testUpdateUI(View view) {
        // TODO 6: Fix crash of CalledFromWrongThreadException
        new Thread() {
            @Override
            public void run() {
                final String s = NetworkUtils.getResponseWithHttpURLConnection(ICatService.HOST + ICatService.PATH);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvOut.setText(s);
                    }
                });
            }
        }.start();
    }

    public void testHttpURLConnectionAsync(View view) {
        // HttpURLConnection Async
        new Thread() {
            @Override
            public void run() {
                final String s = NetworkUtils.getResponseWithHttpURLConnection(ICatService.HOST + ICatService.PATH);
                try {
                    JSONArray cats = new JSONArray(s);
                    JSONObject cat = cats.getJSONObject(0);
                    final String id = cat.getString("id");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvOut.setText(id);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "retrofit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }.start();
    }

    public void testRetrofitAsync(View view) {
        Call<List<Cat>> call = getCatService().randomCat(1);
        call.enqueue(new Callback<List<Cat>>() {
            @Override
            public void onResponse(Call<List<Cat>> call, Response<List<Cat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cat> cats = response.body();
                    Cat cat = cats.get(0);
                    tvOut.setText(cat.getUrl());
                }
            }

            @Override
            public void onFailure(Call<List<Cat>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "retrofit: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    private ICatService getCatService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ICatService.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        if (catService == null) {
            catService = retrofit.create(ICatService.class);
        }
        return catService;
    }
}
