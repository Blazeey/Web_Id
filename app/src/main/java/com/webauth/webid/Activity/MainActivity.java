package com.webauth.webid.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aitorvs.android.fingerlock.FingerLock;
import com.aitorvs.android.fingerlock.FingerLockManager;
import com.aitorvs.android.fingerlock.FingerLockResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.webauth.webid.R;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import online.devliving.passcodeview.PasscodeView;

public class MainActivity extends AppCompatActivity implements FingerLockResultCallback {

    Activity activity;
    public static final String TAG = "PHONE LOGIN";
    private static final int PERMISSION_REQUEST_CODE = 2;
    private static final String KEY_NAME = "Fingerprint";
    public static final String FINGER_TAG = "FINGERPRINT";

    @BindView(R.id.messenger_text)
    TextView messengerText;
    @BindView(R.id.ph_no_text)
    TextView phNoText;
    @BindView(R.id.phone_number_sign_up)
    TextInputEditText phoneNumberSignUp;
    @BindView(R.id.sign_up_options)
    LinearLayout signUpOptions;
    @BindView(R.id.otp)
    PasscodeView otp;
    @BindView(R.id.confirm_otp)
    ImageButton confirmOtp;
    @BindView(R.id.otp_options)
    LinearLayout otpOptions;
    @BindView(R.id.send_otp)
    Button sendOtp;


    private String mVerificationId;

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth firebaseAuth;
    public FirebaseAuth.AuthStateListener listener;
    private PhoneAuthProvider phoneAuthProvider;
    private FingerLockManager fingerLockManager;


    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            Log.d(TAG, "onVerificationCompleted:" + credential);
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(activity, "Verification Failure", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "onVerificationFailed", e);
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
//                mPhoneNumberField.setError("Invalid phone number.");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                        Snackbar.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken token) {
            Log.d(TAG, "onCodeSent:" + verificationId);
            Toast.makeText(activity, "OTP Sent", Toast.LENGTH_SHORT).show();
            otpOptions.setVisibility(View.VISIBLE);
            mVerificationId = verificationId;
            mResendToken = token;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        activity = this;
        if (firebaseAuth == null)
            Log.v("Auth", "null");
        ButterKnife.bind(this);
        phoneAuthProvider = PhoneAuthProvider.getInstance();
        fingerLockManager = FingerLock.initialize(this, KEY_NAME);

        checkPermissions();

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.v("USER", "No user");
                }

            }
        };
        sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.v(TAG, phoneNumberSignUp.getEditableText().toString());
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumberSignUp.getEditableText().toString(),        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        activity,               // Activity (for callback binding)
                        mCallbacks);        // OnVerificationStateChangedCallbacks

                otpOptions.setVisibility(View.VISIBLE);

            }
        });

        confirmOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oneTimePass = otp.getText().toString();
                String phone = phoneNumberSignUp.getEditableText().toString();

                PhoneAuthCredential credential;
                if (!oneTimePass.equals("")) {
                    credential = PhoneAuthProvider.getCredential(mVerificationId, oneTimePass);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(activity, "Success", Toast.LENGTH_SHORT).show();

                            launchActivity(RegistrationActivity.class);

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(activity, "Authenticate Failure", Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    void launchActivity(Class<?> intentClass) {
        Intent intent = new Intent(activity, intentClass);
        startActivity(intent);
        finish();
    }

    void checkPermissions() {
        if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.USE_FINGERPRINT}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
        fingerLockManager.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null)
            firebaseAuth.removeAuthStateListener(listener);

        fingerLockManager.stop();

    }

    @Override
    public void finish() {
        super.finish();
        if (listener != null)
            firebaseAuth.removeAuthStateListener(listener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FullScreencall();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Got The permissions!", Toast.LENGTH_SHORT).show();
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

    public void FullScreencall() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

}