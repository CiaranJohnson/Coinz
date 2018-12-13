package com.example.tech.coinz;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.bumptech.glide.Glide;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> currency = new ArrayList<>();
    private ArrayList<String> values = new ArrayList<>();
    private ArrayList<String> id = new ArrayList<>();
    private Context mContext;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private Double gold = 0.0;
    private Map<String, Object> userInformation = new HashMap<>();

    public RecyclerViewAdapter(ArrayList<String> currency, ArrayList<String> values, ArrayList<String> id, Context mContext) {
        this.id = id;
        this.values = values;
        this.currency = currency;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coin_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        String displayValue = values.get(position).substring(0,5);

        viewHolder.txtCurrency.setText(currency.get(position));
        viewHolder.txtValue.setText(displayValue);

        if(currency.get(position).equals("PENY")){
            viewHolder.coinSymbol.setImageResource(R.drawable.peny_symbol);
        } else if(currency.get(position).equals("DOLR")){
            viewHolder.coinSymbol.setImageResource(R.drawable.dolr_symbol);
        } else if(currency.get(position).equals("QUID")){
            viewHolder.coinSymbol.setImageResource(R.drawable.quid_symbol);
        } else {
            viewHolder.coinSymbol.setImageResource(R.drawable.shil_symbol);
        }

        viewHolder.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SelectUserActivity.class);
                intent.putExtra("Currency", currency.get(position));
                intent.putExtra("Value", values.get(position));
                intent.putExtra("ID", id.get(position));
                intent.putExtra("sendScreen", true);
                mContext.startActivity(intent);
            }
        });

        viewHolder.btnBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> coinInfo = new HashMap<>();
                coinInfo.put("ID", id.get(position));
                coinInfo.put("Currency", currency.get(position));
                coinInfo.put("Value", values.get(position));
                BankBackend.getBankCount(coinInfo, "CollectedCoins", mContext);
                id.remove(position);
                currency.remove(position);
                values.remove(position);
                notifyItemRemoved(position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return currency.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView coinSymbol;
        TextView txtCurrency;
        TextView txtValue;
        Button btnBank, sendBtn;
        RelativeLayout offerLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            coinSymbol = (ImageView) itemView.findViewById(R.id.coinSymbol);
            btnBank = (Button) itemView.findViewById(R.id.btnBank);
            sendBtn = (Button) itemView.findViewById(R.id.sendBtn);
            txtCurrency = (TextView) itemView.findViewById(R.id.txtCurrency);
            txtValue = (TextView) itemView.findViewById(R.id.txtValue);
            offerLayout = (RelativeLayout) itemView.findViewById(R.id.coin_layout);
        }
    }
}
