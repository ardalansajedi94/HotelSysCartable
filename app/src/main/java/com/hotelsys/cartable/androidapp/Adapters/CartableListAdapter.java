package com.hotelsys.cartable.androidapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotelsys.cartable.androidapp.Constants;
import com.hotelsys.cartable.androidapp.Helpers.JalaliCalendar;
import com.hotelsys.cartable.androidapp.Models.CartableItem;
import com.hotelsys.cartable.androidapp.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Mohammad on 12/11/2017.
 */

public class CartableListAdapter extends BaseAdapter {
    private ArrayList<CartableItem> _data;
    private Context _c;
    @BindView(R.id.request_no_tv) TextView request_no_tv;
    @BindView(R.id.request_type_tv) TextView request_type_tv;
    @BindView(R.id.requester_tv) TextView requester_tv;
    @BindView(R.id.request_date_tv) TextView request_date_tv;
    @BindView(R.id.request_state_tv) TextView request_state_tv;
    @BindView(R.id.CartableImage) ImageView request_image;
    @BindView(R.id.cartable_image_container) ConstraintLayout cartable_image_container;
    private ImageLoader imageLoader ;
    public CartableListAdapter(ArrayList<CartableItem> data,Context c)
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
        final CartableItem item = _data.get(position);
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item_cartable, null);
        }
        ButterKnife.bind(this,v);
        request_no_tv.setText(String.valueOf(item.getId()));
        if (item.getRequest_type() != null)
            request_type_tv.setText(item.getRequest_type());
        if(item.getGuest() != null)
        {
            requester_tv.setText(_c.getString(R.string.room)+":"+String.valueOf(item.getGuest().getRoom_no()));
        }
        else
        {
            requester_tv.setText(_c.getString(R.string.management));
        }

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date;
        try {
            date = fmt.parse(item.getCreated_at());
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            JalaliCalendar jDate=new JalaliCalendar(calendar);
            request_date_tv.setText(jDate.getDayOfWeekDayMonthString()+" "+String.valueOf(jDate.getYear()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        switch (item.getStatus().getId())
        {
            case 1:
            case 6:
                request_state_tv.setBackgroundColor(_c.getResources().getColor(R.color.in_progress_color));
                break;
            case 2:
            case 3:
                request_state_tv.setBackgroundColor(_c.getResources().getColor(R.color.finished_color));
                break;
            case 4:
            case 5:
                request_state_tv.setBackgroundColor(_c.getResources().getColor(R.color.cancelled_color));
                break;
        }
        request_state_tv.setTextColor(_c.getResources().getColor(R.color.white));
        request_state_tv.setText(item.getStatus().getTitle());
        if (item.getImages()!=null)
        {
            if (item.getImages().size()!=0)
            {
                cartable_image_container.setVisibility(View.VISIBLE);
                request_image.setImageResource(0);
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .displayer(new RoundedBitmapDisplayer(15)).cacheInMemory(true).cacheOnDisk(true).build();
                imageLoader= ImageLoader.getInstance();
                imageLoader.displayImage(Constants.MEDIA_BASE_URL + item.getImages().get(0).getFile_source(), request_image, options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        ImageView iv = (ImageView) view;
                        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        iv.setAdjustViewBounds(true);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });

            }
            else
            {
                cartable_image_container.setVisibility(View.GONE);
            }
        }
        else
        {
            cartable_image_container.setVisibility(View.GONE);
        }

        return v;
    }
}
