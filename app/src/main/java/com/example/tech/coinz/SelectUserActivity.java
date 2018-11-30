package com.example.tech.coinz;

import android.app.Dialog;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectUserActivity extends AppCompatActivity {

    public static final String TAG = "SelectUserHelpActivity";

    //variables
    private ArrayList<String> mDisplayName = new ArrayList<>();
    private ArrayList<String> mUserID = new ArrayList<>();
    private ArrayList<String> mEmail = new ArrayList<>();

    private ArrayList<String> mRequestName = new ArrayList<>();
    private ArrayList<String> mRequestEmail = new ArrayList<>();
    private ArrayList<String> mRequestID = new ArrayList<>();

    private String coinID;

    //Firebase variables
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private DocumentReference mCurrentUserRef;
    private Map<String, Object> friendData;

    MaterialSearchView searchView;

    private Dialog mDialog;

    private EditText edtSearch;
    private ImageButton searchBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);
        Log.d(TAG, "onCreate: has started.");


        //initialise Firebase
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        searchBtn = (ImageButton) findViewById(R.id.searchBtn);
        edtSearch = (EditText) findViewById(R.id.searchTxt);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mCurrentUserRef = db.collection("User").document(user.getUid());

        mDialog = new Dialog(SelectUserActivity.this);

        Bundle extras = getIntent().getExtras();
        if(extras !=  null){
            coinID = extras.getString("ID");
        }

        initImageBitmap();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtSearch.getText().toString().equals(null)){
                    firebaseSearch(edtSearch.getText().toString());
                }
            }
        });

    }

    private void initImageBitmap(){
        Log.d(TAG, "initImageBitmap: preparing bitmaps.");

        //get all the name and images and add them to the arraylists.
        DocumentReference mCurrentUserRef = db.collection("User").document(user.getUid());

        mCurrentUserRef.collection("Friends").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                if(docs.size()>0) {
                    for (DocumentSnapshot doc : docs) {

                        friendData = doc.getData();
                        mDisplayName.add(friendData.get("DisplayName").toString());
                        mUserID.add(friendData.get("UID").toString());
                        mEmail.add(friendData.get("Email").toString());

                    }
                }
                mCurrentUserRef.collection("FriendRequests").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (DocumentSnapshot doc :queryDocumentSnapshots.getDocuments()){
                            Map<String, Object> requestData = doc.getData();
                            Log.d(TAG, "onSuccess: " + requestData.get("DisplayName").toString());
                            mRequestName.add(requestData.get("DisplayName").toString());
                            mRequestID.add(requestData.get("UID").toString());
                            mRequestEmail.add(requestData.get("Email").toString());
                        }

                        initRecyclerView();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "initImageBitmap: FriendRequests " + e.getMessage());
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error getting friends from firebase: " + e.getMessage());
            }
        });

    }

    private void initRecyclerView(){
        Log.d(TAG, "initRecyclerView: started");
        RecyclerView friendRecyclerView = findViewById(R.id.friend_recycler_view);
        FriendViewAdapter friendViewAdapter = new FriendViewAdapter(mUserID, mDisplayName,mEmail, coinID, this);
        friendRecyclerView.setAdapter(friendViewAdapter);
        friendRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView requestRecyclerView = findViewById(R.id.requests_recycler_view);
        FriendRequestViewAdapter requestViewAdapter = new FriendRequestViewAdapter(mRequestID, mRequestName,mRequestEmail, this);
        requestRecyclerView.setAdapter(requestViewAdapter);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void firebaseSearch(String searchText){
        db.collection("User").whereEqualTo("Email", searchText).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Map<String, Object> userInformation = queryDocumentSnapshots.getDocuments().get(0).getData();
                if (queryDocumentSnapshots.getDocuments().size() == 0){
                    Toast.makeText(SelectUserActivity.this,"Sorry this User does not exist. Try again.", Toast.LENGTH_LONG).show();
                } else {
                    checkDatabase(userInformation);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "firebaseSearch: " + e.getMessage());
            }
        });
    }

    private void checkDatabase(Map<String, Object> userInfo){
        mCurrentUserRef.collection("SentRequests").whereEqualTo("UID", userInfo.get("UID")).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.getDocuments().size() == 0){
                    viewUserInformation(userInfo, false);
                } else {
                    viewUserInformation(userInfo, true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "checkDatabase: " + e.getMessage());
            }
        });
    }

    private void viewUserInformation(Map<String,Object> userInformation, Boolean sent) {

        mDialog.setContentView(R.layout.add_friend_popup);
        Button addFreindBtn = (Button) mDialog.findViewById(R.id.addFriendBtn);
        TextView txtName = (TextView) mDialog.findViewById(R.id.name);
        TextView closePopup = (TextView) mDialog.findViewById(R.id.close_popup);


        txtName.setText(userInformation.get("DisplayName").toString());

        if(sent){
            addFreindBtn.setText("Sent");
            addFreindBtn.setClickable(false);
        }

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.cancel();
            }
        });

        addFreindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Backend.sendRequest(userInformation);
                addFreindBtn.setText("Sent");
                addFreindBtn.setClickable(false);
            }
        });
        mDialog.show();
    }


}
