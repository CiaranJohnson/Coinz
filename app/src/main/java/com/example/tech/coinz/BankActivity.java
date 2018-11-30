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

public class BankActivity extends AppCompatActivity {

    private static final String TAG = "BankActivity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private Button mapBtn;
    private TextView balanceTxt;

    Map<String, Object> collectedCoin;
    ArrayList<String> currency;
    ArrayList<String> value;
    ArrayList<String> id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);


        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        currency = new ArrayList<>();
        value = new ArrayList<>();
        id = new ArrayList<>();


        mapBtn = (Button) findViewById(R.id.mapButton);
        balanceTxt = (TextView) findViewById(R.id.balanceTxt);

        displayBankBalanced();

        //Get the currency, value and id of all the coins currently stored in the collected part of wallet
        db.collection("User").document(user.getUid()).collection("CollectedCoins").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
               List <DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
               for (DocumentSnapshot doc: docs){
                   collectedCoin = doc.getData();

                   Log.d(TAG, "onSuccess: " + collectedCoin.get("Currency").toString());

                   currency.add(collectedCoin.get("Currency").toString());
                   value.add(collectedCoin.get("Value").toString());
                   id.add(collectedCoin.get("ID").toString());

               }
               initRecyclerView();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                startActivity(intent);
            }
        });



    }

    public void displayBankBalanced(){
        db.collection("User").document(user.getUid()).collection("Bank").document("Total").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> balanceData = documentSnapshot.getData();
                String balance = balanceData.get("BankBalance").toString();
                int index = balance.indexOf('.');
                balanceTxt.setText(balance.substring(0, index + 3));
                Log.d(TAG, "onSuccess: " + balance);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
            }
        });
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(currency, value, id, this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
