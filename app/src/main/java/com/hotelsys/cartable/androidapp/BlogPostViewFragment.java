package com.hotelsys.cartable.androidapp;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.hotelsys.cartable.androidapp.Models.BlogItem;
import com.hotelsys.cartable.androidapp.Server.RequestInterface;
import com.hotelsys.cartable.androidapp.Server.RetrofitWithRetry;
import com.hotelsys.cartable.androidapp.Server.ServerResponse;
import com.jsibbold.zoomage.ZoomageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlogPostViewFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener  {


    private int type; //1 for news ,2 for instructions
    private int id;
    private SliderLayout sliderLayout;
    private BlogItem content;
    private ProgressDialog progress;

    TextView title_tv,content_tv;
    private SharedPreferences user_detail;
    public BlogPostViewFragment() {
        // Required empty public constructor
    }
    public static BlogPostViewFragment newInstance(int type,int id) {
        BlogPostViewFragment myFragment = new BlogPostViewFragment();
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("id", id);
        myFragment.setArguments(args);
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_blog_post_view, container, false);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.type = bundle.getInt("type", 1);
            this.id = bundle.getInt("id", 0);
        }
        user_detail=getActivity().getSharedPreferences(Constants.USER_DETAIL, Context.MODE_PRIVATE);
        sliderLayout = (SliderLayout)view.findViewById(R.id.slider);
        title_tv = (TextView)view.findViewById(R.id.PostViewTitle);
        content_tv = (TextView)view.findViewById(R.id.PostViewContent);
        if (type == 1)
            showNews();
        else if (type == 2)
            showInstruction();

        return view;
    }
    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        sliderLayout.stopAutoCycle();
        super.onStop();
    }
    @Override
    public void onSliderClick(BaseSliderView slider) {
        final Dialog nagDialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setCancelable(false);
        nagDialog.setContentView(R.layout.preview_image);
        Button btnClose = (Button)nagDialog.findViewById(R.id.btnIvClose);
        LinearLayout containerLayout = (LinearLayout)nagDialog.findViewById(R.id.container_layout);
        ZoomageView ivPreview = (ZoomageView)nagDialog.findViewById(R.id.iv_preview_image);
        ImageLoader imageLoader= ImageLoader.getInstance();
        imageLoader.displayImage(slider.getUrl(),ivPreview);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                nagDialog.dismiss();
            }
        });
        containerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nagDialog.dismiss();
            }
        });
        nagDialog.show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider ", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    private void showNews()
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
        response = requestInterface.dynamicGetRequestJWT(user_detail.getString(Constants.JWT, ""),"internal_news/"+String.valueOf(id));
        RetrofitWithRetry.enqueueWithRetry(response,3,new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();
                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
                            sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                            sliderLayout.setCustomAnimation(new DescriptionAnimation());
                            sliderLayout.stopAutoCycle();
                            sliderLayout.addOnPageChangeListener(BlogPostViewFragment.this);
                            content=resp.getTheNews();
                            title_tv.setText(content.getTitle());
                            content_tv.setText(content.getContent());
                            if (content.getImages()!=null)
                            {
                                if (content.getImages().size()>0)
                                {
                                    if (content.getImages().size()==1)
                                    {
                                        sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
                                    }
                                    for (int i=0;i<content.getImages().size();i++)
                                    {

                                        DefaultSliderView sliderView= new DefaultSliderView(getActivity());
                                        sliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
                                        if (content.getImages().get(i).getImage_source() != null)
                                        {
                                            sliderView.image(Constants.MEDIA_BASE_URL+content.getImages().get(i).getImage_source());
                                        } else if (content.getImages().get(i).getFile_source() != null)
                                        {
                                            sliderView.image(Constants.MEDIA_BASE_URL+content.getImages().get(i).getFile_source());
                                        }
                                        else if (content.getImages().get(i).getPath() != null)
                                        {
                                            sliderView.image(Constants.MEDIA_BASE_URL+content.getImages().get(i).getPath());
                                        }
                                        sliderView.getView().setTag(i);
                                        sliderView.setOnSliderClickListener(BlogPostViewFragment.this);
                                        sliderLayout.addSlider(sliderView);
                                    }
                                }
                                else
                                {
                                    sliderLayout.setVisibility(View.GONE);
                                }

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
    private void showInstruction()
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
        response = requestInterface.dynamicGetRequestJWT(user_detail.getString(Constants.JWT, ""),"instructions/"+String.valueOf(id));
        RetrofitWithRetry.enqueueWithRetry(response,3,new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();
                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
                            sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                            sliderLayout.setCustomAnimation(new DescriptionAnimation());
                            sliderLayout.stopAutoCycle();
                            sliderLayout.addOnPageChangeListener(BlogPostViewFragment.this);
                            content=resp.getInstruction();
                            title_tv.setText(content.getTitle());
                            content_tv.setText(content.getContent());
                            if (content.getImages()!=null)
                            {
                                if (content.getImages().size()>0)
                                {
                                    if (content.getImages().size()==1)
                                    {
                                        sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
                                    }
                                    for (int i=0;i<content.getImages().size();i++)
                                    {
                                        DefaultSliderView sliderView= new DefaultSliderView(getActivity());
                                        sliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
                                        if (content.getImages().get(i).getImage_source() != null)
                                        {
                                            sliderView.image(Constants.MEDIA_BASE_URL+content.getImages().get(i).getImage_source());

                                        } else if (content.getImages().get(i).getFile_source() != null)
                                        {
                                            sliderView.image(Constants.MEDIA_BASE_URL+content.getImages().get(i).getFile_source());

                                        }
                                        else if (content.getImages().get(i).getPath() != null)
                                        {
                                            sliderView.image(Constants.MEDIA_BASE_URL+content.getImages().get(i).getPath());

                                        }
                                        sliderView.setOnSliderClickListener(BlogPostViewFragment.this);
                                        sliderLayout.addSlider(sliderView);
                                    }
                                }
                                else
                                {
                                    sliderLayout.setVisibility(View.GONE);
                                }

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
