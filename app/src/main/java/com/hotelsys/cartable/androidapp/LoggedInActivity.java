package com.hotelsys.cartable.androidapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hotelsys.cartable.androidapp.Models.Permission;
import com.hotelsys.cartable.androidapp.Server.RequestInterface;
import com.hotelsys.cartable.androidapp.Server.RetrofitWithRetry;
import com.hotelsys.cartable.androidapp.Server.ServerResponse;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoggedInActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
   @BindView(R.id.drawer) DrawerLayout drawerLayout;
    FragmentManager fragmentManager;
    private ImageLoader imageLoader;

    private SharedPreferences user_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        ButterKnife.bind(this);
        user_detail = getSharedPreferences(Constants.USER_DETAIL, Context.MODE_PRIVATE);
        imageLoader = ImageLoader.getInstance();
        Fragment fragment = new CartableFragment();
        Bundle bundle = new Bundle();
        toolbar.setTitle(getString(R.string.my_cartable));
        bundle.putInt("type", 2); //1 for general cartable,2 for my cartable
        fragment.setArguments(bundle);
        setSupportActionBar(toolbar);
        initNavigationDrawer();
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();

    }

    private void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        Menu menu = navigationView.getMenu();
        MenuItem generalCartableMenu = menu.findItem(R.id.general_cartable);
        MenuItem addTaskMenu = menu.findItem(R.id.add_task);
        if (!user_detail.getBoolean(Constants.couldViewGeneralCartable, false)) {
            generalCartableMenu.setVisible(false);
            generalCartableMenu.setEnabled(false);
        }
        if (user_detail.getInt(Constants.USER_ROLE_ID, 0) != 1) {
            addTaskMenu.setVisible(false);
            addTaskMenu.setEnabled(false);
        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
                drawerLayout.bringToFront();
                drawerLayout.requestLayout();
            }
        };
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.signOut) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(LoggedInActivity.this);
                    builder.setTitle(getString(R.string.signOut))
                            .setMessage(getString(R.string.signOutDesc))
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences.Editor editor = user_detail.edit();
                                    editor.putString(Constants.JWT, null);
                                    editor.putBoolean(Constants.IS_LOGGED_IN, false);
                                    editor.putString(Constants.USER_FIRST_NAME, null);
                                    editor.putString(Constants.USER_LAST_NAME, null);
                                    editor.putString(Constants.PROFILE_IMAGE_NAME, null);
                                    editor.putInt(Constants.USER_ROLE_ID, -1);
                                    editor.putString(Constants.USER_ROLE_NAME, null);
                                    editor.apply();
                                    Intent i = new Intent(LoggedInActivity.this, LoginActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);
                                    LoggedInActivity.this.finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_lock_lock)
                            .show();
                } else {
                    if (isInternetAvailable()) {
                        Fragment fragment;
                        Bundle bundle;
                        FragmentTransaction fragmentTransaction;
                        switch (id) {
                            case R.id.my_cartable:
                                getSupportActionBar().setTitle(getString(R.string.my_cartable));
                                fragment = new CartableFragment();
                                bundle = new Bundle();
                                bundle.putInt("type", 2); //1 for general cartable,2 for my cartable
                                fragment.setArguments(bundle);
                                break;
                            case R.id.general_cartable:
                                getSupportActionBar().setTitle(getString(R.string.general_cartable));
                                fragment = new CartableFragment();
                                bundle = new Bundle();
                                bundle.putInt("type", 1); //1 for general cartable,2 for my cartable
                                fragment.setArguments(bundle);
                                break;
                            case R.id.add_task:
                                getSupportActionBar().setTitle(getString(R.string.add_task));
                                fragment = new AddTaskFragment();
                                break;
                            case R.id.news:
                                getSupportActionBar().setTitle(getString(R.string.news));
                                fragment = BlogListFragment.newInstance(1);
                                break;
                            case R.id.instructions:
                                getSupportActionBar().setTitle(getString(R.string.instructions));
                                fragment = new InstructionTabsFragment();
                                break;
                            case R.id.profile:
                                getSupportActionBar().setTitle(getString(R.string.profile));
                                fragment = new ProfileFragment();
                                break;
                            default:
                                getSupportActionBar().setTitle(getString(R.string.my_cartable));
                                fragment = new CartableFragment();
                                bundle = new Bundle();
                                bundle.putInt("type", 2); //1 for general cartable,2 for my cartable
                                fragment.setArguments(bundle);
                                break;
                        }

                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.content_frame, fragment, "CURRENT_FRAGMENT");
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    } else {
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(LoggedInActivity.this);
                        builder.setTitle(getString(R.string.internet))
                                .setMessage(getString(R.string.need_net_desc))
                                .setPositiveButton(getString(R.string.settings), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }

                drawerLayout.closeDrawer(GravityCompat.START, true);
                return true;
            }
        });
        View header = navigationView.getHeaderView(0);
         TextView user_name, role_name;
         CircleImageView profile_iv;
        user_name = (TextView) header.findViewById(R.id.user_name);
        role_name = (TextView) header.findViewById(R.id.user_role);
        profile_iv = (CircleImageView) header.findViewById(R.id.profile_image);
        user_name.setText(user_detail.getString(Constants.USER_FIRST_NAME, "") + " " + user_detail.getString(Constants.USER_LAST_NAME, ""));
        role_name.setText(user_detail.getString(Constants.USER_ROLE_NAME, ""));
        imageLoader.displayImage(Constants.MEDIA_BASE_URL + user_detail.getString(Constants.PROFILE_IMAGE_NAME, ""), profile_iv);
        getProfile();
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment=new ProfileFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment, "CURRENT_FRAGMENT");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                drawerLayout.closeDrawer(GravityCompat.START, true);
            }
        });
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void getProfile() {
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

                ServerResponse resp = response.body();
                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            SharedPreferences.Editor editor = user_detail.edit();
                            editor.putString(Constants.PROFILE_IMAGE_NAME, resp.getProfile().getProfile_image());
                            editor.putString(Constants.USER_FIRST_NAME, resp.getProfile().getFirstname());
                            editor.putString(Constants.USER_LAST_NAME, resp.getProfile().getLastname());
                            editor.putString(Constants.PROFILE_IMAGE_NAME, resp.getProfile().getProfile_image());
                            editor.putInt(Constants.USER_ROLE_ID, resp.getProfile().getRole_id());
                            editor.putString(Constants.USER_ROLE_NAME, resp.getProfile().getRole().getName());
                            editor.putBoolean(Constants.couldViewGeneralCartable, false);
                            editor.putBoolean(Constants.couldAssignTask, false);
                            ArrayList<Permission> permissions = resp.getProfile().getRole().getPermissions();
                            for (int i = 0; i < permissions.size(); i++) {
                                if (permissions.get(i).getName().equals("مشاهده کارتابل عمومی")) {
                                    editor.putBoolean(Constants.couldViewGeneralCartable, true);
                                } else if (permissions.get(i).getName().equals("امکان واگذاری وظایف در کارتابل")) {
                                    editor.putBoolean(Constants.couldAssignTask, true);
                                }
                            }
                            editor.apply();
                            initNavigationDrawer();
                        }
                        break;

                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("error:", t.getMessage());
            }
        });
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
