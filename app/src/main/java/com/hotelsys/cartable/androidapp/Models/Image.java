package com.hotelsys.cartable.androidapp.Models;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Mohammad on 12/17/2017.
 */

public class Image {
    private String image_source,file_source,path;
    private Bitmap internalBitmap;
    private Uri interanl_uri;

    public Uri getInteranl_uri() {
        return interanl_uri;
    }

    public void setInteranl_uri(Uri interanl_uri) {
        this.interanl_uri = interanl_uri;
    }

    public Bitmap getInternalBitmap() {
        return internalBitmap;
    }

    public void setInternalBitmap(Bitmap internalBitmap) {
        this.internalBitmap = internalBitmap;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFile_source() {
        return file_source;
    }

    public void setFile_source(String file_source) {
        this.file_source = file_source;
    }

    public String getImage_source() {
        return image_source;
    }

    public void setImage_source(String image_source) {
        this.image_source = image_source;
    }
}
