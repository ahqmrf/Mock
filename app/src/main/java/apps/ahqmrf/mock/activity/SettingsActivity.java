package apps.ahqmrf.mock.activity;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

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

    @BindView(R.id.image_main)      ImageView    mImageView;
    @BindView(R.id.text_name)       TextView     mName;
    @BindView(R.id.layout_progress) View         progressLayout;
    @BindView(R.id.text_email)      TextView     mEmail;
    @BindView(R.id.text_full_name)  TextView     mFullName;
    @BindView(R.id.text_username)   TextView     mUsername;
    @BindView(R.id.switchCompat)    SwitchCompat switchCompat;
    @BindView(R.id.switchVibrate) SwitchCompat switchVibrate;

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
            Utility.loadImage(imageUrl, mImageView, progressLayout);
        }

        if(Utility.getBoolean(this, Const.Keys.NOTIFICATION_MODE)) {
            switchCompat.setChecked(true);
            switchVibrate.setClickable(true);
        } else switchVibrate.setClickable(false);
        if(Utility.getBoolean(this, Const.Keys.VIBRATION)) {
            switchVibrate.setChecked(true);
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utility.put(getApplicationContext(), Const.Keys.NOTIFICATION_MODE, isChecked);
                if(!isChecked) {
                    switchVibrate.setChecked(false);
                    Utility.put(getApplicationContext(), Const.Keys.VIBRATION, false);
                    switchVibrate.setClickable(false);
                }
                else switchVibrate.setClickable(true);
            }
        });

        switchVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Utility.put(getApplicationContext(), Const.Keys.VIBRATION, isChecked);
            }
        });
    }
}
