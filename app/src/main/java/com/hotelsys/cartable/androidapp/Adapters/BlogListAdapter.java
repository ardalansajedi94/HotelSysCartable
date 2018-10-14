package com.hotelsys.cartable.androidapp.Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotelsys.cartable.androidapp.Constants;
import com.hotelsys.cartable.androidapp.Models.BlogItem;
import com.hotelsys.cartable.androidapp.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Mohammad on 12/17/2017.
 */

public class BlogListAdapter extends BaseAdapter {
    @BindView(R.id.item_image_iv) ImageView itemImage;
    @BindView(R.id.date_tv) TextView dateTv;
    @BindView(R.id.title_tv) TextView title_tv;
    @BindView(R.id.description_tv) TextView description_tv;
    private ArrayList<BlogItem> _data;
    private Context _c;
    private ImageLoader imageLoader ;
    public BlogListAdapter(ArrayList<BlogItem> data, Context c)
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
        final BlogItem item = _data.get(position);
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item_blog, null);
        }
        ButterKnife.bind(this,v);
        imageLoader= ImageLoader.getInstance();
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date;
        try {
            date = fmt.parse(item.getCreated_at());
            Date now=new Date();
            dateTv.setText(DateUtils.getRelativeTimeSpanString(date.getTime(), now.getTime(),DateUtils.MINUTE_IN_MILLIS));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        title_tv.setText(item.getTitle());
        description_tv.setText(item.getContent());
        imageLoader= ImageLoader.getInstance();
        if (item.getImages()!=null)
        {

            if (item.getImages().size()>0)
            {
                if (item.getImages().get(0).getImage_source()!= null)
                {
                    imageLoader.displayImage(Constants.MEDIA_BASE_URL+item.getImages().get(0).getImage_source(),itemImage);
                } else if (item.getImages().get(0).getFile_source() != null)
                {
                    imageLoader.displayImage(Constants.MEDIA_BASE_URL+item.getImages().get(0).getFile_source(),itemImage);
                }
                else if (item.getImages().get(0).getPath() != null)
                {
                    imageLoader.displayImage(Constants.MEDIA_BASE_URL+item.getImages().get(0).getPath(),itemImage);
                }

            }

        }
        return v;
    }
}
