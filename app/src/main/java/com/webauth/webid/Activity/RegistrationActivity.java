package com.webauth.webid.Activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.webauth.webid.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by venki on 28/2/18.
 */

public class RegistrationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    public static final String TAG = "PHONE LOGIN";
    private static final int ZXING_CAMERA_PERMISSION = 1;
    private static final String KEY_NAME = "Fingerprint";
    public static final String FINGER_TAG = "FINGERPRINT";


    @BindView(R.id.first_name)
    TextInputEditText firstName;
    @BindView(R.id.last_name)
    TextInputEditText lastName;
    @BindView(R.id.email)
    TextInputEditText email;
    @BindView(R.id.address)
    TextInputEditText address;
    @BindView(R.id.submit)
    ImageButton submit;
    @BindView(R.id.dob_fab)
    FloatingActionButton dobFab;
    @BindView(R.id.password)
    TextInputEditText password;

    private Activity activity;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private DatePickerDialog datePickerDialog;
    private int day, month, year;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        activity = this;
        onDateSetListener = this;

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Users");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> userDetails = new HashMap<>();

                userDetails.put("first_name", firstName.getEditableText().toString());
                userDetails.put("last_name", lastName.getEditableText().toString());
                userDetails.put("email", email.getEditableText().toString());
                userDetails.put("address", address.getEditableText().toString());
                userDetails.put("phone", user.getPhoneNumber());
                userDetails.put("dob", dateParse(year, month, day));

                reference.child(user.getUid()).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(activity, "Registered", Toast.LENGTH_SHORT).show();
                        launchActivity(NavigationTabLayout.class);
                    }
                });

                firebaseDatabase.getReference("Login").child(user.getUid()).setValue(password.getEditableText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        SharedPreferences sharedPreferences = getSharedPreferences("password",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("pwd",password.getEditableText().toString());
                        editor.apply();

                    }
                });

            }
        });

        dobFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(activity, onDateSetListener, 2018, 1, 23);
                datePickerDialog.show();
            }
        });

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.day = dayOfMonth;
        this.year = year;
        this.month = month;
    }

    String dateParse(int year, int month, int day) {

        String mon;
        switch (month) {
            case Calendar.JANUARY:
                mon = "01";
                break;

            case Calendar.FEBRUARY:
                mon = "02";
                break;

            case Calendar.MARCH:
                mon = "03";
                break;

            case Calendar.APRIL:
                mon = "04";
                break;

            case Calendar.MAY:
                mon = "05";
                break;

            case Calendar.JUNE:
                mon = "06";
                break;

            case Calendar.JULY:
                mon = "07";
                break;

            case Calendar.AUGUST:
                mon = "08";
                break;

            case Calendar.SEPTEMBER:
                mon = "09";
                break;

            case Calendar.OCTOBER:
                mon = "10";
                break;

            case Calendar.NOVEMBER:
                mon = "11";
                break;

            case Calendar.DECEMBER:
                mon = "12";
                break;

        }
        return day + " " + month + " " + year;
    }

    void launchActivity(Class<?> intentClass) {
        Intent intent = new Intent(activity, intentClass);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FullScreencall();
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

