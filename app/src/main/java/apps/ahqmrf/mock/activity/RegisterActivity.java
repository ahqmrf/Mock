package apps.ahqmrf.mock.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.User;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    @BindView(R.id.app_toolbar) Toolbar toolbar;

    @BindView(R.id.layout_progress)        View     progressLayout;
    @BindView(R.id.input_full_name)        EditText fullNameInput;
    @BindView(R.id.input_username)         EditText usernameInput;
    @BindView(R.id.input_email)            EditText emailInput;
    @BindView(R.id.input_password)         EditText passwordInput;
    @BindView(R.id.input_confirm_password) EditText confirmPasswordInput;

    @BindView(R.id.error_full_name)         TextView fullNameError;
    @BindView(R.id.error_username)          TextView usernameError;
    @BindView(R.id.error_email)             TextView emailError;
    @BindView(R.id.error_password)          TextView passwordError;
    @BindView(R.id.error_password_mismatch) TextView passwordMismatchError;

    @BindString(R.string.title_register)  String title;
    @BindString(R.string.error_reg)       String errorRegister;
    @BindString(R.string.msg_reg_success) String successRegister;

    private String  fullName;
    private String  username;
    private String  email;
    private String  password;
    private boolean allValid;
    private DatabaseReference refUser = FirebaseDatabase.getInstance().getReference(Const.Route.USER_REF);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(title);
        }

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        onViewCreated();
    }

    private void onViewCreated() {
        setInputFields();
    }

    private void setInputFields() {
        fullNameInput.setOnFocusChangeListener(this);
        usernameInput.setOnFocusChangeListener(this);
        emailInput.setOnFocusChangeListener(this);
        passwordInput.setOnFocusChangeListener(this);
        confirmPasswordInput.setOnFocusChangeListener(this);
    }

    private void clearErrors() {
        fullNameError.setVisibility(View.GONE);
        usernameError.setVisibility(View.GONE);
        emailError.setVisibility(View.GONE);
        passwordError.setVisibility(View.GONE);
        passwordMismatchError.setVisibility(View.GONE);
    }

    private void setDefaultBorder() {
        fullNameInput.setBackgroundResource(R.drawable.edittext_grey_border);
        usernameInput.setBackgroundResource(R.drawable.edittext_grey_border);
        emailInput.setBackgroundResource(R.drawable.edittext_grey_border);
        passwordInput.setBackgroundResource(R.drawable.edittext_grey_border);
        confirmPasswordInput.setBackgroundResource(R.drawable.edittext_grey_border);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setDefaultBorder();
            v.setBackgroundResource(R.drawable.edittext_primary_border);
        }
    }

    @OnClick(R.id.button_reg)
    public void onRegisterClick() {
        progressLayout.setVisibility(View.VISIBLE);
        validateForm();
    }

    private void validateForm() {
        allValid = true;
        clearErrors();
        fullName = fullNameInput.getText().toString();
        username = usernameInput.getText().toString();

        if (TextUtils.isEmpty(fullName)) {
            fullNameError.setVisibility(View.VISIBLE);
            allValid = false;
        }

        if (TextUtils.isEmpty(username)) {
            usernameError.setText(R.string.error_label_empty_field);
            usernameError.setVisibility(View.VISIBLE);
            allValid = false;
        }

        if (!TextUtils.isEmpty(username)) {
            refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    progressLayout.setVisibility(View.GONE);
                    if (dataSnapshot.hasChild(username)) {
                        allValid = false;
                        usernameError.setText(R.string.error_label_username_exists);
                        usernameError.setVisibility(View.VISIBLE);
                    }
                    doTheRest();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressLayout.setVisibility(View.GONE);
                    doTheRest();
                }
            });
        } else {
            progressLayout.setVisibility(View.GONE);
            doTheRest();
        }
    }

    private void doTheRest() {
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

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

        if (!TextUtils.isEmpty(password) && password.length() < 6) {
            passwordError.setText(R.string.error_password);
            passwordError.setVisibility(View.VISIBLE);
            allValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            passwordMismatchError.setText(R.string.error_label_empty_field);
            passwordMismatchError.setVisibility(View.VISIBLE);
            allValid = false;
        }

        if (!TextUtils.isEmpty(confirmPassword) && !confirmPassword.equals(password)) {
            passwordMismatchError.setText(R.string.error_password_mismatch);
            passwordMismatchError.setVisibility(View.VISIBLE);
            allValid = false;
        }

        if (allValid) register();
    }

    private void register() {
        progressLayout.setVisibility(View.VISIBLE);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressLayout.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    createUser();
                } else {
                    Toast.makeText(getApplicationContext(), errorRegister, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createUser() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(Const.Route.USER_REF);
        Map<String, User> users = new HashMap<>();
        User user = new User(email, username, fullName);
        users.put(username, user);
        database.setValue(users);
        Utility.showToast(this, successRegister);
        Utility.putString(this, Const.Keys.NAME, user.getFullName());
        Utility.putString(this, Const.Keys.USERNAME, user.getUsername());
        Utility.putString(this, Const.Keys.EMAIL, user.getEmail());
        Utility.putBoolean(this, Const.Keys.LOGGED_IN, true);
        Intent intent = new Intent(getApplicationContext(), MyLocationActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
