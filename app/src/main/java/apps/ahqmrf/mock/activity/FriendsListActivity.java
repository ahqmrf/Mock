package apps.ahqmrf.mock.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import apps.ahqmrf.mock.BaseActivity;
import apps.ahqmrf.mock.R;
import butterknife.BindString;
import butterknife.ButterKnife;

public class FriendsListActivity extends BaseActivity {
    @BindString(R.string.title_my_friends)
    String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
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

    }
}
