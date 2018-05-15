package com.webauth.webid.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.webauth.webid.R;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by venki on 1/3/18.
 */

public class AccountFragment extends Fragment {

    @BindView(R.id.first_name)
    MaterialEditText firstName;
    @BindView(R.id.edit_f_name)
    FancyButton editFName;
    @BindView(R.id.last_name)
    MaterialEditText lastName;
    @BindView(R.id.edit_l_name)
    FancyButton editLName;
    @BindView(R.id.email_name)
    MaterialEditText emailName;
    @BindView(R.id.edit_email)
    FancyButton editEmail;
    @BindView(R.id.address)
    MaterialEditText address;
    @BindView(R.id.edit_address)
    FancyButton editAddress;
    @BindView(R.id.dob)
    TextView dob;

    @BindView(R.id.edit_dob)
    FloatingActionButton editDob;
    Unbinder unbinder;

    private FirebaseDatabase database;
    private DatabaseReference db;
    private FirebaseAuth firebaseAuth;
    private Map<String,String> userDetails;

    public AccountFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();
        db = database.getReference("Users");
        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                userDetails = new HashMap<>();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){

                    if(snapshot.getKey().equals(FirebaseAuth.getInstance().getUid())){
                        for(DataSnapshot ds:snapshot.getChildren())
                            userDetails.put(ds.getKey(), String.valueOf(ds.getValue()));

                    }
                }
                showDetails();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editFName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getTag()=="true"){
                    if(!firstName.getText().equals(""))
                    db.child(firebaseAuth.getUid()).child("first_name").setValue(firstName.getText().toString());
                    v.setTag("false");
                    firstName.setEnabled(false);
                    editFName.setText("Edit");
                }else {
                    v.setTag("true");
                    firstName.setEnabled(true);
                    editFName.setText("Update");
                }
            }
        });

        editLName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getTag()=="true"){
                    if(!lastName.getText().equals(""))
                    db.child(firebaseAuth.getUid()).child("last_name").setValue(lastName.getText().toString());
                    v.setTag("false");
                    lastName.setEnabled(false);
                    editLName.setText("Edit");
                }else {
                    v.setTag("true");
                    lastName.setEnabled(true);
                    editLName.setText("Update");
                }
            }
        });

        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getTag()=="true"){
                    if(!emailName.getText().equals(""))
                    db.child(firebaseAuth.getUid()).child("email").setValue(emailName.getText().toString());
                    v.setTag("false");
                    emailName.setEnabled(false);
                    editEmail.setText("Edit");
                }else {
                    v.setTag("true");
                    emailName.setEnabled(true);
                    editEmail.setText("Update");
                }
            }
        });

        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v.getTag()=="true"){
                    if(!address.getText().equals(""))
                    db.child(firebaseAuth.getUid()).child("address").setValue(address.getText().toString());
                    v.setTag("false");
                    address.setEnabled(false);
                    editAddress.setText("Edit");
                }else {
                    v.setTag("true");
                    address.setEnabled(true);
                    editAddress.setText("Update");
                }
            }
        });

        editDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.setTag("true");
                dob.setEnabled(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    void showDetails(){
        firstName.setText(userDetails.get("first_name"));
        Log.v("firstName",userDetails.get("first_name"));
        lastName.setText(userDetails.get("last_name"));
        Log.v("last_name",userDetails.get("last_name"));
        emailName.setText(userDetails.get("email"));
        address.setText(userDetails.get("address"));
        dob.setText(userDetails.get("dob"));
    }
}
