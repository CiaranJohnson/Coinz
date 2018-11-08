package com.example.tech.coinz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private Button mapBtn;
    TextView nameTxt, coinsTxt;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        nameTxt = (TextView) findViewById(R.id.addNameTxt);
        coinsTxt = (TextView) findViewById(R.id.txtCoins);
        mapBtn = (Button) findViewById(R.id.mapButton);

        nameTxt.setText(user.getDisplayName());

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });


        db.collection("App").document("User").collection(user.getUid())
                .document("User Info").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Got the Wallet Count successfully from firestore");
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Object object = documentSnapshot.get("WalletCount");
                    coinsTxt.setText(object.toString());
                    Log.d(TAG, "onComplete: " + object.toString() + " coin/s in the wallet");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e);
            }
        });

//
//        DatabaseWork dw = new DatabaseWork();
//        Integer walletCount =  dw.getWalletCount();
//        coinsTxt.setText(walletCount.toString());

    }
}
