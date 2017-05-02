package apps.ahqmrf.mock.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import apps.ahqmrf.mock.BaseActivity;
import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.User;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivity extends BaseActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String thisUserName;
    private String thisUserFullName;

    @BindView(R.id.text_full_name)
    TextView fullName;

    @BindView(R.id.text_username)
    TextView username;

    @BindView(R.id.image_main)
    ImageView imageView;

    @BindView(R.id.container)
    View rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
        setToolbarWithBackArrow();
        User user = getIntent().getParcelableExtra(Const.Keys.USER);
        thisUserName = user.getUsername();
        thisUserFullName = user.getFullName();

        username.setText(thisUserName);
        fullName.setText(thisUserFullName);
        if(!TextUtils.isEmpty(user.getImageUrl())) Utility.loadImage(user.getImageUrl(), imageView);
    }

    @Override
    protected void setToolbarTitle() {
        setTitle(thisUserFullName);
    }

    @Override
    protected void onViewCreated() {
        Snackbar.make(rootLayout, "You are not friends with " + thisUserFullName, Snackbar.LENGTH_SHORT).show();
    }
}
