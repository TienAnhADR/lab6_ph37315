package com.example.asm_anhntph37315.api;

import com.example.asm_anhntph37315.model.KqRes;
import com.example.asm_anhntph37315.model.KqResProduct;
import com.example.asm_anhntph37315.model.User;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    @Multipart
    @POST("register")
    Call<KqRes> register(@Part MultipartBody.Part file,
                         @Part("username") RequestBody username,
                         @Part("password") RequestBody password);

    @POST("login")
    Call<KqRes> login(@Body User user);
    @Multipart
    @POST("product")
    Call<KqResProduct> addProduct (@Part MultipartBody.Part file,
                                   @Part("namePro") RequestBody namePro,
                                   @Part("pricePro") RequestBody pricePro);
    @GET("product")
    Call<KqResProduct> getListProduct();


    @DELETE("product/{id}")
    Call<KqResProduct> deleteProduct(@Path("id") String _id);

    @Multipart
    @PUT("product/{id}")
    Call<KqResProduct> updateProduct (@Path("id") String _id,
                                      @Part MultipartBody.Part file,
                                      @Part("namePro") RequestBody pricePro,
                                      @Part("pricePro") RequestBody namePro);


}
