package apps.ahqmrf.mock;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by bsse0 on 4/22/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.app_toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.image_my_location)
    protected ImageView mImageLocation;

    @OnClick(R.id.image_my_location)
    public void showMyLocation() {
        setBottomIconDefaultColor();
        mImageLocation.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @BindView(R.id.image_map)
    protected ImageView mImageMap;

    @OnClick(R.id.image_map)
    public void showMap() {
        setBottomIconDefaultColor();
        mImageMap.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @BindView(R.id.image_friends)
    protected ImageView mImageFriends;

    @OnClick(R.id.image_friends)
    public void showFriends() {
        setBottomIconDefaultColor();
        mImageFriends.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @BindView(R.id.image_notification)
    protected ImageView mImageNotification;

    @BindView(R.id.view_notification)
    protected View mViewNotification;

    @OnClick(R.id.view_notification)
    public void showNotification() {
        setBottomIconDefaultColor();
        mImageNotification.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    protected void setToolbarWithBackArrow() {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarTitle();
        onViewCreated();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void trigger(Class<?> activityClass) {
        if (!this.getClass().equals(activityClass)) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
        }
    }

    protected void setBottomIconDefaultColor() {
        mImageLocation.setColorFilter(ContextCompat.getColor(this, R.color.black));
        mImageMap.setColorFilter(ContextCompat.getColor(this, R.color.black));
        mImageFriends.setColorFilter(ContextCompat.getColor(this, R.color.black));
        mImageNotification.setColorFilter(ContextCompat.getColor(this, R.color.black));
    }

    protected abstract void setToolbarTitle();

    protected abstract void onViewCreated();

}
