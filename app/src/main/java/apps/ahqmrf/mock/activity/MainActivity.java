package apps.ahqmrf.mock.activity;

import android.os.Bundle;

import apps.ahqmrf.mock.BaseActivity;
import apps.ahqmrf.mock.R;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setToolbarWithBackArrow();
    }

    @Override
    protected void setToolbarTitle() {
        setTitle("Home");
    }

    @Override
    protected void onViewCreated() {

    }
}
