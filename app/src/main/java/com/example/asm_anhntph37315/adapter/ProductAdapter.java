package com.example.asm_anhntph37315.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.asm_anhntph37315.R;
import com.example.asm_anhntph37315.api.ApiService;
import com.example.asm_anhntph37315.model.KqResProduct;
import com.example.asm_anhntph37315.model.Product;
import com.example.asm_anhntph37315.views.UpdateAndAdd_Product_Activity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{
    private ArrayList<Product> list;

    private Context context;
    static  final String TAG = "ADAPTER";
    private static final String BASE_URL = "http://10.0.2.2:3000/";
    private ApiService service;

    public ProductAdapter(ArrayList<Product> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product pro = list.get(position);
        holder.tvPricePro.setText("Price: "+pro.getPricePro());
        holder.tvNamePro.setText("Name: "+pro.getNamePro());
        Glide.with(context).load(pro.getImage()).into(holder.imgPro);
        retrolfit();
        holder.btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePro(pro.get_id());
            }
        });

        holder.btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"Vao day click");
                Gson gson1 = new Gson();
                String jsoObjPro = gson1.toJson(pro);
                SharedPreferences sharedPreferences = context.getSharedPreferences("product", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("product",jsoObjPro);
                editor.putInt("context",1);
                editor.apply();
                Intent intent = new Intent(context, UpdateAndAdd_Product_Activity.class);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamePro,tvPricePro;
        ImageView imgPro;
        Button btnXoa,btnSua;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamePro = itemView.findViewById(R.id.tvNamePro);
            tvPricePro = itemView.findViewById(R.id.tvPricePro);
            btnSua = itemView.findViewById(R.id.btnSua);
            btnXoa = itemView.findViewById(R.id.btnXoa);
            imgPro = itemView.findViewById(R.id.imgProduct);
        }
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
    private void deletePro(String _id){
        Call<KqResProduct> call = service.deleteProduct(_id);
        call.enqueue(new Callback<KqResProduct>() {
            @Override
            public void onResponse(Call<KqResProduct> call, Response<KqResProduct> response) {
                if(response.isSuccessful()){
                    Log.d(TAG,"OnResponse"+response.body().toString());
                    KqResProduct kq = response.body();
                    if(kq.isSuccess()){
                        list.clear();
                        list.addAll(kq.getData());
                        notifyDataSetChanged();
                    }
                    Toast.makeText(context, kq.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KqResProduct> call, Throwable throwable) {
                throwable.printStackTrace();

            }
        });


    }




}
