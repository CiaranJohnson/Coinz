package com.example.tech.coinz;

import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReceivedViewAdapter extends RecyclerView.Adapter<ReceivedViewAdapter.ViewHolder> {

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

    public ReceivedViewAdapter(ArrayList<String> currency, ArrayList<String> values, ArrayList<String> id, Context mContext) {
        this.id = id;
        this.values = values;
        this.currency = currency;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ReceivedViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.received_coin_item, parent, false);
        ReceivedViewAdapter.ViewHolder holder = new ReceivedViewAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReceivedViewAdapter.ViewHolder viewHolder, final int position) {
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


        viewHolder.btnBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> coinInfo = new HashMap<>();
                coinInfo.put("ID", id.get(position));
                coinInfo.put("Currency", currency.get(position));
                coinInfo.put("Value", values.get(position));
                Backend.getBankCount(coinInfo, "RecievedCoins", mContext);
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
        Button btnBank;
        RelativeLayout offerLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            coinSymbol = (ImageView) itemView.findViewById(R.id.coinSymbol);
            btnBank = (Button) itemView.findViewById(R.id.btnBank);
            txtCurrency = (TextView) itemView.findViewById(R.id.txtCurrency);
            txtValue = (TextView) itemView.findViewById(R.id.txtValue);
            offerLayout = (RelativeLayout) itemView.findViewById(R.id.coin_layout);
        }
    }


}
