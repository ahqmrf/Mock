package apps.ahqmrf.mock.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
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
    @BindView(R.id.layout_progress) View progressLayout;
    @BindView(R.id.text_email) TextView mEmail;
    @BindView(R.id.text_full_name) TextView mFullName;
    @BindView(R.id.text_username) TextView mUsername;

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

        StorageReference photoStorage = mStorage.child(username).child(Const.Keys.PROFILE_PIC).child(imageUrl);
        final long ONE_MEGABYTE = 1024 * 1024;
        photoStorage.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                mImageView.setImageBitmap(bitmap);
                progressLayout.setVisibility(View.GONE);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressLayout.setVisibility(View.GONE);
            }
        });
    }
}
