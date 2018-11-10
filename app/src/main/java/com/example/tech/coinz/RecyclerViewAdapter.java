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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.bumptech.glide.Glide;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> currency = new ArrayList<>();
    private ArrayList<String> values = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> currency, ArrayList<String> values, Context mContext) {
        this.values = values;
        this.currency= currency;
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

//        Glide.with(mContext).asBitmap().load(mImages.get(position)).into(viewHolder.image);
        viewHolder.txtCurrency.setText(currency.get(position));
        viewHolder.txtValue.setText(values.get(position));

//        viewHolder.offerLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: clicked on"+ mImageNames.get(position));
////               Toast.makeText(mContext, mImageNames.get(position), Toast.LENGTH_LONG).show();
//                Toast.makeText(mContext, mHelpNum.get(position), Toast.LENGTH_LONG).show();
//
//                Intent intent = new Intent(view.getContext(), SelectUserToHelpActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("helpNum", mHelpNum.get(position));
//                intent.putExtras(bundle);
//                mContext.startActivity(intent);
//
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return currency.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

            //        CircleImageView image;
            TextView txtCurrency;
            TextView txtValue;
            RelativeLayout offerLayout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

//            image = (CircleImageView) itemView.findViewById(R.id.image);

                txtCurrency = itemView.findViewById(R.id.txtCurrency);
                txtValue = itemView.findViewById(R.id.txtValue);
                offerLayout = itemView.findViewById(R.id.coin_layout);
            }
    }
}
