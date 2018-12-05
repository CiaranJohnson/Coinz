package com.example.tech.coinz;

import android.content.Context;
import android.net.Uri;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendRequestViewAdapter extends RecyclerView.Adapter<FriendRequestViewAdapter.ViewHolder>{



    private static final String TAG = "FriendViewAdapter";

    private ArrayList<String> userID = new ArrayList<>();
    private ArrayList<String> displayName = new ArrayList<>();
    private ArrayList<String> email = new ArrayList<>();
    Context mContext;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;


    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference = storage.getReference();


    public FriendRequestViewAdapter(ArrayList<String> userID, ArrayList<String> displayName, ArrayList<String> email, Context mContext) {
        this.userID = userID;
        this.displayName = displayName;
        this.email = email;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public FriendRequestViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_list_item, parent, false);
        FriendRequestViewAdapter.ViewHolder holder = new FriendRequestViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendRequestViewAdapter.ViewHolder viewHolder, final int position) {
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


        viewHolder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("UID", userID.get(position));
                userInfo.put("Email", email.get(position));
                userInfo.put("DisplayName", displayName.get(position));
                Backend.moveToFriends(userInfo);

            }
        });


    }

    @Override
    public int getItemCount() {
        return email.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Button addBtn;
        TextView txtEmail;
        TextView txtDisplayName;
        CircleImageView profilePicture;
        RelativeLayout friendLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            addBtn = (Button) itemView.findViewById(R.id.addBtn);
            profilePicture = (CircleImageView) itemView.findViewById(R.id.profilePicture);
            txtEmail = (TextView) itemView.findViewById(R.id.txtEmail);
            txtDisplayName = (TextView) itemView.findViewById(R.id.DisplayName);
            friendLayout = (RelativeLayout) itemView.findViewById(R.id.friend_request_layout);
        }
    }
}
