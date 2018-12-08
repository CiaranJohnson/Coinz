package com.example.tech.coinz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class displays the current amount of gold in the bank aswell as all the coins that can be added to the bank.
 *
 * @author Ciaran
 */

public class BankActivity extends AppCompatActivity {

    private static final String TAG = "BankActivity";

    FirebaseAuth firebaseAuth;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseUser user;

    Button mapBtn;
    private static TextView balanceTxt;

    Map<String, Object> collectedCoin;
    Map<String, Object> receivedCoin;
    ArrayList<String> currency;
    ArrayList<String> value;
    ArrayList<String> id;

    ArrayList<String> receivedCurrency;
    ArrayList<String> receivedValue;
    ArrayList<String> receivedId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        currency = new ArrayList<>();
        value = new ArrayList<>();
        id = new ArrayList<>();

        receivedCurrency = new ArrayList<>();
        receivedValue = new ArrayList<>();
        receivedId = new ArrayList<>();


        mapBtn = (Button) findViewById(R.id.mapButton);
        balanceTxt = (TextView) findViewById(R.id.balanceTxt);

        displayBankBalanced();

        getCollectedCoins();

        //Get all the Coins in Collected Coins and store the id, value and Currency as these will be used in the recycler view

//        db.collection("User").document(user.getUid()).collection("CollectedCoins").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//               List <DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
//               for (DocumentSnapshot doc: docs){
//                   collectedCoin = doc.getData();
//
//                   Log.d(TAG, "onSuccess: " + collectedCoin.get("Currency").toString());
//
//                   currency.add(Objects.requireNonNull(collectedCoin.get("Currency").toString()));
//                   value.add(Objects.requireNonNull(collectedCoin.get("Value")).toString());
//                   id.add(Objects.requireNonNull(collectedCoin.get("ID").toString()));
//
//               }
//
//               initRecyclerView();
//            }
//        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });



    }

    private void getCollectedCoins(){
        Log.d(TAG, "getCollectedCoins: ");
        db.collection("User").document(user.getUid()).collection("CollectedCoins").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "onSuccess: heres");
                List <DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot doc: docs){
                    collectedCoin = doc.getData();

                    Log.d(TAG, "onSuccess: " + collectedCoin.get("Currency").toString());

                    currency.add(Objects.requireNonNull(collectedCoin.get("Currency").toString()));
                    value.add(Objects.requireNonNull(collectedCoin.get("Value")).toString());
                    id.add(Objects.requireNonNull(collectedCoin.get("ID").toString()));

                }

                getReceivedCoins();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ", e);
            }
        });
    }

    private void getReceivedCoins(){
        db.collection("User").document(user.getUid()).collection("RecievedCoins").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List <DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot doc: docs){
                    receivedCoin = doc.getData();

                    Log.d(TAG, "onSuccess: " + receivedCoin.get("Currency").toString());

                    receivedCurrency.add(Objects.requireNonNull(receivedCoin.get("Currency").toString()));
                    receivedValue.add(Objects.requireNonNull(receivedCoin.get("Value")).toString());
                    receivedId.add(Objects.requireNonNull(receivedCoin.get("ID").toString()));

                }

                initRecyclerView();
            }
        });
    }

    /**
     * Gets the users BankBalance from Firebase and Displays this value to 2 significant figure
     */
    public static void displayBankBalanced(){
        db.collection("User").document(user.getUid()).collection("Bank").document("Total").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> balanceData = documentSnapshot.getData();
                if(balanceData != null){
                    String balance = Objects.requireNonNull(balanceData.get("BankBalance")).toString();
                    int index = balance.indexOf('.');
                    if(index == -1){
                        balanceTxt.setText(balance);
                    } else {
                        balanceTxt.setText(balance.substring(0, index + 3));
                        Log.d(TAG, "onSuccess: " + balance);
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
            }
        });
    }

    /**
     * Initialises the recycler view with Coin Information about Coins collected that day
     */
    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(currency, value, id, this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView receivedView = findViewById(R.id.received_recycler_view);
        ReceivedViewAdapter receivedViewAdapter = new ReceivedViewAdapter(receivedCurrency, receivedValue, receivedId, this);
        receivedView.setAdapter(receivedViewAdapter);
        receivedView.setLayoutManager(new LinearLayoutManager(this));
    }

}
