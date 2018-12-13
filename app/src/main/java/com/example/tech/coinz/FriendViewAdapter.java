package com.example.tech.coinz;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendViewAdapter extends RecyclerView.Adapter<FriendViewAdapter.ViewHolder>{



    private static final String TAG = "FriendViewAdapter";

    private ArrayList<String> userID = new ArrayList<>();
    private ArrayList<String> displayName = new ArrayList<>();
    private ArrayList<String> email = new ArrayList<>();
    private String coinID;
    private Context mContext;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;


    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();


    public FriendViewAdapter(ArrayList<String> userID, ArrayList<String> displayName, ArrayList<String> email, String coinID,  Context mContext) {
        this.userID = userID;
        this.displayName = displayName;
        this.email = email;
        this.coinID = coinID;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");


        viewHolder.txtDisplayName.setText(displayName.get(position));
        viewHolder.txtEmail.setText(email.get(position));


        storageReference.child("images/profile_picture/" + userID.get(position)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(viewHolder.profilePicture);
                //profilePicture.setImageURI(uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Failed: Profile Picture");
            }
        });


        viewHolder.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Sent Coin", Toast.LENGTH_LONG).show();
                if (coinID.length()>0){
                    BankBackend.sendCoin(coinID, userID.get(position));
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return email.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView txtEmail;
        TextView txtDisplayName;
        CircleImageView profilePicture;
        RelativeLayout friendLayout;
        Button sendBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sendBtn = (Button) itemView.findViewById(R.id.sendBtn);
            profilePicture = (CircleImageView) itemView.findViewById(R.id.profilePicture);
            txtEmail = (TextView) itemView.findViewById(R.id.txtEmail);
            txtDisplayName = (TextView) itemView.findViewById(R.id.DisplayName);
            friendLayout = (RelativeLayout) itemView.findViewById(R.id.friend_layout);

        }
    }

}
