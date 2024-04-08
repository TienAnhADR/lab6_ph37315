package com.example.asm_anhntph37315.views;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
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

import com.example.asm_anhntph37315.MainActivity;
import com.example.asm_anhntph37315.R;
import com.example.asm_anhntph37315.api.ApiService;
import com.example.asm_anhntph37315.model.KqRes;
import com.example.asm_anhntph37315.model.User;
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

public class RegisterActivity extends AppCompatActivity {
    private static final int PICK_REQUEST_CODE = 40;
    private ImageView imgChonAnh;
    private Uri imageUri;
    private EditText edtUserName,edtPass,edtPass2;
    private Button btnRegister;
    private Button tvLogin ;
    static  final String TAG = "REGISTER";
    private static final String BASE_URL = "http://10.0.2.2:3000/auth/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        anhXaView();


        imgChonAnh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
        if (Build.VERSION.SDK_INT >= 30){
            if (!Environment.isExternalStorageManager()){
                Intent getpermission = new Intent();
                getpermission.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(getpermission);
            }
        }
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
    }


    private void anhXaView() {
        btnRegister = findViewById(R.id.btnRegister);
        imgChonAnh = findViewById(R.id.imgChonAnh);
        edtUserName = findViewById(R.id.edtUseNameRegister);
        edtPass = findViewById(R.id.edtPassRegister);
        edtPass2 = findViewById(R.id.edtPass2Register);
        tvLogin = findViewById(R.id.tvLogin);
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
                imgChonAnh.setImageBitmap(bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void register() {
        String username,password,password2;
        username = edtUserName.getText().toString().trim();
        password = edtPass.getText().toString().trim();
        password2 = edtPass2.getText().toString().trim();
        if(username.equals("")|| password.equals("")){
            Toast.makeText(this, "Khong de trong", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(password2)){
            Toast.makeText(this, "Xac thuc mat khau khong giong nhau", Toast.LENGTH_SHORT).show();
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
        RequestBody usernameRequestBody = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody passwordRequestBody = RequestBody.create(MediaType.parse("text/plain"), password);
        Log.d(TAG, "uploadFile: "+ part.body().toString());



        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        ApiService service = retrofit.create(ApiService.class);

        Call<KqRes> objKq = service.register(part,usernameRequestBody,passwordRequestBody);
        objKq.enqueue(new Callback<KqRes>() {
            @Override
            public void onResponse(Call<KqRes> call, Response<KqRes> response) {
                if(response.isSuccessful()){
                    KqRes kq = response.body();
                    if(kq.getSuccess()){

                        Log.d(TAG,"data: "+ kq.getData().toString());
                        Gson gson1 = new Gson();
                        String jsonObjUser = gson1.toJson(kq.getData());
                        SharedPreferences sharedPreferences = getSharedPreferences("userLogin", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_login",jsonObjUser);
                        editor.apply();
                        Toast.makeText(RegisterActivity.this, kq.getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Register that bai do sai dinh dang anh", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(RegisterActivity.this, kq.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KqRes> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
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

}