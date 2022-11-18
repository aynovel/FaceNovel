package com.bytedance.club.interfaces;

import android.content.Context;
import android.widget.ImageView;

import java.io.Serializable;


public interface ImageLoader extends Serializable {
    void displayImage(Context context, String path, ImageView imageView);
}