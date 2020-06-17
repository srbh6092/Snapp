package com.saurabh.snapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.saurabh.snapp.Receiver.ReceiverAdapter;
import com.saurabh.snapp.Receiver.ReceiverObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseReceiverActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Bitmap bitmap;
    String Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_receiver);

        try {
            bitmap = BitmapFactory.decodeStream(getApplication().openFileInput("imageToSend"));//Importing the captured image in bitmap for uploading
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Uid = FirebaseAuth.getInstance().getUid();

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getApplication());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReceiverAdapter(getDataSet(),getApplication());
        mRecyclerView.setAdapter(mAdapter);

        Button mSend = findViewById(R.id.send);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToStories();
            }
        });

    }

    private ArrayList<ReceiverObject> results = new ArrayList<>();
    private ArrayList<ReceiverObject> getDataSet() {
        listenForData();
        return results;
    }

    private void listenForData() {
        for(int i =0;i<UserInformation.listFollowing.size();i++)
        {
            DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference().child("Users").child(UserInformation.listFollowing.get(i));
            usersDB.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String email = "";
                    String uid = dataSnapshot.getRef().getKey();
                    if (dataSnapshot.child("Email").getValue()!=null)
                        email = dataSnapshot.child("Email").getValue().toString();
                    ReceiverObject obj = new ReceiverObject(uid,email,false);
                    if(!results.contains(obj))
                    {
                        results.add(obj);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void saveToStories() {
        final DatabaseReference userStoryDB = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid).child("Story");//Creating database path for putting the image in it
        final String key = userStoryDB.push().getKey();//Creating a unique key
        final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Captures").child(key);//Creating storage path for the Captured Image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//Initialising the Byte Array Output Stream
        bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);//Compressing the bitmap image into byte array through a stream
        byte[] dataToUpload = baos.toByteArray();//Converting the data in the stream into Byte Array
        UploadTask uploadTask = filepath.putBytes(dataToUpload);//Uploading image to the storage
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Long currentTimeStamp = System.currentTimeMillis();//Getting current time
                        Long endTimeStamp = currentTimeStamp+(24*60*60*1000);

                        CheckBox mStory = findViewById(R.id.checkBox);
                        if(mStory.isChecked())
                        {
                            Map<String,Object> userInfo= new HashMap<>();
                            userInfo.put("Image URL",uri.toString());//Saving URL to HashMap
                            userInfo.put("Time Stamp Begin",currentTimeStamp);//Saving upload time to HashMap
                            userInfo.put("Time Stamp End",endTimeStamp);//Saving remove time to HashMap
                            userStoryDB.child(key).setValue(userInfo);//Uploading HashMap to the Database
                        }
                        for(int i=0;i<results.size();i++)
                        {
                            if(results.get(i).getReceive())
                            {
                                DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("Users").child(results.get(i).getUid()).child("Received").child(Uid);

                                Map<String,Object> userInfo= new HashMap<>();
                                userInfo.put("Image URL",uri.toString());//Saving URL to HashMap
                                userInfo.put("Time Stamp Begin",currentTimeStamp);//Saving upload time to HashMap
                                userInfo.put("Time Stamp End",endTimeStamp);//Saving remove time to HashMap
                                userDB.child(key).setValue(userInfo);//Uploading HashMap to the Database
                            }
                        }
                    }
                });
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return;
            }
        });
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                finish();
                return;
            }
        });
    }
}
