package com.hotelsys.cartable.androidapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.hotelsys.cartable.androidapp.Models.Category;
import com.hotelsys.cartable.androidapp.Server.RequestInterface;
import com.hotelsys.cartable.androidapp.Server.RetrofitWithRetry;
import com.hotelsys.cartable.androidapp.Server.ServerResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Mohammad on 12/17/2017.
 */

public class InstructionTabsFragment extends Fragment {
    ViewPagerAdapter adapter;

    private PagerSlidingTabStrip tabLayout;
    private ViewPager viewPager;
    private SharedPreferences user_detail;
    private ProgressDialog progress;
    private Retrofit retrofit;
    private RequestInterface requestInterface;
    private Call<ServerResponse> response;

    public InstructionTabsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_instructions_tabs, container, false);

        user_detail = getActivity().getSharedPreferences(Constants.USER_DETAIL, Context.MODE_PRIVATE);

        progress = new ProgressDialog(getActivity());
        progress.setMessage(getString(R.string.wait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress = new ProgressDialog(getActivity());
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestInterface = retrofit.create(RequestInterface.class);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        tabLayout.getTabsContainer().setGravity(Gravity.RIGHT);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setupViewPager();
        tabLayout.setViewPager(viewPager);
        tabLayout.setIndicatorColor(getResources().getColor(R.color.white));
        viewPager.setOffscreenPageLimit(4);
        return view;
    }
    private void setupViewPager() {
        adapter = new ViewPagerAdapter(getChildFragmentManager());
        progress.show();
        response = requestInterface.getInstructionCategories(user_detail.getString(Constants.JWT,""));
        RetrofitWithRetry.enqueueWithRetry(response, 3, new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();
                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            ArrayList<Category> categories = resp.getCategories();
                            if (categories.size() > 0) {
                                for (int i = categories.size()-1; i >=0; i--) {
                                    adapter.addFragment(BlogListFragment.newInstance(2,categories.get(i).getId()),categories.get(i).getTitle());
                                }
                                adapter.notifyDataSetChanged();
                                viewPager.setCurrentItem(adapter.getCount());
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
                Log.d("error:", t.getMessage());
            }
        });
        viewPager.setAdapter(adapter);
    }
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
