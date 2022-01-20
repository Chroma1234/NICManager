package com.example.nicinventorymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class ChooseActionActivity extends AppCompatActivity {

    String buttonTxt;
    Button viewReturnsBtn, viewDeliveryBtn;
    ActionBar actionBar;

    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooseaction);

        viewReturnsBtn = (Button)findViewById(R.id.viewReturnsBtn);
        viewDeliveryBtn = (Button)findViewById(R.id.viewDeliveryBtn);

        actionBar = getSupportActionBar();

        Intent intent = getIntent();
        buttonTxt = intent.getStringExtra("buttonTxt");

        actionBar.setTitle(buttonTxt);

        viewReturnsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseActionActivity.this, DeliveryListActivity.class);
                buttonTxt = "Returns";
                intent.putExtra("buttonTxt", buttonTxt);
                startActivity(intent);
            }
        });

        viewDeliveryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseActionActivity.this, DeliveryListActivity.class);
                buttonTxt = "Delivery";
                intent.putExtra("buttonTxt", buttonTxt);
                startActivity(intent);
            }
        });
    }
}
