package com.webauth.webid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aitorvs.android.fingerlock.FingerLock;
import com.aitorvs.android.fingerlock.FingerLockManager;
import com.aitorvs.android.fingerlock.FingerLockResultCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.webauth.webid.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.webauth.webid.Activity.MainActivity.FINGER_TAG;

public class SignInActivity extends AppCompatActivity implements FingerLockResultCallback {

    @BindView(R.id.finger)
    ImageView finger;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.divider)
    View divider;
    @BindView(R.id.password_sign_in)
    TextInputEditText passwordSignIn;
    @BindView(R.id.go)
    ImageButton go;
    @BindView(R.id.sign_in_pass)
    LinearLayout signInPass;

    private FingerLockManager fingerLockManager;
    private static final String KEY_NAME = "Fingerprint";
    private static final int PERMISSION_REQUEST_CODE = 2;

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        activity = this;
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            launchActivity(MainActivity.class);
        }
        checkPermissions();
        fingerLockManager = FingerLock.initialize(this, KEY_NAME);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = passwordSignIn.getEditableText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("password",MODE_PRIVATE);
                String pwd = sharedPreferences.getString("pwd","");
                if(pwd.equals(pass)){
                    launchActivity(NavigationTabLayout.class);
                }
                else {
                    Toast.makeText(activity, "Incorrect Password", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void checkPermissions() {
        if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.USE_FINGERPRINT}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onFingerLockError(int errorType, Exception e) {

        switch (errorType) {
            case FingerLock.FINGERPRINT_PERMISSION_DENIED:
                // USE_PERMISSION is denied by the user, fallback to password authentication
                Log.v(FINGER_TAG, "permission denied");
                break;
            case FingerLock.FINGERPRINT_ERROR_HELP:
                // there's some kind of recoverable error that can be solved. Call e.getMessage()
                // to get help about the error
                Log.v(FINGER_TAG, "error_help");
                break;
            case FingerLock.FINGERPRINT_NOT_RECOGNIZED:
                // The fingerprint was not recognized, try another one
                Log.v(FINGER_TAG, "not recognized");
                break;
            case FingerLock.FINGERPRINT_NOT_SUPPORTED:
                // Fingerprint authentication is not supported by the device. Fallback to password
                // authentication
                Log.v(FINGER_TAG, "not supported");
                break;
            case FingerLock.FINGERPRINT_REGISTRATION_NEEDED:
                // There are no fingerprints registered in this device.
                // Go to Settings -> Security -> Fingerprint and register at least one
                Log.v(FINGER_TAG, "registration needed");
                break;
            case FingerLock.FINGERPRINT_UNRECOVERABLE_ERROR:
                // Unrecoverable internal error occurred. Unregister and register back
                Log.v(FINGER_TAG, "unrecoverable error");
                break;
        }
    }

    @Override
    public void onFingerLockAuthenticationSucceeded() {
        Toast.makeText(activity, "Finger Print Authenticated", Toast.LENGTH_SHORT).show();
        launchActivity(NavigationTabLayout.class);
    }

    @Override
    public void onFingerLockReady() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            fingerLockManager.start();
//            launchActivity(ScanActivity.class);
        }
        Log.v(FINGER_TAG, "Lock Ready");
    }

    @Override
    public void onFingerLockScanning(boolean b) {

        if (b) {
            Log.v(FINGER_TAG, "onFingerLockScanning");
        }
    }

    void launchActivity(Class<?> intentClass) {
        Intent intent = new Intent(activity, intentClass);
        startActivity(intent);
        finish();
    }

}