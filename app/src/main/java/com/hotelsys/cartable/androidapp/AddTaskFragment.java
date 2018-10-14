package com.hotelsys.cartable.androidapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hotelsys.cartable.androidapp.Adapters.ImageThumbnailsRecyclerAdapter;
import com.hotelsys.cartable.androidapp.Models.Image;
import com.hotelsys.cartable.androidapp.Models.Profile;
import com.hotelsys.cartable.androidapp.Models.Role;
import com.hotelsys.cartable.androidapp.Server.RequestInterface;
import com.hotelsys.cartable.androidapp.Server.RetrofitWithRetry;
import com.hotelsys.cartable.androidapp.Server.ServerRequest;
import com.hotelsys.cartable.androidapp.Server.ServerResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
public class AddTaskFragment extends Fragment {
    @BindView(R.id.title_et)
    EditText titleEt;
    @BindView(R.id.content_et)
    EditText contentEt;
    @BindView(R.id.role_spinner)
    Spinner roleSpinner;
    @BindView(R.id.employee_spinner)
    Spinner employeeSpinner;
    @BindView(R.id.submit_btn)
    Button sendBtn;
    ProgressDialog progress;
    @BindView(R.id.thumbnails)
    RecyclerView thumbnailsRecyclerView;
    private SharedPreferences user_detail;
    private ArrayList<Role> roles = new ArrayList<>();
    private ArrayList<Profile> employees = new ArrayList<>();
    private List<String> employeesSpinnerContnet = new ArrayList<>();
    private boolean title_set,content_set,employee_set;
    private ArrayList<Image> image_thumbnails = new ArrayList<Image>();
    ImageThumbnailsRecyclerAdapter thumbnailsRecyclerAdapter;
    String mCurrentPhotoPath;
    File photoFile = null;
    public AddTaskFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.submit_btn)
    void sendBtnClicked() {
        if (title_set&&content_set&&employee_set) {
            if (employeeSpinner.getSelectedItemPosition() != -1) {
                Profile selected_emplyoee = employees.get(employeeSpinner.getSelectedItemPosition());
                if (selected_emplyoee != null && !titleEt.getText().toString().equals("") && !contentEt.getText().toString().equals(""))
                    sendTask(titleEt.getText().toString(), contentEt.getText().toString(), selected_emplyoee.getId());
            }
        }
    }
    @OnClick(R.id.add_image_btn)

    void addImageBtnClicked()
    {
        final Integer[] ids={1,2,3};
        CharSequence[]items={getString(R.string.from_camera),getString(R.string.from_gallery),getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.add_image_desc));
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
                                if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
                                    // Create the File where the photo should go
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException ex) {
                                        // Error occurred while creating the File
                                    }
                                    // Continue only if the File was successfully created
                                    if (photoFile != null) {
                                        Uri photoURI = FileProvider.getUriForFile(getActivity(),
                                                "com.example.android.fileprovider",
                                                photoFile);
                                        takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                        startActivityForResult(takePicture, 0);
                                    }
                                }
                            }
                            else
                            {
                                requestPermissions( new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);
                            }
                        }
                        else
                        {
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
                                // Create the File where the photo should go
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException ex) {
                                    // Error occurred while creating the File
                                }
                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                                            "com.example.android.fileprovider",
                                            photoFile);
                                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePicture, 0);
                                }
                            }
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);
        ButterKnife.bind(this, view);
        sendBtn.setEnabled(false);
        user_detail = getActivity().getSharedPreferences(Constants.USER_DETAIL, Context.MODE_PRIVATE);
        AppCompatActivity parent = (AppCompatActivity)getActivity();
        parent.getSupportActionBar().setTitle(getString(R.string.add_task));
        getAssignTaskItems();
        roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                employeesSpinnerContnet = new ArrayList<String>();
                for (int i = 0; i < employees.size(); i++) {
                    if (employees.get(i).getRole_id() == roles.get(position).getId()) {
                        employeesSpinnerContnet.add(employees.get(i).getFirstname() + " " + employees.get(i).getLastname());
                    }
                }
                ArrayAdapter<String> employeesSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, employeesSpinnerContnet);
                employeesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                employeeSpinner.setAdapter(employeesSpinnerAdapter);
                if (employeesSpinnerContnet.size() == 0)
                {
                    employee_set = false;
                }
                else
                    employee_set = true;
                checkSubmitCondition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        titleEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (titleEt.getText().toString().equals(""))
                {
                    title_set=false;
                }
                else
                   title_set=true;
                checkSubmitCondition();
            }
        });
        contentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (contentEt.getText().toString().equals(""))
                {
                   content_set = false;
                }
                else
                    content_set = true;
                checkSubmitCondition();
            }
        });
        thumbnailsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        thumbnailsRecyclerView.setLayoutManager(layoutManager);
        thumbnailsRecyclerView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        thumbnailsRecyclerAdapter  = new ImageThumbnailsRecyclerAdapter(getActivity(),image_thumbnails);
        thumbnailsRecyclerView.setAdapter(thumbnailsRecyclerAdapter);
        return view;
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    compressImage(photoFile,photoFile);

                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    compressImage(new File(getRealPathFromURI(selectedImage)),photoFile);
                }
                break;
        }
    }
    private void compressImage(File pictureFile,File outPutFile)
    {
        if (pictureFile == null) {
            Log.i("ERROR", "Error creating media file, check storage permissions:");
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap imageBitmap= BitmapFactory.decodeFile(pictureFile.getAbsolutePath(),bmOptions);
        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),Uri.fromFile(pictureFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            FileOutputStream fos = new FileOutputStream(outPutFile);

            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 65, fos);


            fos.flush();
            fos.close();
            Image thumbnailModel = new Image();
            thumbnailModel.setInteranl_uri(Uri.fromFile(outPutFile));
            thumbnailModel.setInternalBitmap(null);
            image_thumbnails.add(thumbnailModel);
            thumbnailsRecyclerAdapter.notifyDataSetChanged();
        } catch (FileNotFoundException e) {
            Log.i("ERROR", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.i("ERROR", "Error accessing file: " + e.getMessage());
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File mediaStorageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                mediaStorageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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
    private void checkSubmitCondition()
    {
        if (title_set&&content_set&&employee_set)
            sendBtn.setEnabled(true);
        else
            sendBtn.setEnabled(false);
    }
    private void sendTask(String title, final String content, int worker_id) {
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
        List<Image> images = thumbnailsRecyclerAdapter.getData();
        List<MultipartBody.Part> to_upload_image_list = new ArrayList<>();
        for (int i =0;i<images.size();i++)
        {
            File image=new File(getRealPathFromURI(images.get(i).getInteranl_uri()));
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), image);
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("files[]", image.getName(), requestFile);
            to_upload_image_list.add(body);
        }
        RequestBody request_data_title = RequestBody.create(okhttp3.MultipartBody.FORM, title);
        RequestBody request_data_content = RequestBody.create(okhttp3.MultipartBody.FORM, content);
        RequestBody request_data_worker_id = RequestBody.create(okhttp3.MultipartBody.FORM, String.valueOf(worker_id));
        Call<ServerResponse> response = requestInterface.addManualTask(user_detail.getString(Constants.JWT, ""),request_data_title,request_data_content,request_data_worker_id,to_upload_image_list);
        RetrofitWithRetry.enqueueWithRetry(response, 3, new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();

                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            Toast.makeText(getActivity(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                            titleEt.setText("");
                            contentEt.setText("");
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                        break;
                    case 401:
                        Toast.makeText(getActivity(), getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        if (resp != null) {
                            Toast.makeText(getActivity(), resp.getMessage(), Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getActivity(), getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("error:", t.getMessage());
            }
        });
    }

    private void getAssignTaskItems() {
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
        response = requestInterface.getAssignTaskItems(user_detail.getString(Constants.JWT, ""));
        RetrofitWithRetry.enqueueWithRetry(response, 3, new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();
                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            roles = resp.getRoles();
                            employees = resp.getUsers();
                            List<String> roleSpinnerContent = new ArrayList<String>();
                            for (int i = 0; i < roles.size(); i++) {
                                roleSpinnerContent.add(roles.get(i).getName());
                            }
                            ArrayAdapter<String> roleSpinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, roleSpinnerContent);
                            roleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            roleSpinner.setAdapter(roleSpinnerAdapter);
                        }
                        break;
                    default:
                        if (resp != null) {
                            Toast.makeText(getActivity(), resp.getMessage(), Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getActivity(), getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("error:", t.getMessage());
            }
        });
    }

}
