package apps.ahqmrf.mock.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity implements View.OnFocusChangeListener {

    @BindView(R.id.app_toolbar) Toolbar toolbar;

    @BindView(R.id.progressBar)            View     progressLayout;
    @BindView(R.id.input_full_name)        EditText fullNameInput;
    @BindView(R.id.input_username)         EditText usernameInput;
    @BindView(R.id.input_email)            EditText emailInput;
    @BindView(R.id.input_password)         EditText passwordInput;
    @BindView(R.id.input_confirm_password) EditText confirmPasswordInput;
    @BindView(R.id.input_path)             EditText pathInput;

    @BindView(R.id.error_full_name)         TextView fullNameError;
    @BindView(R.id.error_username)          TextView usernameError;
    @BindView(R.id.error_email)             TextView emailError;
    @BindView(R.id.error_password)          TextView passwordError;
    @BindView(R.id.error_password_mismatch) TextView passwordMismatchError;
    @BindView(R.id.error_invalid_path)      TextView pathError;

    @BindString(R.string.title_register)  String title;
    @BindString(R.string.error_reg)       String errorRegister;
    @BindString(R.string.msg_reg_success) String successRegister;

    private String  fullName;
    private String  username;
    private String  email;
    private String  password;
    private String filePath;
    private boolean allValid;
    private Uri uri;
    private DatabaseReference refUser = FirebaseDatabase.getInstance().getReference(Const.Route.USER_REF);
    private StorageReference mStorage = FirebaseStorage.getInstance().getReference();

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
        pathInput.setOnFocusChangeListener(this);
    }

    private void clearErrors() {
        fullNameError.setVisibility(View.GONE);
        usernameError.setVisibility(View.GONE);
        emailError.setVisibility(View.GONE);
        passwordError.setVisibility(View.GONE);
        passwordMismatchError.setVisibility(View.GONE);
        pathError.setVisibility(View.GONE);
    }

    private void setDefaultBorder() {
        fullNameInput.setBackgroundResource(R.drawable.edittext_grey_border);
        usernameInput.setBackgroundResource(R.drawable.edittext_grey_border);
        emailInput.setBackgroundResource(R.drawable.edittext_grey_border);
        passwordInput.setBackgroundResource(R.drawable.edittext_grey_border);
        confirmPasswordInput.setBackgroundResource(R.drawable.edittext_grey_border);
        pathInput.setBackgroundResource(R.drawable.edittext_grey_border);
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

        if (!Utility.isValidUsername(username)) {
            usernameError.setText(R.string.error_invalid_username);
            usernameError.setVisibility(View.VISIBLE);
            allValid = false;
        }

        if (!TextUtils.isEmpty(username) && Utility.isValidUsername(username)) {
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
        filePath = pathInput.getText().toString();

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

        if(!TextUtils.isEmpty(filePath)) {
            File file = new File(filePath);
            if(!file.exists()) {
                pathError.setVisibility(View.VISIBLE);
                allValid = false;
            }
        }
        if (allValid) uploadImage();
    }

    private void register(final String imageUrl) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressLayout.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    createUser(imageUrl);
                } else {
                    Utility.showToast(getApplicationContext(), errorRegister);
                }
            }
        });
    }

    private void createUser(String imageUrl) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference(Const.Route.USER_REF).child(username);
        database.child(Const.Keys.NAME).setValue(fullName);
        database.child(Const.Keys.USERNAME).setValue(username);
        database.child(Const.Keys.EMAIL).setValue(email);
        if(TextUtils.isEmpty(imageUrl)) imageUrl = "";
        database.child(Const.Keys.PROFILE_PIC).setValue(imageUrl);
        Utility.showToast(this, successRegister);
        Utility.put(this, Const.Keys.NAME, fullName);
        Utility.put(this, Const.Keys.USERNAME, username);
        Utility.put(this, Const.Keys.EMAIL, email);
        Utility.put(this, Const.Keys.PROFILE_PIC, imageUrl);
        Utility.put(this, Const.Keys.LOGGED_IN, true);
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

    @OnClick(R.id.button_choose)
    public void checkReadExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Const.RequestCodes.READ_EXTERNAL_STORAGE);
                }
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Const.RequestCodes.READ_EXTERNAL_STORAGE);
            }
        } else {

            openImageGallery();
        }
    }

    private void openImageGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        if (getPackageManager().resolveActivity(intent, 0) != null) {
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), Const.RequestCodes.REQUEST_BROWSE_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Const.RequestCodes.READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageGallery();

                } else {
                    Utility.showToast(this, "Permission required to select media");
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Const.RequestCodes.REQUEST_BROWSE_GALLERY && resultCode == RESULT_OK) {
            uri = data.getData();
            if (null != uri) {
                filePath = Utility.getFileUrl(this, uri);
                pathInput.setText(filePath);
            }
        }
    }

    private void uploadImage() {
        if(TextUtils.isEmpty(filePath)) {
            progressLayout.setVisibility(View.VISIBLE);
            register("");
            return;
        }
        File file = new File(filePath);
        double length = file.length() / 1024;
        if(length > 500) {
            Utility.showToast(this, "File size must be less than 500 kB");
            return;
        }
        progressLayout.setVisibility(View.VISIBLE);
        final StorageReference photoStorage = mStorage.child(username).child(Const.Keys.PROFILE_PIC).child(uri.getLastPathSegment());
        photoStorage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                photoStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Utility.showToast(getApplicationContext(), "Uploaded successfully");
                        register(uri.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showToast(getApplicationContext(), "Failed to upload photo");
                register("");
            }
        });
    }
}
