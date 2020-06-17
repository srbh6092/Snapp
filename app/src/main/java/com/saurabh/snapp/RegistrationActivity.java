package com.saurabh.snapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private EditText mName, mEmail, mPassword, mConfirmPassword;
    private RadioGroup mRadioGroup;
    private Button mRegister;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null)
                {
                    Intent intent = new Intent(RegistrationActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mProfileImage = (ImageView)findViewById(R.id.profileImage);

        mName = (EditText) findViewById(R.id.name);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mConfirmPassword = (EditText) findViewById(R.id.confirmPassword);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });

        mRegister = (Button) findViewById(R.id.register);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final RadioButton mRadioButton = (RadioButton) findViewById(mRadioGroup.getCheckedRadioButtonId());
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                final String confirmPassword = mConfirmPassword.getText().toString();

                if(password.equals(confirmPassword) && resultUri!=null) {
                    if (mRadioButton.getText() == null)
                        return;

                    final String sex = mRadioButton.getText().toString();

                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful())
                                Toast.makeText(RegistrationActivity.this, "Registration Error!", Toast.LENGTH_SHORT).show();
                            else {
                                final String userID = mAuth.getCurrentUser().getUid();
                                DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                                Map userInfo = new HashMap<>();
                                userInfo.put("Name",name);
                                userInfo.put("Sex",sex);
                                userInfo.put("Email",email);
                                currentUserDB.updateChildren(userInfo);
                                StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Profile Images").child(userID);
                                Bitmap bitmap = null;

                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
                                byte[] data = baos.toByteArray();

                                UploadTask uploadTask = filepath.putBytes(data);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        finish();
                                    }
                                });
                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("Profile Images").child(userID);
                                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                final DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("Profile Image URL");
                                                currentUserDB.setValue(uri.toString());
                                                finish();
                                                return;
                                            }
                                        });

                                    }
                                });

                            }
                        }
                    });
                }
                else if(!password.equals(confirmPassword))
                    Toast.makeText(RegistrationActivity.this, "Password don't match!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(RegistrationActivity.this, "Fill every details!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode== Activity.RESULT_OK)
        {
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

}