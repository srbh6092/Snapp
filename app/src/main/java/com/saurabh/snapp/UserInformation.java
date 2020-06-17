package com.saurabh.snapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserInformation {
    public static ArrayList<String> listFollowing = new ArrayList<>();

    public void startFetching(){
        listFollowing.clear();
        getUserFollowing();
    }

    private void getUserFollowing() {
        DatabaseReference userFollowingDB = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Following");
        userFollowingDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists())
                {
                    String uid = dataSnapshot.getRef().getKey();
                    if(uid!=null && !listFollowing.contains(uid))
                        listFollowing.add(uid);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String uid = dataSnapshot.getRef().getKey();
                    if(uid!=null)
                        listFollowing.remove(uid);
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
