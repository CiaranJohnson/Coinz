package com.example.tech.coinz;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    public static final String TAG = "GameActivity";
    ArrayList<String> prizes = new ArrayList<>();

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
        int[] cardOrder = new int[9];
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
            cardOrder[i] = position.get(result);
            position.remove(result);
            j--;
        }


        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reward = prizes.get(cardOrder[0]);
                GameBackend.getCoinsFromSpareChange(reward);
                changeImageButton(card1, reward);
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reward = prizes.get(cardOrder[1]);
                GameBackend.getCoinsFromSpareChange(reward);
                changeImageButton(card2, reward);
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reward = prizes.get(cardOrder[2]);
                GameBackend.getCoinsFromSpareChange(reward);
                changeImageButton(card3, reward);
            }
        });
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reward = prizes.get(cardOrder[3]);
                GameBackend.getCoinsFromSpareChange(reward);
                changeImageButton(card4, reward);
            }
        });
        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reward = prizes.get(cardOrder[4]);
                GameBackend.getCoinsFromSpareChange(reward);
                changeImageButton(card5, reward);
            }
        });
        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String reward = prizes.get(cardOrder[5]);
                GameBackend.getCoinsFromSpareChange(reward);
                changeImageButton(card6, reward);
            }
        });
        card7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reward = prizes.get(cardOrder[6]);
                GameBackend.getCoinsFromSpareChange(reward);
                changeImageButton(card7, reward);
            }
        });
        card8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reward = prizes.get(cardOrder[7]);
                GameBackend.getCoinsFromSpareChange(reward);
                changeImageButton(card8, reward);
            }
        });
        card9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reward = prizes.get(cardOrder[8]);
                GameBackend.getCoinsFromSpareChange(reward);
                changeImageButton(card9, reward);
            }
        });

    }

    private void changeImageButton(ImageButton imageButton, String reward){

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
