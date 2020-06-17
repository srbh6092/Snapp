package com.saurabh.snapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DisplayImageActivity extends AppCompatActivity {

    String userId, chatOrStory;
    private ImageView mImage;
    private boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        Bundle b = getIntent().getExtras();
        userId=b.getString("userID");
        chatOrStory=b.getString("chatOrStory");
        mImage=(ImageView)findViewById(R.id.image);

        switch (chatOrStory){
            case "chat":
                listenForChat();
                break;
            case "story":
                listenForStory();
                break;
        }

    }
    ArrayList<String> imageUrlList = new ArrayList<>();

    private void listenForChat()
    {
        final DatabaseReference chatDB = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Received").child(userId);
        chatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageURL="";
                for(DataSnapshot chatSnapshot : dataSnapshot.getChildren())
                {
                    if(chatSnapshot.child("Image URL").getValue()!=null)
                        imageURL = chatSnapshot.child("Image URL").getValue().toString();
                    imageUrlList.add(imageURL);
                    if(!started)
                    {
                        started=true;
                        initializeDisplay();
                    }
                    chatDB.child(chatSnapshot.getKey()).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void listenForStory()
    {
        DatabaseReference followingUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        followingUserDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageURL="";
                long timeStampBegin = 0;
                long timeStampEnd = 0;
                for(DataSnapshot storySnapshot : dataSnapshot.child("Story").getChildren())
                {
                    if(storySnapshot.child("Time Stamp Begin").getValue()!=null)
                        timeStampBegin = Long.parseLong(storySnapshot.child("Time Stamp Begin").getValue().toString());
                    if(storySnapshot.child("Time Stamp End").getValue()!=null)
                        timeStampEnd = Long.parseLong(storySnapshot.child("Time Stamp End").getValue().toString());
                    if(storySnapshot.child("Image URL").getValue()!=null)
                        imageURL = storySnapshot.child("Image URL").getValue().toString();
                    long timeStampCurrent = System.currentTimeMillis();
                    if(timeStampBegin <= timeStampCurrent && timeStampCurrent <= timeStampEnd)
                    {
                        imageUrlList.add(imageURL);
                        if(!started)
                        {
                            started=true;
                            initializeDisplay();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private int imageIterator=0;
    private void initializeDisplay() {
        Glide.with(getApplication()).load(imageUrlList.get(imageIterator)).into(mImage);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeImage();
            }
        });
        final Handler handler = new Handler();
        final int delay=5000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeImage();
                handler.postDelayed(this,delay);
            }
        },delay);
    }

    private void changeImage() {
        if(imageIterator==imageUrlList.size()-1)
        {
            finish();
            return;
        }
        imageIterator++;
        Glide.with(getApplication()).load(imageUrlList.get(imageIterator)).into(mImage);
    }

}