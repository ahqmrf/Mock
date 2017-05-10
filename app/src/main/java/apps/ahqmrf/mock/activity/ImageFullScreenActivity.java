package apps.ahqmrf.mock.activity;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.MyDisplayImageOptions;


public class ImageFullScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView    mImageView;
    private ImageView    mBackImage;
    private ImageView    mMore;
    private String       imageUri;
    private LinearLayout linearLayout;
    private Bitmap       mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mMore = (ImageView) findViewById(R.id.image_more);
        mImageView = (ImageView) findViewById(R.id.image_full_size);
        mBackImage = (ImageView) findViewById(R.id.image_back);
        imageUri = getIntent().getStringExtra(Const.Keys.IMAGE_URL);

        linearLayout = (LinearLayout) findViewById(R.id.linear_progressbar);

        setValuesIntoViews();
    }

    private void setValuesIntoViews() {

        ImageLoader.getInstance().displayImage(
                imageUri,
                mImageView,
                MyDisplayImageOptions.getInstance().getDisplayImageOptions(), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        linearLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        linearLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        linearLayout.setVisibility(View.GONE);
                        mBitmap = loadedImage;
                    }
                });

        mBackImage.setOnClickListener(this);
        mMore.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.image_more:
                showPopup();
                break;
        }
    }

    private void showPopup() {
        PopupMenu popupMenu = new PopupMenu(this, mMore);
        popupMenu.inflate(R.menu.image_context_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.rotate_left:
                        rotateLeft();
                        break;
                    case R.id.rotate_right:
                        rotateRight();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void rotateRight() {
        if (mBitmap != null) {
            mBitmap = rotateBitmap(90, mBitmap);
            mImageView.setImageBitmap(mBitmap);
        }
    }

    private void rotateLeft() {
        if (mBitmap != null) {
            mBitmap = rotateBitmap(-90, mBitmap);
            mImageView.setImageBitmap(mBitmap);
        }
    }


    public Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        if (angle != 0) {
            Bitmap oldBitmap = bitmap;

            Matrix matrix = new Matrix();
            matrix.postRotate(angle);

            bitmap = Bitmap.createBitmap(
                    oldBitmap, 0, 0, oldBitmap.getWidth(), oldBitmap.getHeight(), matrix, false
            );

            oldBitmap.recycle();
        }
        return bitmap;
    }
}
