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

public class Backend {


    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseUser currentUser = mAuth.getCurrentUser() ;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static CollectionReference mUserRef = db.collection("User");

    private static DocumentReference mCurrentUserRef = mUserRef.document(currentUser.getUid());

    private static CollectionReference mBankRef = mUserRef.document(currentUser.getUid()).collection("Bank");



    static void addUserInfo(Map<String, Object> userInfo, String uID, Context context){
        mUserRef.document(uID).set(userInfo).addOnSuccessListener(aVoid ->
                Timber.d("onSuccess: Successfully added user info to firestore db")
        ).addOnFailureListener(e -> {
            Timber.e("onFailure: %s", e.getMessage());
            Toast.makeText(context, "Error Add User Info. Try Again", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(((Activity)context), SignUpActivity.class);
            ((Activity)context).startActivity(intent);
            ((Activity)context).finish();
        });
    }

    static void changeBankBalance(int newBalance, Context context){
        Map<String, Object> bankBalance = new HashMap<>();
        bankBalance.put("BankBalance", newBalance);
        mBankRef.document("Total").set(bankBalance).addOnSuccessListener(aVoid ->
                Timber.d("changeBankBalance: Bank Balance was Successfully Changed")).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e("changeBankBalance: Failed %s", e.getMessage());
            }
        });
    }

    static void changeCoinsSubmitted(int submittedCount, Context context){
        Map<String, Object> submittedToday = new HashMap<>();
        submittedToday.put("SubmittedToday", submittedCount);
        mBankRef.document("submittedCount").set(submittedToday).addOnSuccessListener(aVoid ->
                Timber.d("changeCoinsSubmitted: CoinsSubmitted today was successfully changed")).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Timber.e("changeCoinsSubmitted: Failure %s", e.getMessage());

            }
        });
    }



    public static void addFriend(String email, String TAG, Context context){
        mUserRef.whereEqualTo("Email", email).get().addOnSuccessListener(queryDocumentSnapshots -> {
            Map<String, Object> friendInfo = queryDocumentSnapshots.getDocuments().get(0).getData();
            String uID = null;
            if (friendInfo != null) {
                uID = Objects.requireNonNull(friendInfo.get("UID")).toString();
            }

            mCurrentUserRef.collection("SentRequests").whereEqualTo("UID", uID).get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                Timber.d("addFriend: friend Request Has already been sent");
               Toast.makeText(context, "Friend Request has already been sent", Toast.LENGTH_LONG).show();
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (friendInfo != null) {
                        sendFriendRequest(friendInfo, TAG, context);
                    }
                }
            });

        }). addOnFailureListener(e -> {
            Timber.e("addFriend: %s", e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    public static void sendFriendRequest(Map<String, Object> friendInfo, String TAG, Context context){
        mCurrentUserRef.collection("SentRequests").document(Objects.requireNonNull(friendInfo.get("UID")).toString())
                .set(friendInfo).addOnSuccessListener(aVoid ->
                Timber.d("sendFriendRequest: Successfully added SentRequests")
        ).addOnFailureListener(e ->
                Timber.e("sendFriendRequest: Failed to add SentRequests %s", e.getMessage())
        );

        Map<String, Object> userInfo = new HashMap<String, Object>();
        userInfo.put("Email", UserInfo.userEmail);
        userInfo.put("DisplayName", UserInfo.userDisplayName);
        userInfo.put("UID", UserInfo.userUid);


        mUserRef.document(Objects.requireNonNull(friendInfo.get("UID")).toString()).collection("FriendRequests").document(UserInfo.userUid)
                .set(userInfo).addOnSuccessListener(aVoid ->
                Timber.d("sendFriendRequest: Successfully added FriendRequests")
        ).addOnFailureListener(e ->
                Timber.e("sendFriendRequest: Failed to add FriendRequests %s", e.getMessage())
        );
    }






    public static void sendRequest(Map<String, Object> userInfo){
        String usersID = userInfo.get("UID").toString();
        mCurrentUserRef.collection("SentRequests").document(usersID).set(userInfo).addOnSuccessListener(aVoid ->
                Log.d(SelectUserActivity.TAG, "sendRequest: Succefully added info to sent Request")
        ).addOnFailureListener(e ->
                Timber.e("sendRequest: Failure adding to sent requests %s", e.getMessage())
        );
        mCurrentUserRef.get().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> currentUserInfo = documentSnapshot.getData();
            mUserRef.document(usersID).collection("FriendRequests").document(currentUser.getUid()).set(currentUserInfo);
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





}
