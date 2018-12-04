package com.example.tech.coinz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class BonusFeatureActivity extends AppCompatActivity {

    private static final String TAG = "BonusFeatureActivity";

    private TextView usr_first, usr_second, usr_third, usr_fourth, usr_fifth;
    private TextView bank_first, bank_second, bank_third, bank_fourth, bank_fifth;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference mCurrentUserRef;

    ArrayList<Double> bankBalances;
    ArrayList<String> userEmails;

    private Button playBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus_feature);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mCurrentUserRef = db.collection("User").document(user.getUid());

        userEmails = new ArrayList<>();
        bankBalances = new ArrayList<>();

        usr_first = (TextView) findViewById(R.id.usr_first);
        usr_second = (TextView) findViewById(R.id.usr_second);
        usr_third = (TextView) findViewById(R.id.usr_third);
        usr_fourth = (TextView) findViewById(R.id.usr_fourth);
        usr_fifth = (TextView) findViewById(R.id.usr_fifth);

        bank_first = (TextView) findViewById(R.id.bank_first);
        bank_second = (TextView) findViewById(R.id.bank_second);
        bank_third = (TextView) findViewById(R.id.bank_third);
        bank_fourth = (TextView) findViewById(R.id.bank_fourth);
        bank_fifth = (TextView) findViewById(R.id.bank_fifth);

        getLeaderboard();

//        db.collection("User").orderBy("BankBalance").limit(5).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                Log.d(TAG, "onSuccess: " + queryDocumentSnapshots.getDocuments().get(0).getData().get("UID"));
//                for(DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
//                    Log.d(TAG, "onSuccess:  documents" +doc.getData());
//                }
//            }
//        });

        playBtn = (Button)findViewById(R.id.playBtn);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BonusFeatureActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });
    }


    private void getLeaderboard(){
        db.collection("User").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                    Map<String, Object> userInfo = doc.getData();
                    Log.d(TAG, "onSuccess: getLeaderboard " + userInfo.get("UID"));
                    doc.getReference().collection("Bank").document("Total").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Map<String, Object> bank = documentSnapshot.getData();
                            Double balance = Double.parseDouble(bank.get("BankBalance").toString());
                            int i = 0;
                            if(bankBalances.size()!=0){
                                while(i<bankBalances.size() && bankBalances.get(i)<balance){
                                    i++;
                                }
                            }

                            bankBalances.add(i, balance);
                            userEmails.add(i, userInfo.get("Email").toString());
                            if(userEmails.size()==queryDocumentSnapshots.size()){
                                displayTopFive();
                            }
                        }
                    });
                }
            }
        });

    }

    private void displayTopFive() {
        Log.d(TAG, "displayTopFive: started " +userEmails.size());
        int arr_length = userEmails.size();
        if (arr_length < 5) {
            for (int i = 0; i < 5 - arr_length ; i++) {
                userEmails.add(i, "Username");
                bankBalances.add(i, 0.0);
            }
            arr_length = 5;
        }
        Log.d(TAG, "displayTopFive: new size "+ userEmails.size());
        usr_first.setText(userEmails.get(arr_length - 1));
        usr_second.setText(userEmails.get(arr_length - 2));
        usr_third.setText(userEmails.get(arr_length - 3));
        usr_fourth.setText(userEmails.get(arr_length - 4));
        usr_fifth.setText(userEmails.get(arr_length - 5));

        bank_first.setText(String.valueOf(bankBalances.get(arr_length - 1).intValue()));
        bank_second.setText(String.valueOf(bankBalances.get(arr_length - 2).intValue()));
        bank_third.setText(String.valueOf(bankBalances.get(arr_length - 3).intValue()));
        bank_fourth.setText(String.valueOf(bankBalances.get(arr_length - 4).intValue()));
        bank_fifth.setText(String.valueOf(bankBalances.get(arr_length - 5).intValue()));
    }

}
