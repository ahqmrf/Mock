package apps.ahqmrf.mock.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.User;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends AppCompatActivity {

    @Nullable
    @BindView(R.id.app_toolbar)     Toolbar  toolbar;
    @BindView(R.id.input_email)     EditText mInputEmail;
    @BindView(R.id.input_password)  EditText mInputPassword;
    @BindView(R.id.text_register)   TextView mRegister;
    @BindView(R.id.progressBar) View     progressLayout;
    @BindView(R.id.error_email)     TextView emailError;
    @BindView(R.id.error_password)  TextView passwordError;

    @BindString(R.string.title_sign_in) String title;
    @BindString(R.string.error_signin)  String errorSigningIn;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if (Utility.getBoolean(this, Const.Keys.LOGGED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MyLocationActivity.class);
            startActivity(intent);
            finish();
        }
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
    public void onSignInClick() {
        progressLayout.setVisibility(View.VISIBLE);
        validateForm();
    }

    private void validateForm() {
        boolean allValid = true;
        clearErrors();
        email = mInputEmail.getText().toString();
        password = mInputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailError.setText(R.string.error_label_empty_field);
            emailError.setVisibility(View.VISIBLE);
            allValid = false;
        }

        if (!TextUtils.isEmpty(email) && !Utility.isValidEmail(email)) {
            emailError.setText(R.string.error_label_invalid_email);
            emailError.setVisibility(View.VISIBLE);
            allValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordError.setText(R.string.error_label_empty_field);
            passwordError.setVisibility(View.VISIBLE);
            allValid = false;
        }

        if(allValid) signIn();
        else progressLayout.setVisibility(View.GONE);
    }


    private void clearErrors() {
        emailError.setVisibility(View.GONE);
        passwordError.setVisibility(View.GONE);
    }

    public void signIn() {
        FirebaseAuth.getInstance().
                signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            findUser();
                        } else {
                            progressLayout.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), errorSigningIn, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void findUser() {
        FirebaseDatabase.getInstance().getReference(Const.Route.USER_REF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                findUser((Map<String,Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void findUser(Map<String, Object> value) {
        for (Map.Entry<String, Object> entry : value.entrySet()){

            //Get user map
            Map singleUser = (Map) entry.getValue();
            if(singleUser.get(Const.Keys.EMAIL).equals(email)) {
                markAsLoggedIn(singleUser);
            }
        }

    }

    private void markAsLoggedIn(Map user) {
        Utility.putString(this, Const.Keys.NAME, (String) user.get(Const.Keys.NAME));
        Utility.putString(this, Const.Keys.USERNAME, (String) user.get(Const.Keys.USERNAME));
        Utility.putString(this, Const.Keys.EMAIL, (String) user.get(Const.Keys.EMAIL));
        Utility.putBoolean(this, Const.Keys.LOGGED_IN, true);
        progressLayout.setVisibility(View.GONE);
        Intent intent = new Intent(getApplicationContext(), MyLocationActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @OnClick(R.id.text_register)
    public void openRegisterPage() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
