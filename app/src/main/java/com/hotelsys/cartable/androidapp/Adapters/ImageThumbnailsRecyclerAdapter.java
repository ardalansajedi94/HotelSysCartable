package com.hotelsys.cartable.androidapp.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotelsys.cartable.androidapp.Models.Image;
import com.hotelsys.cartable.androidapp.R;
import com.jsibbold.zoomage.ZoomageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Mohammad on 12/20/2017.
 */

public class ImageThumbnailsRecyclerAdapter extends RecyclerView.Adapter <ImageThumbnailsRecyclerAdapter.ImageThumbnailViewHolder>{
    private ArrayList<Image> list;
    private Context _c;
    public ImageThumbnailsRecyclerAdapter(Context context,ArrayList<Image> Data) {
        this.list = Data;
        this._c = context;
    }
    @Override
    public ImageThumbnailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_item_add_image_thumbnail, parent, false);
        ImageThumbnailViewHolder holder = new ImageThumbnailViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(final ImageThumbnailViewHolder holder, int position) {
        Image item = list.get(position);
        if (item.getInternalBitmap() == null)
        {
            holder.imageView.setImageURI(item.getInteranl_uri());
        }
        else
        {
            holder.imageView.setImageBitmap(item.getInternalBitmap());
        }

        holder.deleteBtn.setTag(position);
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public ArrayList<Image> getData()
    {
        return this.list;
    }
    public class ImageThumbnailViewHolder extends RecyclerView.ViewHolder {
        public Button deleteBtn;
        public ImageView imageView;


        public ImageThumbnailViewHolder(View v) {
            super(v);
            deleteBtn = (Button) v.findViewById(R.id.deleteBtn);
            imageView = (ImageView) v.findViewById(R.id.thumbnail_image);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog nagDialog = new Dialog(_c,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                    nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    nagDialog.setCancelable(false);
                    nagDialog.setContentView(R.layout.preview_image);
                    Button btnClose = (Button)nagDialog.findViewById(R.id.btnIvClose);
                    LinearLayout containerLayout = (LinearLayout)nagDialog.findViewById(R.id.container_layout);
                    ZoomageView ivPreview = (ZoomageView)nagDialog.findViewById(R.id.iv_preview_image);
                    ivPreview.setImageDrawable(imageView.getDrawable());
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
            });
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) deleteBtn.getTag();
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position,list.size());
                }
            });
        }
    }
}
