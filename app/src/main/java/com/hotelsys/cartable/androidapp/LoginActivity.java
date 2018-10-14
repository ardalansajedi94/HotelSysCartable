package com.hotelsys.cartable.androidapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hotelsys.cartable.androidapp.Models.Permission;
import com.hotelsys.cartable.androidapp.Server.RequestInterface;
import com.hotelsys.cartable.androidapp.Server.RetrofitWithRetry;
import com.hotelsys.cartable.androidapp.Server.ServerRequest;
import com.hotelsys.cartable.androidapp.Server.ServerResponse;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;
//import com.onesignal.OSPermissionSubscriptionState;
//import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private boolean usernameSet,passwordSet;
    private SharedPreferences user_detail;
    ProgressDialog progress;
    @BindView(R.id.user_name_TIL) TextInputLayout usernameTIL;
    @BindView(R.id.user_name_ET) EditText usernameEt;
    @BindView(R.id.password_TIL) TextInputLayout passwordTIL;
    @BindView(R.id.password_et) EditText passwordET;
    @BindView(R.id.login_btn) Button loginBTN;
    @BindView(R.id.top_logo) ImageView topLogo;
    private ImageLoader imageLoader;
    @OnClick(R.id.login_btn) void loginBTNClick()
    {
        String username = usernameEt.getText().toString();
        String password = passwordET.getText().toString();
        if (username.length() == 0)
        {
            usernameTIL.setError(getResources().getString(R.string.usernameError));
        }
        else if (password.length() == 0)
        {
            usernameTIL.setError(null);
            passwordTIL.setError(getString(R.string.passwordError));
        }
        else
        {
            usernameTIL.setError(null);
            passwordTIL.setError(null);
            doLogin(username,password);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(Constants.MEDIA_BASE_URL + "1509095583.png", topLogo);
        loginBTN.setEnabled(false);
        usernameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (usernameEt.getText().toString().length()!=0)
                    usernameSet = true;
                else
                   usernameSet= true;
               checkSubmitCondition();
            }
        });
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passwordET.getText().toString().length()!=0)
                    passwordSet=true;
                else
                   passwordSet=false;
                checkSubmitCondition();
            }
        });

    }
    private void checkSubmitCondition()
    {
        if (usernameSet&&passwordSet)
            loginBTN.setEnabled(true);
        else
            loginBTN.setEnabled(false);
    }
    private void doLogin(String username,String password)
    {
        progress = new ProgressDialog(this);
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
        ServerRequest request = new ServerRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setDevice_type(1);
        request.setDevice_id( Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
        OSPermissionSubscriptionState status = OneSignal.getPermissionSubscriptionState();
        status.getPermissionStatus().getEnabled();
        request.setToken(status.getSubscriptionStatus().getUserId());

        Call<ServerResponse> response = requestInterface.login(request);
        RetrofitWithRetry.enqueueWithRetry(response,3,new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();

                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            user_detail=LoginActivity.this.getSharedPreferences(Constants.USER_DETAIL, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = user_detail.edit();
                            editor.putString(Constants.JWT, "Bearer "+resp.getJwt());
                            editor.putBoolean(Constants.IS_LOGGED_IN, true);
                            editor.putString(Constants.USER_FIRST_NAME, resp.getProfile().getFirstname());
                            editor.putString(Constants.USER_LAST_NAME, resp.getProfile().getLastname());
                            editor.putString(Constants.PROFILE_IMAGE_NAME, resp.getProfile().getProfile_image());
                            editor.putInt(Constants.USER_ROLE_ID,resp.getProfile().getRole_id());
                            editor.putString(Constants.USER_ROLE_NAME,resp.getProfile().getRole().getName());
                            editor.putBoolean(Constants.couldViewGeneralCartable,false);
                            editor.putBoolean(Constants.couldAssignTask,false);
                            ArrayList<Permission> permissions = resp.getProfile().getRole().getPermissions();
                            for (int i=0;i<permissions.size();i++)
                            {
                                if (permissions.get(i).getName().equals("مشاهده کارتابل عمومی"))
                                {
                                    editor.putBoolean(Constants.couldViewGeneralCartable,true);
                                }
                                else if (permissions.get(i).getName().equals("امکان واگذاری وظایف در کارتابل"))
                                {
                                    editor.putBoolean(Constants.couldAssignTask,true);
                                }
                            }
                            editor.apply();
                            Intent i = new Intent(LoginActivity.this, LoggedInActivity.class);
                            startActivity(i);
                            LoginActivity.this.finish();
                        }
                        break;
                    case 401:
                        Toast.makeText(LoginActivity.this, getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        if (resp != null) {
                            Toast.makeText(LoginActivity.this, resp.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(LoginActivity.this, getString(R.string.network_problem), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("error:",t.getMessage());
            }
        });
        Log.i("url",response.request().url().toString());
    }
}
