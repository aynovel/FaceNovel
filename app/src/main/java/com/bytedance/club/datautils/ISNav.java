package com.bytedance.club.datautils;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bytedance.club.interfaces.ImageLoader;
import com.bytedance.club.entry.ISCameraConfig;
import com.bytedance.club.entry.ISListConfig;
import com.bytedance.club.activtiy.ISCameraActivity;
import com.bytedance.club.activtiy.ISListActivity;



public class ISNav {

    private static ISNav instance;

    private ImageLoader loader;

    public static ISNav getInstance() {
        if (instance == null) {
            synchronized (ISNav.class) {
                if (instance == null) {
                    instance = new ISNav();
                }
            }
        }
        return instance;
    }

    public void init(@NonNull ImageLoader loader) {
        this.loader = loader;
    }

    public void displayImage(Context context, String path, ImageView imageView) {
        if (loader != null) {
            loader.displayImage(context, path, imageView);
        }
    }

    public void toListActivity(Object source, ISListConfig config, int reqCode) {
        if (source instanceof Activity) {
            ISListActivity.startForResult((Activity) source, config, reqCode);
        } else if (source instanceof Fragment) {
            ISListActivity.startForResult((Fragment) source, config, reqCode);
        } else if (source instanceof android.app.Fragment) {
            ISListActivity.startForResult((android.app.Fragment) source, config, reqCode);
        }
    }

    public void toCameraActivity(Object source, ISCameraConfig config, int reqCode) {
        if (source instanceof Activity) {
            ISCameraActivity.startForResult((Activity) source, config, reqCode);
        } else if (source instanceof Fragment) {
            ISCameraActivity.startForResult((Fragment) source, config, reqCode);
        } else if (source instanceof android.app.Fragment) {
            ISCameraActivity.startForResult((android.app.Fragment) source, config, reqCode);
        }
    }

}
