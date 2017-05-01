package apps.ahqmrf.mock.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import apps.ahqmrf.mock.BaseActivity;
import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.MyDisplayImageOptions;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity {
    @BindString(R.string.title_settings)
    String title;

    @BindView(R.id.image_main)      ImageView mImageView;
    @BindView(R.id.text_name)       TextView  mName;
    @BindView(R.id.layout_progress) View      progressLayout;
    @BindView(R.id.text_email)      TextView  mEmail;
    @BindView(R.id.text_full_name)  TextView  mFullName;
    @BindView(R.id.text_username)   TextView  mUsername;

    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    private String username, imageUrl, fullName, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
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
        mEmail.setText(email);
        mFullName.setText(fullName);
        mUsername.setText(username);
        if (TextUtils.isEmpty(imageUrl)) {
            progressLayout.setVisibility(View.GONE);
        } else {
            StorageReference photoStorage = mStorage.child(username).child(Const.Keys.PROFILE_PIC).child(imageUrl);
            photoStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Utility.loadImage(uri.toString(), mImageView, progressLayout);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressLayout.setVisibility(View.GONE);
                }
            });
        }
    }
}
