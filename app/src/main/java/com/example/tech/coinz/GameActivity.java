package com.example.tech.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    public static final String TAG = "GameActivity";
    ArrayList<String> prizes = new ArrayList<>();
    String[] cardOrder;

    ImageButton card1, card2, card3, card4, card5, card6, card7, card8, card9;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        card1 = (ImageButton)findViewById(R.id.card1);
        card2 = (ImageButton)findViewById(R.id.card2);
        card3 = (ImageButton)findViewById(R.id.card3);
        card4 = (ImageButton)findViewById(R.id.card4);
        card5 = (ImageButton)findViewById(R.id.card5);
        card6 = (ImageButton)findViewById(R.id.card6);
        card7 = (ImageButton)findViewById(R.id.card7);
        card8 = (ImageButton)findViewById(R.id.card8);
        card9 = (ImageButton)findViewById(R.id.card9);


        prizes.add("QUID");
        prizes.add("SHIL");
        prizes.add("DOLR");
        prizes.add("PENY");
        prizes.add("AllCoins");
        prizes.add("NoCoins");
        prizes.add("NoCoins");
        prizes.add("NoCoins");
        prizes.add("NoCoins");


        ArrayList<Integer> position = new ArrayList<>();
        cardOrder = new String[9];
        position.add(0);
        position.add(1);
        position.add(2);
        position.add(3);
        position.add(4);
        position.add(5);
        position.add(6);
        position.add(7);
        position.add(8);

        Random r = new Random();
        int j = 9;

        for (int i = 0; i<9; i++){
            int result = r.nextInt(j);
            cardOrder[i] = prizes.get(result);
            prizes.remove(result);
            j--;
        }



        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GameBackend.getCoinsFromSpareChange(cardOrder[0]);
                changeImageButton(card1, cardOrder[0]);
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GameBackend.getCoinsFromSpareChange(cardOrder[1]);
                changeImageButton(card2, cardOrder[1]);
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GameBackend.getCoinsFromSpareChange(cardOrder[2]);
                changeImageButton(card3, cardOrder[2]);
            }
        });
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GameBackend.getCoinsFromSpareChange(cardOrder[3]);
                changeImageButton(card4, cardOrder[3]);
            }
        });
        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GameBackend.getCoinsFromSpareChange(cardOrder[4]);
                changeImageButton(card5, cardOrder[4]);
            }
        });
        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GameBackend.getCoinsFromSpareChange(cardOrder[5]);
                changeImageButton(card6, cardOrder[5]);
            }
        });
        card7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GameBackend.getCoinsFromSpareChange(cardOrder[6]);
                changeImageButton(card7, cardOrder[6]);
            }
        });
        card8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GameBackend.getCoinsFromSpareChange(cardOrder[7]);
                changeImageButton(card8, cardOrder[7]);
            }
        });
        card9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GameBackend.getCoinsFromSpareChange(cardOrder[8]);
                changeImageButton(card9, cardOrder[8]);
            }
        });


    }

    private void changeImageButton(ImageButton imageButton, String reward){

        if (reward.equals("DOLR")) {
            imageButton.setImageResource(R.mipmap.ic_dollar_foreground);
            Toast.makeText(GameActivity.this, "Dollar Coins Added to Bank!", Toast.LENGTH_LONG).show();
        } else if (reward.equals("PENY")) {
            imageButton.setImageResource(R.mipmap.ic_peny_foreground);
            Toast.makeText(GameActivity.this, "Penny Coins Added to Bank!", Toast.LENGTH_LONG).show();
        } else if(reward.equals("QUID")){
            imageButton.setImageResource(R.mipmap.ic_quid_foreground);
            Toast.makeText(GameActivity.this, "Quid Coins Added to Bank!", Toast.LENGTH_LONG).show();
        } else if (reward.equals("SHIL")){
            imageButton.setImageResource(R.mipmap.ic_shilling_foreground);
            Toast.makeText(GameActivity.this, "Shilling Coins Added to Bank!", Toast.LENGTH_LONG).show();
        } else if(reward.equals("AllCoins")){
            imageButton.setImageResource(R.mipmap.ic_all_coins_foreground);
            Toast.makeText(GameActivity.this, "All Coins Added to Bank!", Toast.LENGTH_LONG).show();
        } else {
            imageButton.setImageResource(R.mipmap.ic_no_coins_foreground);
            Toast.makeText(GameActivity.this, "No Coins Added to Bank...", Toast.LENGTH_LONG).show();
        }

        setCardsToUnclickable();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                ImageButton[] buttons = {card1, card2, card3, card4, card5, card6, card7,card8, card9};
                for (int i = 0; i<9;i++){
                    flipTheRest(buttons[i], cardOrder[i]);
                }
            }
        }, 3000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(GameActivity.this, MapActivity.class);
                startActivity(intent);
            }
        },5000);


    }

    private void setCardsToUnclickable(){
        card9.setClickable(false);
        card8.setClickable(false);
        card7.setClickable(false);
        card6.setClickable(false);
        card5.setClickable(false);
        card4.setClickable(false);
        card3.setClickable(false);
        card2.setClickable(false);
        card1.setClickable(false);
    }

    private void flipTheRest(ImageButton imageButton, String reward){
        if (reward.equals("DOLR")) {
            imageButton.setImageResource(R.mipmap.ic_dollar_foreground);
        } else if (reward.equals("PENY")) {
            imageButton.setImageResource(R.mipmap.ic_peny_foreground);
        } else if(reward.equals("QUID")){
            imageButton.setImageResource(R.mipmap.ic_quid_foreground);
        } else if (reward.equals("SHIL")){
            imageButton.setImageResource(R.mipmap.ic_shilling_foreground);
        } else if(reward.equals("AllCoins")){
            imageButton.setImageResource(R.mipmap.ic_all_coins_foreground);
        } else {
            imageButton.setImageResource(R.mipmap.ic_no_coins_foreground);
        }
    }


}
