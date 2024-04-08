package com.example.asm_anhntph37315.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.asm_anhntph37315.MainActivity;
import com.example.asm_anhntph37315.R;
import com.example.asm_anhntph37315.api.ApiService;
import com.example.asm_anhntph37315.model.KqResProduct;
import com.example.asm_anhntph37315.model.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateAndAdd_Product_Activity extends AppCompatActivity {
    private TextView tvTitle;
    private EditText edtNamePro;
    private EditText edtPrice;
    private Button btnBack;
     private Button btnUpdate;
    private ImageView imgAnh;
    private static final int PICK_REQUEST_CODE = 40;
    private Uri imageUri;
    static  final String TAG = "UPLOAD";
    private static final String BASE_URL = "http://10.0.2.2:3000/";
    SharedPreferences sharedPreferences;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("DESTROY", "Vao");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_and_add_product);
        tvTitle = findViewById(R.id.tvTitleDiaLog);
        edtNamePro = findViewById(R.id.edtNamePro);
        edtPrice = findViewById(R.id.edtPrice);
        btnBack = findViewById(R.id.btnBack);
        btnUpdate =findViewById(R.id.btnAddOrUpdate);
        imgAnh = findViewById(R.id.imgChonImg);
        sharedPreferences = getSharedPreferences("product", Context.MODE_PRIVATE);
        String jsonProduct = sharedPreferences.getString("product", "");
        int context = sharedPreferences.getInt("context",0);
        Gson gson = new Gson();

        Product product = gson.fromJson(jsonProduct, Product.class);
        if(context==1){
            tvTitle.setText("Update");
            edtPrice.setText(product.getPricePro()+"");
            edtNamePro.setText(product.getNamePro());
            btnUpdate.setText("Update");
            Glide.with(this).load(product.getImage()).into(imgAnh);
        }

        imgAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context==1){
                    Log.e("Vao Update","ham");
                    updateProduct(product.get_id());
                }else {
                    Log.e("Vao ADD","ham");
                    addProduct();
                }
                
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateAndAdd_Product_Activity.this,MainActivity.class));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Stop", "Vao");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void addProduct() {
        String namePro = edtNamePro.getText().toString().trim();
        int pricePro = Integer.parseInt(edtPrice.getText().toString().trim());
        Log.e("pricePro:",pricePro+"");
        if(namePro.equals("")){
            Toast.makeText(this, "Vui long nhap du thong tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pricePro<= 0){
            Toast.makeText(this, "Gia san pham phai lon hon 0", Toast.LENGTH_SHORT).show();
            return;
        }
        if(imageUri == null){
            Log.d(TAG,"uploadFile: URI null");
            Toast.makeText(this, "Ban chua chon Avatar", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(getRealPathFromURI(imageUri));
        Log.d(TAG,"Upload File : "+ file.toString());
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),file);
        // chu y : cai nay MediaType.parse("image/*") phai de la image/* thi tren server moi nhan dien duoc la file anh

        MultipartBody.Part part = MultipartBody.Part.createFormData("image",file.getName(),requestBody);
        RequestBody numberRequestBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(pricePro));
        RequestBody priceRequest = RequestBody.create(MediaType.parse("text/plain"),""+pricePro);
        Log.d("price",priceRequest.toString());
        RequestBody nameRequest = RequestBody.create(MediaType.parse("text/plain"), namePro);
        Log.d(TAG, "uploadFile: "+ part.body().toString());



        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        ApiService service = retrofit.create(ApiService.class);
        Call<KqResProduct> call = service.addProduct(part,nameRequest,priceRequest);
        call.enqueue(new Callback<KqResProduct>() {
            @Override
            public void onResponse(Call<KqResProduct> call, Response<KqResProduct> response) {
                if(response.isSuccessful()){
                    KqResProduct kq = response.body();
                    if(kq.isSuccess()){
                        startActivity(new Intent(UpdateAndAdd_Product_Activity.this, MainActivity.class));
                    }
                    Toast.makeText(UpdateAndAdd_Product_Activity.this, kq.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KqResProduct> call, Throwable throwable) {

            }
        });
    }

    private void pickImage() {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent,PICK_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            Log.d(TAG, "onActivityResult: " + imageUri);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                imgAnh.setImageBitmap(bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private String getRealPathFromURI(Uri imageUri) {
        String [] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(imageUri,null,null,null,null);
        Log.d(TAG, "getRealPathFromURI: "  + cursor.getCount());
        if(cursor != null){
            cursor.moveToFirst();
            for (int i = 0; i<cursor.getColumnCount();i++){
                Log.d(TAG, "getRealPathFromURI: " + cursor.getColumnName(i) + "==>" + cursor.getString(i));
            }
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        else {
            return null;
        }
    }
    private void updateProduct(String id){
        String namePro = edtNamePro.getText().toString().trim();
        int pricePro = Integer.parseInt(edtPrice.getText().toString().trim());
        Log.e("pricePro:",pricePro+"");
        if(namePro.equals("")){
            Toast.makeText(this, "Vui long nhap du thong tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if(pricePro<= 0){
            Toast.makeText(this, "Gia san pham phai lon hon 0", Toast.LENGTH_SHORT).show();
            return;
        }
        if(imageUri == null){
            Log.d(TAG,"uploadFile: URI null");
            Toast.makeText(this, "Ban chua chon Avatar", Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(getRealPathFromURI(imageUri));
        Log.d(TAG,"Upload File : "+ file.toString());
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),file);
        // chu y : cai nay MediaType.parse("image/*") phai de la image/* thi tren server moi nhan dien duoc la file anh

        MultipartBody.Part part = MultipartBody.Part.createFormData("image",file.getName(),requestBody);
        RequestBody numberRequestBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(pricePro));
        RequestBody priceRequest = RequestBody.create(MediaType.parse("text/plain"),""+pricePro);
        Log.d("price",priceRequest.toString());
        RequestBody nameRequest = RequestBody.create(MediaType.parse("text/plain"), namePro);
        Log.d(TAG, "uploadFile: "+ part.body().toString());



        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        ApiService service = retrofit.create(ApiService.class);
        CallUpDate(service, id,part,nameRequest,priceRequest);
    }

    private void CallUpDate(ApiService service,String id ,MultipartBody.Part part, RequestBody nameRequest, RequestBody priceRequest) {
        Call<KqResProduct> call = service.updateProduct(id,part,nameRequest,priceRequest);
        call.enqueue(new Callback<KqResProduct>() {
            @Override
            public void onResponse(Call<KqResProduct> call, Response<KqResProduct> response) {

                if(response.isSuccessful()){

                    KqResProduct kq = response.body();
                    Log.e("KetQua: ",kq.toString());
                    if(kq.isSuccess()){

                        startActivity(new Intent(UpdateAndAdd_Product_Activity.this, MainActivity.class));
                    }
                    Toast.makeText(UpdateAndAdd_Product_Activity.this, kq.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KqResProduct> call, Throwable throwable) {
                Log.e("Throw: ",throwable.toString());
            }
        });
    }
}