package com.example.tech.coinz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";


    private MaterialEditText metEmail, metName, metPassword;
    Button btnSignUp, btnBack;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnSignUp = (Button) findViewById(R.id.signUpButton);
        btnBack = (Button) findViewById(R.id.backButton);

        metEmail = (MaterialEditText) findViewById(R.id.medtEmail);
        metName = (MaterialEditText) findViewById(R.id.medtName);
        metPassword = (MaterialEditText) findViewById(R.id.medtPassword);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(metEmail.getText()).toString();
                String password = Objects.requireNonNull(metPassword.getText()).toString();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "Successfully created account");
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            updateUI(currentUser);

                        } else{
                            Log.d(TAG, "CreateUserWithEmail: failure"+ task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed: "+task.getException(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null ){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void updateUI(final FirebaseUser currentUser){


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user !=null){
                    Log.d(TAG, "user is not null");
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(Objects.requireNonNull(metName.getText()).toString()).build();
                    user.updateProfile(profileUpdates);


                    Map<String, Object> userInfo = new HashMap<>();
                    String displayName = metName.getText().toString();
                    Log.d(TAG, "name of user is " + displayName);
                    Timber.d("name");


                    userInfo.put("UID", user.getUid());
                    if(user.getEmail() != null){
                        userInfo.put("Email", user.getEmail());
                    }

                    userInfo.put("DisplayName", displayName);

                    //Adds users info to the database aand if successful closes SignUp and starts MapActivity
                    Backend.addUserInfo(userInfo, user.getUid(), getApplicationContext());
                    Backend.changeBankBalance(0, getApplicationContext());
                    Backend.changeCoinsSubmitted(0, getApplicationContext());

                    UserInfo.userDisplayName = displayName;
                    UserInfo.userEmail = user.getEmail();
                    UserInfo.userUid = user.getUid();


                    String date = "0000/00/00";
                    Map<String, Object> dateMap = new HashMap<>();
                    dateMap.put("LastUpdated", date);
                    db.collection("User").document(user.getUid()).collection("Date").document("LastUsed").set(dateMap);


                    Intent intent = new Intent(SignUpActivity.this, MapActivity.class);
                    startActivity(intent);
                    finish();


                } else {
                    Log.d(TAG, "onAuthStateChanged: user was null");
                }
            }
        };

    }

//    private void addDefaultProfilePic(FirebaseUser user){
//
//        StorageReference profileRef = storageReference.child("images/profile_picture/"+ user.getUid());
//        Uri uri = Uri.parse("android.resource://com.example.tech.coinz/drawable/ic_person_outline_black_24dp");
//        profileRef.putFile(uri).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "Error: Unsuccessful upload");
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Log.d(TAG, "onSuccess: Added a default Profile Picture");
//            }
//        });
//    }




}
