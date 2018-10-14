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
import android.widget.TextView;
import android.widget.Toast;

import com.hotelsys.cartable.androidapp.Adapters.CartableListAdapter;
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
public class CartableFragment extends Fragment {

    @BindView(R.id.cartable_list) ListView cartableList;
    @BindView(R.id.cartable_empty_tv) TextView cartable_empty_tv;
    private ArrayList<CartableItem> cartableContent;
    ProgressDialog progress;
    CartableListAdapter cartableListAdapter;
    private SharedPreferences user_detail;
    private int type; //1 for general cartable,2 for my cartable
    public CartableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cartable, container, false);
        AppCompatActivity parent = (AppCompatActivity)getActivity();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.type = bundle.getInt("type", 2);
        }
        if (type == 1)
        {
            parent.getSupportActionBar().setTitle(getString(R.string.general_cartable));
        }
        else if (type == 2)
        {
            parent.getSupportActionBar().setTitle(getString(R.string.my_cartable));
        }
        ButterKnife.bind(this,view);
        user_detail = getActivity().getSharedPreferences(Constants.USER_DETAIL, Context.MODE_PRIVATE);
        getCartable();
        cartableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppCompatActivity parent_activity = (AppCompatActivity)getActivity();
                parent_activity.getSupportActionBar().setTitle(getString(R.string.request_detail));
                Fragment fragment = new RequestDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("id", cartableContent.get(position).getId());
                bundle.putInt("type",type);
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return  view;
    }
    private void getCartable()
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

        if (type == 1)
        {
            response = requestInterface.getGeneralCartable(user_detail.getString(Constants.JWT, ""));
        }
        else
        {
            response = requestInterface.getMyCartable(user_detail.getString(Constants.JWT, ""));
        }
        RetrofitWithRetry.enqueueWithRetry(response,3,new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();
                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            cartableContent=resp.getTasks();
                            if (cartableContent.size() == 0)
                            {
                                cartable_empty_tv.setVisibility(View.VISIBLE);
                                cartableList.setVisibility(View.GONE);
                            }
                            else {
                                cartableListAdapter = new CartableListAdapter(cartableContent, getActivity());
                                cartableList.setAdapter(cartableListAdapter);
                            }
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
