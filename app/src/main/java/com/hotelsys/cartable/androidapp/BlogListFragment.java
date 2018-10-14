package com.hotelsys.cartable.androidapp;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.hotelsys.cartable.androidapp.Adapters.BlogListAdapter;
import com.hotelsys.cartable.androidapp.Adapters.CartableListAdapter;
import com.hotelsys.cartable.androidapp.Models.BlogItem;
import com.hotelsys.cartable.androidapp.Models.CartableItem;
import com.hotelsys.cartable.androidapp.Server.RequestInterface;
import com.hotelsys.cartable.androidapp.Server.RetrofitWithRetry;
import com.hotelsys.cartable.androidapp.Server.ServerResponse;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlogListFragment extends Fragment {
    @BindView(R.id.blogList) ListView blogList;
    private int type; //1 for news ,2 for instructions
    private int cat_id;
    private ArrayList<BlogItem> blogContent;
    ProgressDialog progress;
    BlogListAdapter blogListAdapter;
    private SharedPreferences user_detail;
    public BlogListFragment() {
        // Required empty public constructor
    }

    public static BlogListFragment newInstance(int type,int category) {
        BlogListFragment myFragment = new BlogListFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("cat_id", category);
        myFragment.setArguments(args);
        return myFragment;
    }
    public static BlogListFragment newInstance(int type) {
        BlogListFragment myFragment = new BlogListFragment();

        Bundle args = new Bundle();
        args.putInt("type", type);
        myFragment.setArguments(args);

        return myFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_blog_list, container, false);
        Bundle bundle = this.getArguments();
        ButterKnife.bind(this,view);
        user_detail = getActivity().getSharedPreferences(Constants.USER_DETAIL, Context.MODE_PRIVATE);
        if (bundle != null) {
            this.type = bundle.getInt("type", 1);
            this.cat_id = bundle.getInt("cat_id", 0);
        }
        AppCompatActivity parent = (AppCompatActivity)getActivity();

        if (type==1)
        {
            parent.getSupportActionBar().setTitle(getString(R.string.news));
            getInternalNews();
        }
        else if (type == 2)
        {
            parent.getSupportActionBar().setTitle(getString(R.string.instructions));
            getInstructions();
        }

        blogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = BlogPostViewFragment.newInstance(type,blogContent.get(position).getId());
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return view;
    }
    private void getInstructions()
    {
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
        response = requestInterface.getInstuctions(user_detail.getString(Constants.JWT, ""),cat_id);
        RetrofitWithRetry.enqueueWithRetry(response,3,new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();
                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            blogContent=resp.getInstructions();
                            blogListAdapter = new BlogListAdapter(blogContent,getActivity());
                            blogList.setAdapter(blogListAdapter);
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
                Log.i("error:",t.getMessage());
            }
        });
    }
    private void getInternalNews()
    {
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
        response = requestInterface.getInternalNews(user_detail.getString(Constants.JWT, ""));
        RetrofitWithRetry.enqueueWithRetry(response,3,new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();
                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            blogContent=resp.getHotelNews();
                            blogListAdapter = new BlogListAdapter(blogContent,getActivity());
                            blogList.setAdapter(blogListAdapter);
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
                Log.i("error:",t.getMessage());
            }
        });
    }

}
