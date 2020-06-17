package com.saurabh.snapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saurabh.snapp.R;
import com.saurabh.snapp.Story.StoryAdapter;
import com.saurabh.snapp.Story.StoryObject;
import com.saurabh.snapp.UserInformation;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StoryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Button mRefresh;

    public static StoryFragment newInstance(){
        StoryFragment fragment = new StoryFragment();
        return fragment;
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new StoryAdapter(getDataSet(),getContext());
        mRecyclerView.setAdapter(mAdapter);

        mRefresh = view.findViewById(R.id.refresh);
        mRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
                listenForData();
            }
        });

        return view;
    }

    private void clear() {
        int size = this.results.size();
        this.results.clear();
        mAdapter.notifyItemRangeChanged(0,size);
    }

    private ArrayList<StoryObject> results = new ArrayList<>();
    private ArrayList<StoryObject> getDataSet() {
        listenForData();
        return results;
    }

    private void listenForData() {
        for(int i=0;i< UserInformation.listFollowing.size();i++)
        {
            DatabaseReference followingUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(UserInformation.listFollowing.get(i));
            followingUserDB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String email = dataSnapshot.child("Email").getValue().toString();
                    String uid = dataSnapshot.getRef().getKey();
                    long timeStampBegin = 0;
                    long timeStampEnd = 0;
                    for(DataSnapshot storySnapshot : dataSnapshot.child("Story").getChildren())
                    {
                        if(storySnapshot.child("Time Stamp Begin").getValue()!=null)
                            timeStampBegin = Long.parseLong(storySnapshot.child("Time Stamp Begin").getValue().toString());
                        if(storySnapshot.child("Time Stamp End").getValue()!=null)
                            timeStampEnd = Long.parseLong(storySnapshot.child("Time Stamp End").getValue().toString());
                        long timeStampCurrent = System.currentTimeMillis();
                        if(timeStampBegin <= timeStampCurrent && timeStampCurrent <= timeStampEnd)
                        {
                            StoryObject obj = new StoryObject(uid,email,"story");
                            if(!results.contains(obj))
                            {
                                results.add(obj);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}