package apps.ahqmrf.mock.activity;

import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;

import apps.ahqmrf.mock.BaseActivity;
import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.User;
import apps.ahqmrf.mock.util.Const;
import butterknife.ButterKnife;

public class UserActivity extends BaseActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String thisUserName;
    private String thisUserFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);
        User user = getIntent().getParcelableExtra(Const.Keys.USER);
        thisUserName = user.getUsername();
        thisUserFullName = user.getFullName();
        setToolbarWithBackArrow();
    }

    @Override
    protected void setToolbarTitle() {
        setTitle(thisUserFullName);
    }

    @Override
    protected void onViewCreated() {

    }
}
