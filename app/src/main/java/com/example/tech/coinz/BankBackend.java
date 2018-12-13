package com.example.tech.coinz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class BankBackend {

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseUser currentUser = mAuth.getCurrentUser() ;
    @SuppressLint("StaticFieldLeak")
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static CollectionReference mUserRef = db.collection("User");

    private static DocumentReference mCurrentUserRef = mUserRef.document(currentUser.getUid());

    private static CollectionReference mBankRef = mUserRef.document(currentUser.getUid()).collection("Bank");

    public static void updateDate() {
        Map<String, Object> date = new HashMap<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH);
        String currentDate = dateTimeFormatter.format(localDateTime);
        date.put("LastUpdated", currentDate);
        mCurrentUserRef.collection("Date").document("LastUsed").set(date);
    }

    public static void addCollectedCoin(Map<String, Object> coinInfo, String TAG, Context context, Marker marker, MapboxMap map){
        mCurrentUserRef.collection("CollectedCoin").document("Collected").set(coinInfo).addOnSuccessListener(aVoid -> {
            Timber.d("onSuccess: Coin successfully added to wallet");
            Toast.makeText(context, "Coin added to wallet", Toast.LENGTH_LONG).show();
            map.removeMarker(marker);
        });
    }


    static void getExchangeRate(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONObject jsonRates = jsonObject.getJSONObject("rates");
        String shil =jsonRates.get("SHIL").toString();
        String dolr = jsonRates.get("DOLR").toString();
        String quid = jsonRates.get("QUID").toString();
        String peny = jsonRates.get("PENY").toString();

        HashMap<String, Object> rates = new HashMap<>();
        rates.put("SHIL", shil);
        rates.put("DOLR", dolr);
        rates.put("QUID", quid);
        rates.put("PENY", peny);

        db.collection("General").document("Rates").set(rates);
    }



    static void moveCoinsToSpareChange() {
        mCurrentUserRef.collection("CollectedCoins").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                Map<String, Object> collectedCoins = doc.getData();
                if (collectedCoins != null) {
                    mCurrentUserRef.collection("SpareChange").document(Objects.requireNonNull(collectedCoins.get("ID")).toString())
                            .set(collectedCoins).addOnSuccessListener(aVoid ->
                            Timber.d("onSuccess: Succefully moved coin to SpareChange")
                    ).addOnFailureListener(e ->
                            Timber.d("moveCoinsToSpareChange: Failed when adding coin to SpareChange. Error: %s", e.getMessage())
                    );
                }
            }
            BankBackend.emptyCollectedCoins();
        }).addOnFailureListener(e ->
                Timber.e("moveCoinsToSpareChange: Failed to get coin from Collected Coin. Error: %s", e.getMessage())
        );
    }

    private static void emptyCollectedCoins() {
        mCurrentUserRef.collection("CollectedCoins").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                doc.getReference().delete();
            }
        }).addOnFailureListener(e ->
                Timber.e("emptyCollectedCoins: Failed to delte Documents %s", e.getMessage())
        );
    }

    public static void pickUpCoin(String coinID, Context mContext){
        mCurrentUserRef.collection("Map").document(coinID).get().addOnSuccessListener(documentSnapshot -> {
            if(!(documentSnapshot.getData() == null)) {
                Toast.makeText(mContext, "Coin Picked Up", Toast.LENGTH_LONG).show();
                Map<String, Object> coinInfo = documentSnapshot.getData();
                documentSnapshot.getReference().delete();
                addToCollectedCoins(coinInfo);
            }
        });
    }

    private static void addToCollectedCoins(Map<String, Object> coinInfo) {
        mCurrentUserRef.collection("CollectedCoins").document(Objects.requireNonNull(coinInfo.get("ID")).toString()).set(coinInfo).addOnSuccessListener(aVoid ->
                Timber.d("addToCollectedCoins: Succesfully added coin to CollectedCoins")
        ).addOnFailureListener(e -> Timber.e("addToCollectedCoins: %s", e.getMessage()));
    }

    public static void sendCoin(String coinID, String userID) {
        mCurrentUserRef.collection("CollectedCoins").whereEqualTo("ID", coinID).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.getDocuments().size()>0){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                Map<String, Object> coinInfo = doc.getData();
                if (coinInfo != null) {
                    addToReceivedCoins(coinInfo, userID);
                }
                doc.getReference().delete();
            }
        }).addOnFailureListener(e -> Timber.e("onFailure: sendCoin %s", e.getMessage()));


        Query querySpareChange = mCurrentUserRef.collection("SpareChange").whereEqualTo("ID", coinID);
        querySpareChange.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.getDocuments().size()>0){
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                Map<String, Object> coinInfo = doc.getData();
                if (coinInfo != null) {
                    addToReceivedCoins(coinInfo, userID);
                }
                doc.getReference().delete();
            }
        }).addOnFailureListener(e -> Timber.e("onFailure: sendCoin %s", e.getMessage()));
    }

    private static void addToReceivedCoins(Map<String,Object> coinInfo, String userID) {
        mUserRef.document(userID).collection("RecievedCoins").document(Objects.requireNonNull(coinInfo.get("ID")).toString()).set(coinInfo).addOnSuccessListener(aVoid ->
                Timber.d("addToRecievedCoins: Successful")
        ).addOnFailureListener(e -> Timber.e("onFailure: addToRecievedCoins %s", e.getMessage()));
    }

    public static void getBankCount(Map<String,Object> coinInfo, String coinType, Context mContext){
        mCurrentUserRef.collection("Bank").document("submittedCount").get().addOnSuccessListener(documentSnapshot -> {
            Timber.d("onSuccess: getBankCount");
            Map<String, Object> submittedCount= documentSnapshot.getData();
            if(submittedCount!= null) {
                if (coinType.equals("RecievedCoins")) {
                    addCoinToBank(coinInfo, Integer.parseInt(Objects.requireNonNull(submittedCount.get("SubmittedToday")).toString()), coinType);
                } else {
                    if (Integer.parseInt(Objects.requireNonNull(submittedCount.get("SubmittedToday")).toString()) < 25) {
                        addCoinToBank(coinInfo, Integer.parseInt(Objects.requireNonNull(submittedCount.get("SubmittedToday")).toString()) + 1, coinType);
                    } else {
                        Toast.makeText(mContext, "Can't bank anymore today!", Toast.LENGTH_LONG).show();
                    }
                }
            }


        }).addOnFailureListener(e -> Timber.e("onFailure: getBankCount %s", e.getMessage()));
    }

    private static void addCoinToBank(Map<String,Object> coinInfo, int submitCount, String coinType) {
        mCurrentUserRef.collection("Bank").document(Objects.requireNonNull(coinInfo.get("ID")).toString()).set(coinInfo).addOnSuccessListener(aVoid -> {

            removeCoin(coinInfo, coinType);
            Map<String, Object> submittedCount = new HashMap<>();
            submittedCount.put("SubmittedToday", submitCount);
            getRate(coinInfo);

            mCurrentUserRef.collection("Bank").document("submittedCount").set(submittedCount).addOnSuccessListener(aVoid1 -> {
                Timber.d("onSuccess: addCoinToBank");
                BankActivity.displayBankBalanced();

            }).addOnFailureListener(e -> Timber.e("onFailure: addCoinToBank%s", e.getMessage()));
        });
    }
    private static void removeCoin(Map<String, Object> coinInfo, String coinType){
        mCurrentUserRef.collection(coinType).document(Objects.requireNonNull(coinInfo.get("ID")).toString()).delete().addOnSuccessListener(aVoid ->
                Timber.d("onSuccess: removeCoin Successful")
        ).addOnFailureListener(e -> Timber.e("onFailure: removeCoin %s", e.getMessage()));
    }

    private static void incrementBank(Map<String, Object> coinInfo, Double rate){

        DocumentReference docRef = mCurrentUserRef.collection("Bank").document("Total");
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> bankTotal = documentSnapshot.getData();
            Double bankBalance = Double.parseDouble(bankTotal.get("BankBalance").toString());
            Double goldCoin = Double.parseDouble(Objects.requireNonNull(coinInfo.get("Value")).toString());
            goldCoin *= rate;

            bankTotal.put("BankBalance", bankBalance + goldCoin);
            docRef.set(bankTotal).addOnSuccessListener(aVoid ->
                    Timber.d("onSuccess: incrementBank"));
        }).addOnFailureListener(e -> Timber.e("onFailure: incrementBank %s", e.getMessage()));
    }

    private static void getRate(Map<String, Object> coinInfo){
        db.collection("General").document("Rates").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> rates = documentSnapshot.getData();
                Double rate = null;
                Timber.d(coinInfo.get("Currency").toString());
                String currency = Objects.requireNonNull(coinInfo.get("Currency").toString());
                if (rates != null) {
                    rate = Double.parseDouble(Objects.requireNonNull(rates.get(currency)).toString());
                }
                incrementBank(coinInfo, rate);
            }
        });
    }
}
