package com.example.tech.coinz;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Backend {

    //Currency transfer rates
    public static Double shil;
    public static Double quid;
    public static Double dolr;
    public static Double peny;


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



    public static void addUserInfo(Map<String, Object> userInfo, String uID, Context context, String TAG){
        mUserRef.document(uID).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Successfully added user info to firestore db");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getMessage() );
                Toast.makeText(context, "Error Add User Info. Try Again", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(((Activity)context), SignUpActivity.class);
                ((Activity)context).startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }

    public static void changeBankBalance(int newBalance, Context context, String TAG){
        Map<String, Object> bankBalance = new HashMap<>();
        bankBalance.put("BankBalance", newBalance);
        mBankRef.document("Total").set(bankBalance).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "changeBankBalance: Bank Balance was Successfully Changed");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "changeBankBalance: Failed " + e.getMessage());
            }
        });
    }

    public static void changeCoinsSubmitted(int submittedCount, Context context, String TAG){
        Map<String, Object> submittedToday = new HashMap<>();
        submittedToday.put("SubmittedToday", submittedCount);
        mBankRef.document("submittedCount").set(submittedToday).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "changeCoinsSubmitted: CoinsSubmitted today was successfully changed");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "changeCoinsSubmitted: Failure " + e.getMessage());

            }
        });
    }



    public static void addFriend(String email, String TAG, Context context){
        mUserRef.whereEqualTo("Email", email).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Map<String, Object> friendInfo = queryDocumentSnapshots.getDocuments().get(0).getData();
                String uID = friendInfo.get("UID").toString();

                mCurrentUserRef.collection("SentRequests").whereEqualTo("UID", uID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                       Log.d(TAG, "addFriend: friend Request Has already been sent");
                       Toast.makeText(context, "Friend Request has already been sent", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        sendFriendRequest(friendInfo, TAG, context);
                    }
                });

            }
        }). addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "addFriend: " + e.getMessage());
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void sendFriendRequest(Map<String, Object> friendInfo, String TAG, Context context){
        mCurrentUserRef.collection("SentRequests").document(friendInfo.get("UID").toString())
                .set(friendInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "sendFriendRequest: Successfully added SentRequests");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "sendFriendRequest: Failed to add SentRequests " + e.getMessage());
            }
        });

        Map<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("Email", UserInfo.userEmail);
        userInfo.put("DisplayName", UserInfo.userDisplayName);
        userInfo.put("UID", UserInfo.userUid);


        mUserRef.document(friendInfo.get("UID").toString()).collection("FriendRequests").document(UserInfo.userUid)
                .set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "sendFriendRequest: Successfully added FriendRequests");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "sendFriendRequest: Failed to add FriendRequests " + e.getMessage());
            }
        });
    }

    public static void addCollectedCoin(Map<String, Object> coinInfo, String TAG, Context context, Marker marker, MapboxMap map){
        mCurrentUserRef.collection("CollectedCoin").document("Collected").set(coinInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Coin successfully added to wallet");
                Toast.makeText(context, "Coin added to wallet", Toast.LENGTH_LONG).show();
                map.removeMarker(marker);
            }
        });
    }


    public static void getExchangeRate(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        JSONObject jsonRates = jsonObject.getJSONObject("rates");
        String shil =jsonRates.get("SHIL").toString();
        String dolr = jsonRates.get("DOLR").toString();
        String quid = jsonRates.get("QUID").toString();
        String peny = jsonRates.get("PENY").toString();

        Backend.shil = Double.parseDouble(shil);
        Backend.dolr = Double.parseDouble(dolr);
        Backend.peny = Double.parseDouble(peny);
        Backend.quid = Double.parseDouble(quid);

        HashMap<String, Object> rates = new HashMap<>();
        rates.put("SHIL", shil);
        rates.put("DOLR", dolr);
        rates.put("QUID", quid);
        rates.put("PENY", peny);

        db.collection("General").document("Rates").set(rates);
    }



    public static void moveCoinsToSpareChange(String TAG) {
        mCurrentUserRef.collection("CollectedCoins").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                    Map<String, Object> collectedCoins = doc.getData();
                    mCurrentUserRef.collection("SpareChange").document(collectedCoins.get("ID").toString())
                            .set(collectedCoins).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: Succefully moved coin to SpareChange");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "moveCoinsToSpareChange: Failed when adding coin to SpareChange. Error: " + e.getMessage());
                        }
                    });
                }
                Backend.emptyCollectedCoins(TAG);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "moveCoinsToSpareChange: Failed to get coin from Collected Coin. Error: " + e.getMessage());
            }
        });
    }

    private static void emptyCollectedCoins(String TAG) {
        mCurrentUserRef.collection("CollectedCoins").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc: queryDocumentSnapshots.getDocuments()){
                    doc.getReference().delete();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "emptyCollectedCoins: Failed to delte Documents " + e.getMessage());
            }
        });
    }

    public static void pickUpCoin(String coinID, String TAG){
        mCurrentUserRef.collection("Map").document(coinID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> coinInfo = documentSnapshot.getData();
                documentSnapshot.getReference().delete();
                addToCollectedCoins(coinInfo, TAG);
            }
        });
    }

    public static void addToCollectedCoins(Map<String,Object> coinInfo, String TAG) {
        mCurrentUserRef.collection("CollectedCoins").document(coinInfo.get("ID").toString()).set(coinInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "addToCollectedCoins: Succesfully added coin to CollectedCoins");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "addToCollectedCoins: " + e.getMessage());
            }
        });
    }


    public static void updateDate() {
        Map<String, Object> date = new HashMap<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH);
        String currentDate = dateTimeFormatter.format(localDateTime);
        date.put("LastUpdated", currentDate);
        mCurrentUserRef.collection("Date").document("LastUsed").set(date);
    }

    public static void sendRequest(Map<String, Object> userInfo){
        String usersID = userInfo.get("UID").toString();
        mCurrentUserRef.collection("SentRequests").document(usersID).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(SelectUserActivity.TAG, "sendRequest: Succefully added info to sent Request");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(SelectUserActivity.TAG, "sendRequest: Failure adding to sent requests " + e.getMessage());
            }
        });
        mCurrentUserRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> currentUserInfo = documentSnapshot.getData();
                mUserRef.document(usersID).collection("FriendRequests").document(currentUser.getUid()).set(currentUserInfo);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(SelectUserActivity.TAG, "sendRequests: Failure getting the current users info "+e.getMessage());
            }
        });

    }

    public static void moveToFriends(Map<String, Object> userInfo){
        mCurrentUserRef.collection("Friends").document(userInfo.get("UID").toString()).set(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mCurrentUserRef.collection("FriendRequests").document(userInfo.get("UID").toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(SelectUserActivity.TAG, "FriendRequests successfully moved to Friends.");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(SelectUserActivity.TAG, "Failure on deleteing FriendRequests: "+ e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(SelectUserActivity.TAG, "Failure on moving to Friends: " + e.getMessage());
            }
        });
        Map<String, Object> mCurrentUser = new HashMap<>();
        mCurrentUser.put("Email", currentUser.getEmail());
        mCurrentUser.put("UID", currentUser.getUid());
        mCurrentUser.put("DisplayName", currentUser.getDisplayName());
        mUserRef.document(userInfo.get("UID").toString()).collection("Friends").document(currentUser.getUid()).set(mCurrentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(SelectUserActivity.TAG, "Successfully added current user to friends");
                mUserRef.document(userInfo.get("UID").toString()).collection("SentRequests").document(currentUser.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(SelectUserActivity.TAG, "onSuccess: Successfully deleted current user info from SentRequests");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(SelectUserActivity.TAG, "onFailure: Failed to delete current users info from SentRequests" + e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(SelectUserActivity.TAG, "Failed to add current User to friends " + e.getMessage());
            }
        });
    }


    public static void sendCoin(String coinID, String userID) {
        mCurrentUserRef.collection("CollectedCoins").whereEqualTo("ID", coinID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size()>0){
                    DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                    Map<String, Object> coinInfo = doc.getData();
                    addToReceivedCoins(coinInfo, userID);
                    doc.getReference().delete();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(SelectUserActivity.TAG, "onFailure: sendCoin " + e.getMessage());
            }
        });
        Query querySpareChange = mCurrentUserRef.collection("SpareChange").whereEqualTo("ID", coinID);
        querySpareChange.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.getDocuments().size()>0){
                    DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                    Map<String, Object> coinInfo = doc.getData();
                    addToReceivedCoins(coinInfo, userID);
                    doc.getReference().delete();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(SelectUserActivity.TAG, "onFailure: sendCoin " + e.getMessage());
            }
        });
    }

    private static void addToReceivedCoins(Map<String,Object> coinInfo, String userID) {
        mUserRef.document(userID).collection("RecievedCoins").document(coinInfo.get("ID").toString()).set(coinInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(SelectUserActivity.TAG, "addToRecievedCoins: Successful");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(SelectUserActivity.TAG, "onFailure: addToRecievedCoins " + e.getMessage());
            }
        });
    }

    public static void getBankCount(Map<String,Object> coinInfo, Context mContext){
        mCurrentUserRef.collection("Bank").document("submittedCount").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(SelectUserActivity.TAG, "onSuccess: getBankCount");
                Map<String, Object> submittedCount= documentSnapshot.getData();
                if(Integer.parseInt(submittedCount.get("SubmittedToday").toString())<25){
                    addCoinToBank(coinInfo, Integer.parseInt(submittedCount.get("SubmittedToday").toString()));
                } else {
                    Toast.makeText(mContext, "Can't bank anymore today!", Toast.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(SelectUserActivity.TAG, "onFailure: getBankCount " +e.getMessage());
            }
        });
    }

    private static void addCoinToBank(Map<String,Object> coinInfo, int submitCount) {
        final int count = submitCount +1;
        mCurrentUserRef.collection("Bank").document(coinInfo.get("ID").toString()).set(coinInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                removeCoin(coinInfo);
                Map<String, Object> submittedCount = new HashMap<>();
                submittedCount.put("SubmittedToday", count);
                getRate(coinInfo);

                mCurrentUserRef.collection("Bank").document("submittedCount").set(submittedCount).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(SelectUserActivity.TAG, "onSuccess: addCoinToBank");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(SelectUserActivity.TAG, "onFailure: addCoinToBank" +e.getMessage());
                    }
                });
            }
        });
    }
    private static void removeCoin(Map<String, Object> coinInfo){
        mCurrentUserRef.collection("CollectedCoins").document(coinInfo.get("ID").toString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private static void incrementBank(Map<String, Object> coinInfo, Double rate){

        DocumentReference docRef = mCurrentUserRef.collection("Bank").document("Total");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> bankTotal = documentSnapshot.getData();
                Double bankBalance = Double.parseDouble(bankTotal.get("BankBalance").toString());
                Double goldCoin = Double.parseDouble(coinInfo.get("Value").toString());
                goldCoin *= rate;

                bankTotal.put("BankBalance", bankBalance + goldCoin);
                docRef.set(bankTotal).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(SelectUserActivity.TAG, "onSuccess: incrementBank");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(SelectUserActivity.TAG, "onFailure: incrementBank "+ e.getMessage());
            }
        });
    }

    private static void getRate(Map<String, Object> coinInfo){
        db.collection("General").document("Rates").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Map<String, Object> rates = documentSnapshot.getData();
                Double rate = Double.parseDouble(rates.get(coinInfo.get("Currency")).toString());
                incrementBank(coinInfo, rate);
            }
        });
    }


}
