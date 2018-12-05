package com.example.tech.coinz;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GameBackend {

    private static final String TAG = "GameBackend";

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseUser currentUser = mAuth.getCurrentUser() ;
    private static FirebaseStorage mFirestorage = FirebaseStorage.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static CollectionReference mUserRef = db.collection("User");

    private static DocumentReference mCurrentUserRef = mUserRef.document(currentUser.getUid());

    private static CollectionReference mFriendsRef = mUserRef.document(currentUser.getUid())
            .collection("Friends");

    private static DocumentReference mRatesRef = db.collection("General").document("Rates");

    private static CollectionReference mBankRef = mUserRef.document(currentUser.getUid()).collection("Bank");

    public static void getCoinsFromSpareChange(String currency){
        mCurrentUserRef.collection("SpareChange").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                    Map<String, Object> coinInfo = doc.getData();

                    //Add the correct Coins to the Bank given the reward and delete all not being added to bank from SpareChange
                    if(coinInfo != null) {
                        if (currency.equals("AllCoins")) {
                            addCoinToBank(coinInfo);
                        } else if (Objects.requireNonNull(coinInfo.get("Currency")).toString().equals(currency)) {
                            addCoinToBank(coinInfo);
                        } else {
                            removeCoin(coinInfo);
                        }
                    } else {
                        Log.e(TAG, "coinInfo is null");
                    }
                }
            }
        });
    }



    private static void addCoinToBank(Map<String,Object> coinInfo) {

        mCurrentUserRef.collection("Bank").document(Objects.requireNonNull(coinInfo.get("ID")).toString()).set(coinInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                removeCoin(coinInfo);
                getRate(coinInfo);


            }
        });
    }

    private static void getRate(Map<String, Object> coinInfo){
        db.collection("General").document("Rates").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> rates = documentSnapshot.getData();
                if(rates != null){
                    Object currency = Objects.requireNonNull(coinInfo.get("Currency")).toString();
                    Double rate = Double.parseDouble(Objects.requireNonNull(rates.get(currency)).toString());
                    incrementBank(coinInfo, rate);
                }
            }
        });
    }

    private static void incrementBank(Map<String, Object> coinInfo, Double rate){

        DocumentReference docRef = mCurrentUserRef.collection("Bank").document("Total");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> bankTotal = documentSnapshot.getData();
                if (bankTotal != null) {
                    Double bankBalance = Double.parseDouble(Objects.requireNonNull(bankTotal.get("BankBalance")).toString());
                    Double goldCoin = Double.parseDouble(Objects.requireNonNull(coinInfo.get("Value")).toString());
                    goldCoin *= rate;

                    bankTotal.put("BankBalance", bankBalance + goldCoin);
                    docRef.set(bankTotal).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(GameActivity.TAG, "onSuccess: incrementBank");
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(GameActivity.TAG, "onFailure: incrementBank "+ e.getMessage());
            }
        });
    }

    private static void removeCoin(Map<String, Object> coinInfo){
        mCurrentUserRef.collection("CollectedCoins").document(Objects.requireNonNull(coinInfo.get("ID")).toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(SelectUserActivity.TAG, "onSuccess: removeCoin Successful");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(SelectUserActivity.TAG, "onFailure: removeCoin " + e.getMessage());
            }
        });
    }
}
