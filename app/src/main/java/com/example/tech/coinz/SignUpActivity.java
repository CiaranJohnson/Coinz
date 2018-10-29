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
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = "SignUpActivity";

    private MaterialEditText metEmail, metName, metPassword;
    private Button btnSignUp, btnBack;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        btnSignUp = (Button) findViewById(R.id.signUpButton);
        btnBack = (Button) findViewById(R.id.backButton);

        metEmail = (MaterialEditText) findViewById(R.id.medtEmail);
        metName = (MaterialEditText) findViewById(R.id.medtName);
        metPassword = (MaterialEditText) findViewById(R.id.medtPassword);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.createUserWithEmailAndPassword(metEmail.getText().toString(), metPassword.getText().toString()).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
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
                if(currentUser!=null){
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(metName.getText().toString()).build();
                    currentUser.updateProfile(profileUpdates);
                    Intent intent = new Intent(SignUpActivity.this, MapActivity.class);
                    startActivity(intent);
                }
            }
        };
    }
}
