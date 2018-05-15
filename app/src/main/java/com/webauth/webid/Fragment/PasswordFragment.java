package com.webauth.webid.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.webauth.webid.Adapter.DomainAdapter;
import com.webauth.webid.Models.Auth;
import com.webauth.webid.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by venki on 1/3/18.
 */

public class PasswordFragment extends Fragment {

    Unbinder unbinder;
    @BindView(R.id.add_password)
    FloatingActionButton addPassword;
    @BindView(R.id.password)
    TextView password;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.username)
    MaterialEditText username;
    @BindView(R.id.password_add)
    MaterialEditText passwordAdd;
    @BindView(R.id.container)
    LinearLayout container;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private List<Auth> authList = new ArrayList<>();
    private DomainAdapter domainAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("ExternalAuth").child("Domain").child(firebaseAuth.getUid());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        domainAdapter = new DomainAdapter(getContext(), authList);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(domainAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                authList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.getKey().equals("Facebook"))
                        for (DataSnapshot snap : snapshot.getChildren())
                            authList.add(new Auth("facebook", snap.getKey(), snap.getValue().toString()));
                }
                domainAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = username.getText().toString();
                String pass = passwordAdd.getText().toString();

                databaseReference.child("facebook").child(userName).setValue(pass);
                Toast.makeText(getContext(), "Credentials Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
