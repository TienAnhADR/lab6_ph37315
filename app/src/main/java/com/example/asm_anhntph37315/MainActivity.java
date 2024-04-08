package com.example.asm_anhntph37315;

import static androidx.constraintlayout.widget.StateSet.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.asm_anhntph37315.adapter.ProductAdapter;
import com.example.asm_anhntph37315.api.ApiService;
import com.example.asm_anhntph37315.model.KqResProduct;
import com.example.asm_anhntph37315.model.Product;
import com.example.asm_anhntph37315.model.User;
import com.example.asm_anhntph37315.views.UpdateAndAdd_Product_Activity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    static  final String TAG = "Main";
    ArrayList<Product> list = new ArrayList<>();
    private ApiService service;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private static final String BASE_URL = "http://10.0.2.2:3000/";
    private FloatingActionButton btnadd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.rcvListSP);
        btnadd = findViewById(R.id.btnAdd);
        retrolfit();
        getListPro();
        btnadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UpdateAndAdd_Product_Activity.class));
            }
        });

    }

    private void loadData(ArrayList<Product> list) {
        Log.e(TAG,"Vao LoadData");
        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(manager);
        adapter = new ProductAdapter(list,MainActivity.this);
        recyclerView.setAdapter(adapter);

    }

    private void retrolfit(){
        Log.e(TAG,"vao creat retrofit");
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        service = retrofit.create(ApiService.class);
    }

    private void getListPro(){
        Log.e(TAG,"Vao getList");
        Call<KqResProduct> call = service.getListProduct();
        call.enqueue(new Callback<KqResProduct>() {
            @Override
            public void onResponse(Call<KqResProduct> call, Response<KqResProduct> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.body().toString());
                    KqResProduct kq = response.body();
                    if(kq.isSuccess()){

                        Log.d(TAG,kq.getData().toString());
                        list.addAll(kq.getData());

                        loadData(list);
                    }
                    Toast.makeText(MainActivity.this, kq.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KqResProduct> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });

    }
}