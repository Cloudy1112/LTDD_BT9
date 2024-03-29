package com.example.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddFile extends AppCompatActivity {

    Button btnChoose, btnUpload;
    ImageView imageViewChoose, imageViewUpload;
    EditText editTextUserName;
    TextView textViewUsername;
    private Uri mUri;
    private ProgressDialog mProgressDialog;
    public static final int MY_REQUEST_CODE=100;
    public static final String TAG = MainActivity.class.getName();

    private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){
                @Override
                public void onActivityResult(ActivityResult result){
                    Log.e(TAG,"onActivityResult");
                    if (result.getResultCode()== Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data == null){
                            return;
                        }
                        Uri uri = data.getData();
                        mUri = uri;

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                            imageViewChoose.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);
        Anhxa();
        mProgressDialog = new ProgressDialog(AddFile.this);
        mProgressDialog.setMessage("Please wait upload....");
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckPermission();
            }

        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUri != null){
                    UploadImage1();
                }
            }
        });
    }

    public void UploadImage1(){
        mProgressDialog.show();
        String username = editTextUserName.getText().toString().trim();
        RequestBody requestUsername = RequestBody.create(MediaType.parse("multipart/form-data"), username);
        String IMAGE_PATH = RealPathUtil.getRealPath(AddFile.this, mUri);
        Log.e ("ffff", IMAGE_PATH);
        File file = new File(IMAGE_PATH);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);

        MultipartBody.Part partbodyavatar =
                MultipartBody.Part.createFormData(Const.MY_IMAGES, file.getName(), requestFile);

        ImageUpload.ServiceAPI.serviceapi.upload(requestUsername,partbodyavatar).enqueue(new Callback<List<ImageUpload>>() {
            @Override
            public void onResponse(Call<List<ImageUpload>> call, Response<List<ImageUpload>> response) {
                mProgressDialog.dismiss();
                List<ImageUpload> imageUpload = response.body();
                if (imageUpload.size() >8){
                    for (int i =0; 1<imageUpload.size();i++){
                        textViewUsername.setText(imageUpload.get(i).getUsername());
                        Glide.with(AddFile.this)
                                .load(imageUpload.get(i).getAvatar())
                                .into(imageViewUpload);
                        Toast.makeText(AddFile.this, "Thành công", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddFile.this, "Thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ImageUpload>> call, Throwable t) {
                mProgressDialog.dismiss();
                Log.e("TAG",t.toString());
                Toast.makeText(AddFile.this, "Gọi API thất bại", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void Anhxa(){
        btnChoose = findViewById(R.id.Choosefile);
        btnUpload = findViewById(R.id.Uploadfile);
        imageViewUpload = findViewById(R.id.imageMulti);
        editTextUserName = findViewById(R.id.editUsername);
        textViewUsername = findViewById(R.id.tvUsername);
        imageViewChoose = findViewById(R.id.imageChoose);

    }

    public static String[] storage_permissions = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storage_permissions_33 = {
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_VIDEO,
    };

    public static String[] permissions(){
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            p = storage_permissions_33;
        } else {
            p = storage_permissions;
        }
        return p;
    }
    private void CheckPermission(){
        if (Build.VERSION.SDK_INT< Build.VERSION_CODES.M){
            openGallery();
            return;
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            openGallery();
        }else{
            requestPermissions(permissions(),MY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }

    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Select Picture"));
    }

}