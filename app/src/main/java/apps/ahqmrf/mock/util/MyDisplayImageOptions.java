package apps.ahqmrf.mock.util;

/**
 * Created by bsse0 on 5/1/2017.
 */

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import apps.ahqmrf.mock.R;

/**
 * Created by Lenovo on 3/8/2017.
 */

public class MyDisplayImageOptions {
    private DisplayImageOptions displayImageOptions;
    private static MyDisplayImageOptions mInstance;

    private MyDisplayImageOptions() {
        displayImageOptions = getDisplayImageOptions();
    }

    public static synchronized MyDisplayImageOptions getInstance() {
        if(mInstance == null) {
            mInstance = new MyDisplayImageOptions();
        }
        return mInstance;
    }

    public DisplayImageOptions getDisplayImageOptions() {
        if(displayImageOptions == null) {
            displayImageOptions = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.drawable.account_placeholder)
                    .showImageOnFail(R.drawable.account_placeholder)
                    .showImageOnLoading(R.drawable.account_placeholder)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .resetViewBeforeLoading(true)
                    .build();
        }
        return displayImageOptions;
    }
}