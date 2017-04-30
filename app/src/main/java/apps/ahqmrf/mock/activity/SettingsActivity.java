package apps.ahqmrf.mock.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import apps.ahqmrf.mock.BaseActivity;
import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity {
    @BindString(R.string.title_settings)
    String title;

    @BindView(R.id.image_main) ImageView mImageView;
    @BindView(R.id.text_name) TextView mName;

    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private String username, imageUrl, fullName, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        mImageFriends.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary));
        setToolbarWithBackArrow();
    }

    @Override
    protected void setToolbarTitle() {
        setTitle(title);
    }

    @Override
    protected void onViewCreated() {
        imageUrl = Utility.getString(this, Const.Keys.PROFILE_PIC);
        username = Utility.getString(this, Const.Keys.USERNAME);
        fullName = Utility.getString(this, Const.Keys.NAME);
        email = Utility.getString(this, Const.Keys.EMAIL);

        mName.setText(fullName);

        StorageReference photoStorage = mStorage.child(username).child(Const.Keys.PROFILE_PIC).child(imageUrl);
        final long ONE_MEGABYTE = 1024 * 1024;
        photoStorage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mImageView.setImageBitmap(bitmap);
            }
        });
    }
}
