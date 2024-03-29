package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static androidx.core.app.ActivityCompat.requestPermissions;
import static androidx.core.content.PermissionChecker.checkSelfPermission;

import static com.example.myapplication.MainActivity.MY_REQUEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public class ImageUpload {
    private int id;
    private String username;
    private String avatar;

    public ImageUpload(int id, String username, String avatar) {
        this.id = id;
        this.username = username;
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public interface ServiceAPI {
        public static final String BASE_URL = "http://app.iostar.vn/appfoods/";
        Gson gson = new GsonBuilder().setDateFormat("yyyy MM dd HH:mm:ss").create();

        ServiceAPI serviceapi = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ServiceAPI.class);

        @Multipart
        @POST("upload.php")
        Call<List<ImageUpload>> upload (@Part(Const.MY_USERNAME)RequestBody username,
                                        @Part MultipartBody.Part avatar);

        @Multipart
        @POST("upload1.php")
        Call<Message> upload1(@Part (Const.MY_USERNAME)RequestBody username,
                              @Part MultipartBody.Part avatar);
    }



}

