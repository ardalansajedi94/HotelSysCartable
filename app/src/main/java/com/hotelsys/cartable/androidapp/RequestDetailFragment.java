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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.hotelsys.cartable.androidapp.Adapters.BasketListAdapter;
import com.hotelsys.cartable.androidapp.Adapters.LogsListAdapter;
import com.hotelsys.cartable.androidapp.Helpers.FormatHelper;
import com.hotelsys.cartable.androidapp.Helpers.JalaliCalendar;
import com.hotelsys.cartable.androidapp.Models.Basket;
import com.hotelsys.cartable.androidapp.Models.CartableItem;
import com.hotelsys.cartable.androidapp.Models.Profile;
import com.hotelsys.cartable.androidapp.Models.RequestAction;
import com.hotelsys.cartable.androidapp.Models.RequestLog;
import com.hotelsys.cartable.androidapp.Models.Role;
import com.hotelsys.cartable.androidapp.Server.RequestInterface;
import com.hotelsys.cartable.androidapp.Server.RetrofitWithRetry;
import com.hotelsys.cartable.androidapp.Server.ServerRequest;
import com.hotelsys.cartable.androidapp.Server.ServerResponse;
import com.jsibbold.zoomage.ZoomageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestDetailFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    @BindView(R.id.request_title_tv) TextView RequestTitleTv;
    @BindView(R.id.explanationLabelTv) TextView explanationLabelTv;
    @BindView(R.id.explanationEt) EditText explanationEt;
    @BindView(R.id.request_state_tv) TextView RequestStateTv;
    @BindView(R.id.requester_label_tv) TextView RequesterLabelTv;
    @BindView(R.id.requester_tv) TextView RequesterTv;
    @BindView(R.id.room_no_container) LinearLayout RoomNoContainer;
    @BindView(R.id.room_tv) TextView RoomTv;
    @BindView(R.id.request_date_tv) TextView RequestDateTv;
    @BindView(R.id.request_detail_container) LinearLayout RequestDetailContainer;
    @BindView(R.id.request_detail_tv) TextView RequestDetailTv;
    @BindView(R.id.basket_list) NonScrollableListView BasketList;
    @BindView(R.id.management_explanation_container) LinearLayout ManagementExplanationContainer;
    @BindView(R.id.management_explanation_tv) TextView ManagementExplanationTv;
    @BindView(R.id.operation_spinner) Spinner OperationSpinner;
    @BindView(R.id.roleEmployeeContainer) LinearLayout RoleEmployeeContainer;
    @BindView(R.id.role_spinner) Spinner RoleSpinner;
    @BindView(R.id.employee_spinner) Spinner EmployeeSpinner;
    @BindView(R.id.submit_btn) Button SubmitBtn;
    @BindView(R.id.logs_container) LinearLayout LogsContainer;
    @BindView(R.id.logs_list) NonScrollableListView LogsList;
    @BindView(R.id.mainScrollView) ScrollView mainScrollView;
    @BindView(R.id.slider) SliderLayout sliderLayout;
    private ProgressDialog progress;
    private SharedPreferences user_detail;
    private int id,type; //type = 1 for general cartable and type =2 for my cartable
    private ArrayList<Role> roles=new ArrayList<>();
    private ArrayList<RequestAction> actions = new ArrayList<>();
    private ArrayList<Profile> employees=new ArrayList<>();
    private List<String> employeesSpinnerContnet=new ArrayList<>();
    private List<String> operationSpinnerContent;
    private List<Integer> employeesSpinnerContnetIds=new ArrayList<>();

    CartableItem item;
    @OnClick(R.id.submit_btn) void submitBtnClicked()
    {
        if (this.type ==1)
            assignTask();
        else if (this.type == 2)
            doTask();
    }
    public RequestDetailFragment() {
        // Required empty public constructor
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
        ImageLoader  imageLoader= ImageLoader.getInstance();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_request_detail, container, false);
        ButterKnife.bind(this,view);

        user_detail = getActivity().getSharedPreferences(Constants.USER_DETAIL, Context.MODE_PRIVATE);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            this.id = bundle.getInt("id", 0);
            this.type = bundle.getInt("type",0);
        }
        getRequest(id);
        RoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                employeesSpinnerContnet=new ArrayList<String>();
                for (int i=0;i<employees.size();i++)
                {
                    if (employees.get(i).getRole_id() == roles.get(position).getId())
                    {
                        employeesSpinnerContnet.add(employees.get(i).getFirstname() + " " +employees.get(i).getLastname());
                        employeesSpinnerContnetIds.add(employees.get(i).getId());
                    }
                }
                ArrayAdapter<String> employeesSpinnerAdapter= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,employeesSpinnerContnet);
                employeesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                EmployeeSpinner.setAdapter(employeesSpinnerAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        OperationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (operationSpinnerContent.size() == 4)
                {
                    if (position == 0)
                        RoleEmployeeContainer.setVisibility(View.VISIBLE);
                    else
                        RoleEmployeeContainer.setVisibility(View.GONE);
                    if (position == 3)
                        explanationLabelTv.setText(getString(R.string.response));
                    else
                        explanationLabelTv.setText(getString(R.string.explanation));
                }
                else
                {
                    if (position == 2)
                        explanationLabelTv.setText(getString(R.string.response));
                    else
                        explanationLabelTv.setText(getString(R.string.explanation));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }
    private void getRequest(int id)
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
        response = requestInterface.dynamicGetRequestJWT(user_detail.getString(Constants.JWT, ""),Constants.BASE_URL+"cartable/"+String.valueOf(id));
        RetrofitWithRetry.enqueueWithRetry(response,3,new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();
                switch (response.code()) {
                    case 200:
                        getAssignTaskItems();
                        if (resp != null) {
                             item=resp.getRequest();
                            if (item.getTitle() != null )
                            {
                                RequestTitleTv.setText(item.getTitle());
                                RoomNoContainer.setVisibility(View.GONE);
                                RequesterLabelTv.setText(getString(R.string.requester));
                                if (item.getAssigner() != null)
                                {
                                    RequesterTv.setText(item.getAssigner().getFirstname()+" "+item.getAssigner().getLastname());
                                }
                                RequestDetailTv.setText(item.getContent());
                                BasketList.setVisibility(View.GONE);
                            }
                            else
                            {
                                if (item.getRequest_type() != null)
                                {
                                    if (item.getRequest_type().equals("food"))
                                    {
                                        Log.i("type",item.getRequest_type());
                                        RequestTitleTv.setText(getString(R.string.food_order));
                                        RequestDetailContainer.setVisibility(View.GONE);
                                        if (!item.getRequest().getBasket().isEmpty())
                                        {
                                            for (int i=0;i<item.getRequest().getBasket().size();i++)
                                            {
                                                item.getRequest().getBasket().get(i).setNo(i+1);
                                            }
                                            ArrayList<Basket> basket=item.getRequest().getBasket();
                                            BasketListAdapter basketListAdapter = new BasketListAdapter(basket,getActivity());
                                            BasketList.setAdapter(basketListAdapter);
                                        }

                                    }
                                    else
                                    {
                                        BasketList.setVisibility(View.GONE);
                                        RequestTitleTv.setText(item.getRequest_type());
                                        RequestDetailTv.setText(item.getDetail());
                                    }
                                }
                                else
                                {
                                    RequestTitleTv.setText("");
                                }
                                RequesterLabelTv.setText(getString(R.string.guest)+": ");
                                RequesterTv.setText(item.getGuest().getFirstname() + " "+item.getGuest().getLastname());
                                RoomTv.setText(FormatHelper.toPersianNumber(String.valueOf(item.getGuest().getRoom_no())));
                            }
                            RequestStateTv.setText(item.getStatus().getTitle());
                            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            Date date;
                            try {
                                date = fmt.parse(item.getCreated_at());
                                GregorianCalendar calendar = new GregorianCalendar();
                                calendar.setTime(date);
                                JalaliCalendar jDate=new JalaliCalendar(calendar);
                                RequestDateTv.setText(jDate.getDayOfWeekDayMonthString()+" "+String.valueOf(jDate.getYear()) +" "+getString(R.string.time)+" "+FormatHelper.toPersianNumber(String.valueOf(calendar.get(Calendar.MINUTE)))+" : "+FormatHelper.toPersianNumber(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))));
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            if (!resp.getLogs().isEmpty())
                            {
                                for (int i=0;i<resp.getLogs().size();i++)
                                {
                                    resp.getLogs().get(i).setNo(i+1);
                                }
                                ArrayList<RequestLog> logs=resp.getLogs();
                                LogsListAdapter logsListAdapter = new LogsListAdapter(logs,getActivity());
                                LogsList.setAdapter(logsListAdapter);
                                LogsContainer.setVisibility(View.VISIBLE);
                                Log.i("logs","not empty");
                            }
                            else
                            {
                                LogsContainer.setVisibility(View.GONE);
                                Log.i("logs"," empty");
                            }
                            if (item.getImages()!=null)
                            {
                                if ( item.getImages().size()!=0)
                                {
                                    sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
                                    sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                                    sliderLayout.setCustomAnimation(new DescriptionAnimation());
                                    sliderLayout.stopAutoCycle();
                                    sliderLayout.addOnPageChangeListener(RequestDetailFragment.this);
                                    if (item.getImages().size()==1)
                                    {
                                        sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
                                    }
                                    for (int i=0;i<item.getImages().size();i++)
                                    {
                                        DefaultSliderView sliderView= new DefaultSliderView(getActivity());
                                        sliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
                                        sliderView.image(Constants.MEDIA_BASE_URL+item.getImages().get(i).getFile_source());
                                        sliderView.setOnSliderClickListener(RequestDetailFragment.this);
                                        sliderLayout.addSlider(sliderView);
                                    }
                                }
                                else
                                    sliderLayout.setVisibility(View.GONE);

                            }
                            else
                                sliderLayout.setVisibility(View.GONE);
                            mainScrollView.smoothScrollTo(0, 0);
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
    private void getAssignTaskItems()
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
        response = requestInterface.getAssignTaskItems(user_detail.getString(Constants.JWT, ""));
        RetrofitWithRetry.enqueueWithRetry(response,3,new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();
                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            roles=resp.getRoles();
                            employees = resp.getUsers();
                            List<String> roleSpinnerContent=new ArrayList<String>();
                            for (int i=0;i<roles.size();i++)
                            {
                                roleSpinnerContent.add(roles.get(i).getName());
                            }
                            ArrayAdapter<String> roleSpinnerAdapter= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,roleSpinnerContent);
                            roleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            RoleSpinner.setAdapter(roleSpinnerAdapter);
                            actions =resp.getActions();
                            operationSpinnerContent = new ArrayList<String>();
                            for (int i=0;i<actions.size();i++)
                            {
                                if (!actions.get(i).getKey().equals("10"))
                                {
                                    operationSpinnerContent.add(actions.get(i).getTitle());
                                }
                                else
                                {
                                    if (item.getStatus().getId() == 6 )
                                    {
                                        operationSpinnerContent.add(actions.get(i).getTitle());
                                    }

                                }
                            }
                            if (operationSpinnerContent.size() == 3)
                            {
                                RoleEmployeeContainer.setVisibility(View.GONE);

                            }
                            ArrayAdapter<String> operationsSpinnerAdapter= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,operationSpinnerContent);
                            operationsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            OperationSpinner.setAdapter(operationsSpinnerAdapter);
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
    private void assignTask()
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
        ServerRequest request = new ServerRequest();
        if (operationSpinnerContent.size() == 3)
        {
            if (OperationSpinner.getSelectedItemPosition()==2)
            {
                request.setAction(actions.get(3).getKey());
                request.setResponse(explanationEt.getText().toString());
            }
            else
            {
                request.setAction(actions.get(OperationSpinner.getSelectedItemPosition()+1).getKey());
                if (!explanationEt.getText().toString().equals(""))
                    request.setComment(explanationEt.getText().toString());
                request.setStatus_id(Integer.parseInt(actions.get(OperationSpinner.getSelectedItemPosition()+1).getKey()));
            }
        }
        else if (operationSpinnerContent.size() == 4)
        {
            if (OperationSpinner.getSelectedItemPosition()==0)
            {
                request.setWorker_id(employeesSpinnerContnetIds.get(EmployeeSpinner.getSelectedItemPosition()));
                request.setAction(actions.get(0).getKey());
                if (!explanationEt.getText().toString().equals(""))
                    request.setComment(explanationEt.getText().toString());
                request.setStatus_id(10);
            }
            else if (OperationSpinner.getSelectedItemPosition()==3)
            {
                request.setAction(actions.get(3).getKey());
                request.setResponse(explanationEt.getText().toString());
            }
            else
            {
                request.setAction(actions.get(OperationSpinner.getSelectedItemPosition()).getKey());
                if (!explanationEt.getText().toString().equals(""))
                    request.setComment(explanationEt.getText().toString());
                request.setStatus_id(Integer.parseInt(actions.get(OperationSpinner.getSelectedItemPosition()).getKey()));
            }
        }
        Call<ServerResponse> response = requestInterface.dynamicPostRequest(user_detail.getString(Constants.JWT,""),Constants.BASE_URL+"cartable/"+String.valueOf(id)+"/assign",request);
        RetrofitWithRetry.enqueueWithRetry(response,3,new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();

                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            Toast.makeText(getActivity(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                            explanationEt.setText("");
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                        break;
                    case 401:
                        Toast.makeText(getActivity(), getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
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
    private void doTask()
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
        ServerRequest request = new ServerRequest();
        if (operationSpinnerContent.size() == 3)
        {
            if (OperationSpinner.getSelectedItemPosition()==2)
            {
                request.setAction(actions.get(3).getKey());
                request.setResponse(explanationEt.getText().toString());
            }
            else
            {
                request.setAction(actions.get(OperationSpinner.getSelectedItemPosition()+1).getKey());
                if (!explanationEt.getText().toString().equals(""))
                    request.setComment(explanationEt.getText().toString());
                request.setStatus_id(Integer.parseInt(actions.get(OperationSpinner.getSelectedItemPosition()+1).getKey()));
            }
        }
        Call<ServerResponse> response = requestInterface.dynamicPostRequest(user_detail.getString(Constants.JWT,""),Constants.BASE_URL+"cartable/"+String.valueOf(id)+"/do",request);
        RetrofitWithRetry.enqueueWithRetry(response,3,new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                progress.dismiss();
                ServerResponse resp = response.body();

                switch (response.code()) {
                    case 200:
                        if (resp != null) {
                            Toast.makeText(getActivity(), getString(R.string.success), Toast.LENGTH_SHORT).show();
                            explanationEt.setText("");
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                        break;
                    case 401:
                        Toast.makeText(getActivity(), getString(R.string.invalid_credentials), Toast.LENGTH_SHORT).show();
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
