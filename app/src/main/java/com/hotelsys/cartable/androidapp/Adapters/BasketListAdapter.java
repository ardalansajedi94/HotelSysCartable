package com.hotelsys.cartable.androidapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hotelsys.cartable.androidapp.Helpers.JalaliCalendar;
import com.hotelsys.cartable.androidapp.Models.Basket;
import com.hotelsys.cartable.androidapp.Models.CartableItem;
import com.hotelsys.cartable.androidapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohammad on 12/17/2017.
 */

public class BasketListAdapter extends BaseAdapter {
    private ArrayList<Basket> _data;
    private Context _c;
    @BindView(R.id.no_tv) TextView no_tv;
    @BindView(R.id.food_name_tv) TextView food_name_tv;
    @BindView(R.id.restaurant_name_tv) TextView restaurant_name_tv;
    @BindView(R.id.type_tv) TextView type_tv;
    @BindView(R.id.count_tv) TextView count_tv;
    public BasketListAdapter(ArrayList<Basket> data,Context c)
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
        final Basket item = _data.get(position);
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item_basket, null);
        }
        ButterKnife.bind(this,v);
        no_tv.setText(String.valueOf(item.getNo()));
        type_tv.setText(item.getType());
        count_tv.setText(String.valueOf(item.getCount()));
        food_name_tv.setText(item.getFood().getTitle());
        if (item.getRestaurant()!=null)
            restaurant_name_tv.setText(item.getRestaurant().getName());
        else if (item.getCafe()!=null)
            restaurant_name_tv.setText(item.getCafe().getName());
        return v;
    }
}
