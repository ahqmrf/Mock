package apps.ahqmrf.mock.activity;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import apps.ahqmrf.mock.R;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.app_toolbar)          Toolbar toolbar;
    @BindString(R.string.title_register) String  title;
    @BindView(R.id.spinner)              Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setTitle(title);
        }

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.security_questions, R.layout.spinner_text);
        spinner.setAdapter(adapter);
    }
}
