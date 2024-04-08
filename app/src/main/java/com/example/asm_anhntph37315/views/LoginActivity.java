package com.example.asm_anhntph37315.views;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private static final String BASE_URL = "http://10.0.2.2:3000/auth/";
    private EditText edtUserName,edtPassWord;
    private Button btnLogin;
    private Button tvRegister;
    static  final String TAG = "LOGIN_MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        anhXaView();
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String username, password;
        username = edtUserName.getText().toString().trim();
        password = edtPassWord.getText().toString().trim();
        if(username.equals("")||password.equals("")){
            Toast.makeText(this, "Khong de trong", Toast.LENGTH_SHORT).show();
            return;
        }
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        ApiService service = retrofit.create(ApiService.class);
        Call<KqRes> call = service.login(new User(username,password));
        call.enqueue(new Callback<KqRes>() {
            @Override
            public void onResponse(Call<KqRes> call, Response<KqRes> response) {
                if(response.isSuccessful()){
                    Log.d(TAG, "onResponse: " + response.body().toString());
                    KqRes kqRes = response.body();
                    if(kqRes.getSuccess()){

                        Log.d(TAG,"data: "+ kqRes.getData().toString());
                        Gson gson1 = new Gson();
                        String jsonObjUser = gson1.toJson(kqRes.getData());


                        SharedPreferences sharedPreferences = getSharedPreferences("userLogin", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("user_login",jsonObjUser);
                        editor.apply();


                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    Toast.makeText(LoginActivity.this, kqRes.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<KqRes> call, Throwable throwable) {
                Toast.makeText(LoginActivity.this, "Login That bai: "+throwable.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void anhXaView() {
        edtPassWord = findViewById(R.id.edtPassWordLogin);
        edtUserName = findViewById(R.id.edtUseNameLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }
}