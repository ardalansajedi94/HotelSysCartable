package com.hotelsys.cartable.androidapp;


import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.hotelsys.cartable.androidapp.Models.Permission;
import com.hotelsys.cartable.androidapp.Server.RequestInterface;
import com.hotelsys.cartable.androidapp.Server.RetrofitWithRetry;
import com.hotelsys.cartable.androidapp.Server.ServerResponse;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private ImageLoader imageLoader;
    private SharedPreferences user_detail;
    ProgressDialog progress;
    @BindView(R.id.profile_image) CircleImageView profileImage;
    @BindView(R.id.nameTV) TextView nameTV;
    @BindView(R.id.userNameTV) TextView userNameTV;
    @BindView(R.id.telTV) TextView mobileTV;
    @BindView(R.id.RoleTV) TextView roleTv;
    @BindView(R.id.national_idTV) TextView national_idTV;
    Animation profileImageBlinkingAnimation;
    @OnClick(R.id.profile_image) void profileImageClicked()
    {
        if (user_detail.getInt(Constants.USER_UNDERSTOOD_PROFILE_IMAGE,0)==0)
        {
            SharedPreferences.Editor editor = user_detail.edit();
            editor.putInt(Constants.USER_UNDERSTOOD_PROFILE_IMAGE, 1);
            editor.apply();
            profileImage.clearAnimation();
        }
        final Integer[] ids={1,2,3};
        CharSequence[]items={getString(R.string.from_camera),getString(R.string.from_gallery),getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.change_profile_photo_desc));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id=ids[which];
                switch (id)
                {
                    case 1:
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(takePicture, 0);
                            }
                            else
                            {
                                requestPermissions( new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                            }
                        }
                        else
                        {
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);
                        }


                        break;
                    case 2:
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickPhoto , 1);
                            }
                            else
                            {
                                requestPermissions( new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 133);
                            }
                        }
                        else
                        {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto , 1);
                        }

                        break;
                    case 3:
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            if (requestCode==123)
            {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            }
            else if (requestCode==133)
            {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        }
        else if (grantResults[0]== PackageManager.PERMISSION_DENIED)
        {
            if (requestCode==123)
            {
                Toast.makeText(getActivity(),getString(R.string.write_permission_desc),Toast.LENGTH_SHORT).show();
            }

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this,view);
        user_detail = getActivity().getSharedPreferences(Constants.USER_DETAIL, Context.MODE_PRIVATE);
        imageLoader = ImageLoader.getInstance();
        getProfile();
        if (user_detail.getInt(Constants.USER_UNDERSTOOD_PROFILE_IMAGE,0)==0)
        {
            profileImageBlinkingAnimation = new AlphaAnimation(1, 0);
            profileImageBlinkingAnimation.setDuration(1000);
            profileImageBlinkingAnimation.setInterpolator(new LinearInterpolator());
            profileImageBlinkingAnimation.setRepeatCount(Animation.INFINITE);
            profileImageBlinkingAnimation.setRepeatMode(Animation.REVERSE);
            profileImage.startAnimation(profileImageBlinkingAnimation);
        }
        AppCompatActivity parent = (AppCompatActivity)getActivity();
        parent.getSupportActionBar().setTitle(getString(R.string.profile));
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "HMS");
                    if (! mediaStorageDir.exists()){
                        if (! mediaStorageDir.mkdirs()){

                            Log.d("CustomCameraApp", "failed to create directory");
                        }
                    }
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                    File destination = new File(mediaStorageDir.getPath() + File.separator + System.currentTimeMillis() + ".jpg");
                    FileOutputStream fo;
                    try {
                        destination.createNewFile();
                        fo = new FileOutputStream(destination);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    profileImage.setImageBitmap(thumbnail);
                    change_profile_photo(Uri.fromFile(destination));
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    profileImage.setImageURI(selectedImage);
                    change_profile_photo(selectedImage);
                }
                break;
        }
    }
    private void getProfile() {
        progress = new ProgressDialog(getActivity());
        progress.setMessage(getString(R.string.wait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        Call<ServerResponse> response;
        response = requestInterface.getProfile(user_detail.getString(Constants.JWT, ""));
        RetrofitWithRetry.enqueueWithRetry(response, 3, new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();
                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            imageLoader.displayImage(Constants.MEDIA_BASE_URL + resp.getProfile().getProfile_image(), profileImage);
                            nameTV.setText(getString(R.string.name)+": "+resp.getProfile().getFirstname()+" "+resp.getProfile().getLastname());
                            national_idTV.setText(getString(R.string.national_id)+": "+resp.getProfile().getNational_id());
                            mobileTV.setText(getString(R.string.mobile_no)+": "+resp.getProfile().getMobile());
                            userNameTV.setText(getString(R.string.username)+": "+resp.getProfile().getUsername());
                            roleTv.setText(getString(R.string.role)+": "+resp.getProfile().getRole().getName());
                        }
                        break;

                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progress.dismiss();
                Log.d("error:", t.getMessage());
            }
        });
    }
    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
    private void change_profile_photo(Uri image_uri)
    {
        progress = new ProgressDialog(getActivity());
        progress.setMessage(getString(R.string.wait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();
        File image=new File(getRealPathFromURI(image_uri));
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), image);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("profile_image", image.getName(), requestFile);
        Call<ServerResponse> response = requestInterface.upload_profile_picture(user_detail.getString(Constants.JWT,""),body);
        RetrofitWithRetry.enqueueWithRetry(response,3,new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();
                Log.d("response",String.valueOf(response.code()));
                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            Toast.makeText(getActivity(), getString(R.string.uploaded), Toast.LENGTH_SHORT).show();
                            Intent intent = getActivity().getIntent();
                            getActivity().finish();
                            startActivity(intent);
                        }
                        break;
                    default:
                        if (resp != null) {
                            Toast.makeText(getActivity(), resp.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(getActivity(), getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("error:",t.getMessage());
            }
        });

    }

}
