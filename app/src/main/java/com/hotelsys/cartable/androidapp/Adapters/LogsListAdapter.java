package com.hotelsys.cartable.androidapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelsys.cartable.androidapp.Helpers.FormatHelper;
import com.hotelsys.cartable.androidapp.Helpers.JalaliCalendar;
import com.hotelsys.cartable.androidapp.Models.CartableItem;
import com.hotelsys.cartable.androidapp.Models.RequestLog;
import com.hotelsys.cartable.androidapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohammad on 12/16/2017.
 */

public class LogsListAdapter extends BaseAdapter {
    private ArrayList<RequestLog> _data;
    private Context _c;
    @BindView(R.id.no_tv) TextView no_tv;
    @BindView(R.id.state_tv) TextView state_tv;
    @BindView(R.id.worker_tv) TextView worker_tv;
    @BindView(R.id.assigner_tv) TextView assigner_tv;
    @BindView(R.id.date_tv) TextView date_tv;
    @BindView(R.id.assigner_container) LinearLayout assigner_container;
    @BindView(R.id.worker_container) LinearLayout worker_container;
    public LogsListAdapter(ArrayList<RequestLog> data,Context c)
    {
        this._data = data;
        this._c = c;
    }
    public int getCount() {
        // TODO Auto-generated method stub
        return _data.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return _data.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;
        final RequestLog item = _data.get(position);
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item_request_log, null);
        }
        ButterKnife.bind(this,v);
        no_tv.setText(FormatHelper.toPersianNumber(String.valueOf(item.getNo())));
        state_tv.setText(item.getAction());
        if (item.getEmployee()!=null)
        {
            assigner_container.setVisibility(View.VISIBLE);
            assigner_tv.setText(item.getEmployee().getFirstname()+" "+item.getEmployee().getLastname());
        }
        else
        {
            assigner_container.setVisibility(View.GONE);
        }
        if (item.getWorker()!=null)
        {
            worker_container.setVisibility(View.VISIBLE);
            worker_tv.setText(item.getWorker().getFirstname()+" "+item.getWorker().getLastname());
        }
        else
        {
            worker_container.setVisibility(View.GONE);
        }
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date;
        try {
            date = fmt.parse(item.getCreated_at());
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            JalaliCalendar jDate=new JalaliCalendar(calendar);
            date_tv.setText(jDate.getDayOfWeekDayMonthString()+" "+String.valueOf(jDate.getYear()) +" "+_c.getString(R.string.time)+" "+FormatHelper.toPersianNumber(String.valueOf(calendar.get(Calendar.MINUTE)))+" : "+FormatHelper.toPersianNumber(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return v;
    }
}
