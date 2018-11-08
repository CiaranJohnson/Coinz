package com.example.tech.coinz;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseWork {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public Integer walletCount;
    private static final String TAG = "DatabaseWork";


    public Integer getWalletCount(){
        db.collection("App").document("User").collection(user.getUid()).document("User Info").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Log.d(TAG, "onComplete: Got the Wallet Count successfully from firestore");
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Object object = documentSnapshot.get("WalletCount");
                    Log.d(TAG, "onComplete: " + object.toString());
                    walletCount = Integer.getInteger(object.toString());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e);

            }
        });
        if (walletCount == null){
            Log.d(TAG, "getWalletCount: Wallet Count is null");
            return 0;
        } else {
            Log.d(TAG, "getWalletCount: " + walletCount);
            return walletCount;
        }
    }

    public void incrementWalletCount(Integer walletCount){
        walletCount++;
        db.collection("App").document("User").collection(user.getUid()).document("User Info").update("WalletCount", walletCount).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Successfully incremented successfully");
            }
        });
    }
}
