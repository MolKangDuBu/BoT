package com.example.bot_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {


    ImageButton Area01;
    ImageButton Area02;
    ImageButton Area03;
    ImageButton Area04;
    Intent intnet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        intnet  = new Intent(getApplicationContext(), Area_Main.class);
        Area01 = (ImageButton)findViewById(R.id.imb_Area01);
        Area02 = (ImageButton)findViewById(R.id.imb_Area02);
        Area03 = (ImageButton)findViewById(R.id.imb_Area03);
        Area04 = (ImageButton)findViewById(R.id.imb_Area04);

        //버튼에 따라 다른 인자값을 전달하여 몇번째 Area인지 확인
        Area01.setOnClickListener(new View.OnClickListener() {//Area01
            @Override
            public void onClick(View view) {

                startActivity(intnet);
            }
        });

        Area02.setOnClickListener(new View.OnClickListener() {//Area02
            @Override
            public void onClick(View view) {

                startActivity(intnet);

            }
        });

        Area03.setOnClickListener(new View.OnClickListener() {//Area03
            @Override
            public void onClick(View view) {

                startActivity(intnet);
            }
        });

        Area04.setOnClickListener(new View.OnClickListener() {//Area04
            @Override
            public void onClick(View view) {

                startActivity(intnet);
            }
        });
    }



}