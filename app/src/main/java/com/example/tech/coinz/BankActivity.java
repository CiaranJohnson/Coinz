package com.example.tech.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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

    Map<String, Object> collectedCoin;
    ArrayList<String> currency;
    ArrayList<String> value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        currency = new ArrayList<>();
        value = new ArrayList<>();

        db.collection("App").document("User").collection(user.getUid()).document("Wallet")
                .collection("Collected").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
               List <DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
               for (DocumentSnapshot doc: docs){
                   collectedCoin = doc.getData();

                   Log.d(TAG, "onSuccess: " + collectedCoin.get("Currency").toString());

                   currency.add(collectedCoin.get("Currency").toString());
                   value.add(collectedCoin.get("Value").toString());

//                   Coin coin = new Coin(collectedCoin.get("ID").toString(), collectedCoin.get("Value").toString(), collectedCoin.get("Currency").toString(),
//                           collectedCoin.get("Marker Symbol").toString(), collectedCoin.get("Marker Colour").toString(), collectedCoin.get("LatLng").toString());
//

               }
               initRecyclerView();
            }
        });


    }

    private void initRecyclerView(){
//        Log.d(TAG, "initRecyclerView: " + currency.get(0));
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(currency, value,this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}
