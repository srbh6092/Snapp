package com.saurabh.snapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.saurabh.snapp.Follow.FollowAdapter;
import com.saurabh.snapp.Follow.FollowObject;

import java.util.ArrayList;

public class FindUsersActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    EditText mEmailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_users);

        mEmailInput = (EditText)findViewById(R.id.emailInput);
        Button mSearch = (Button)findViewById(R.id.search);

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FollowAdapter(getDataSet(),getApplication());
        mRecyclerView.setAdapter(mAdapter);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
                listenForData();
            }

        });

    }

    private void listenForData() {
        DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference().child("Users");
        Query query = usersDB.orderByChild("Email").startAt(mEmailInput.getText().toString()).endAt((mEmailInput.getText().toString())+"\uf8ff");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String email = "";
                String uid = dataSnapshot.getRef().getKey();
                if (dataSnapshot.child("Email").getValue()!=null)
                    email = dataSnapshot.child("Email").getValue().toString();
                if(!email.equals(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                {
                    FollowObject obj = new FollowObject(uid,email);
                    results.add(obj);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void clear() {
        int size = this.results.size();
        this.results.clear();
        mAdapter.notifyItemRangeChanged(0,size);
    }

    private ArrayList<FollowObject> results = new ArrayList<>();
    private ArrayList<FollowObject> getDataSet() {
        listenForData();
        return results;
    }
}
