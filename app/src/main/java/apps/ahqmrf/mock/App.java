package apps.ahqmrf.mock;

import android.app.Application;
import android.content.Intent;

import com.binjar.prefsdroid.Preference;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import apps.ahqmrf.mock.service.LocationUpdateService;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;

/**
 * Created by bsse0 on 5/1/2017.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
        Preference.load().using(this).with(Const.PREF_NAME).prepare();
        boolean serviceStarted = Utility.getBoolean(this, Const.Keys.SERVICE_STARTED);
        if(!serviceStarted) {
            Utility.put(this, Const.Keys.SERVICE_STARTED, true);
            startService(new Intent(this, LocationUpdateService.class));
        }
    }
}
