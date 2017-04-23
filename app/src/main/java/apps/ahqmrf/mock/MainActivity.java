package apps.ahqmrf.mock;

import android.os.Bundle;

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
