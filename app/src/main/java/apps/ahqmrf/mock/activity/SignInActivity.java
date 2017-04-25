package apps.ahqmrf.mock.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import apps.ahqmrf.mock.R;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class SignInActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.app_toolbar)    Toolbar  toolbar;
    @BindView(R.id.input_email)    EditText mInputEmail;
    @BindView(R.id.input_password) EditText mInputPassword;
    @BindView(R.id.checkbox)       CheckBox mCheckBox;
    @BindView(R.id.text_register)  TextView mRegister;

    @BindString(R.string.title_sign_in) String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            setTitle(title);
        }

        setFocusForEditTexts();
        String text = "Don't have an account yet? <font color='blue'>Click here</font> to register.";
        mRegister.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
    }

    private void setFocusForEditTexts() {
        mInputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mInputPassword.setBackgroundResource(R.drawable.edittext_grey_border);
                    mInputEmail.setBackgroundResource(R.drawable.edittext_primary_border);
                }
            }
        });

        mInputPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mInputEmail.setBackgroundResource(R.drawable.edittext_grey_border);
                    mInputPassword.setBackgroundResource(R.drawable.edittext_primary_border);
                }
            }
        });
    }

    @OnClick(R.id.button_sign_in)
    public void signIn() {
        Intent intent = new Intent(this, MyLocationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
