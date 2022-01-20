package com.example.nicinventorymanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanActivity extends AppCompatActivity {

    String buttonTxt;
    Button scanBtn, manualAddBtn;
    ActionBar actionBar;
    public static String scannedData;
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        final Activity activity = this;

        Intent intent = getIntent();
        buttonTxt = intent.getStringExtra("buttonTxt");

        actionBar = getSupportActionBar();
        actionBar.setTitle("Add new " + buttonTxt.toLowerCase());

        scanBtn = (Button)findViewById(R.id.scanBtn);
        manualAddBtn = (Button)findViewById(R.id.manualBtn);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setBeepEnabled(false);
                integrator.setCameraId(0);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        manualAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScanActivity.this, AddActivity.class);
                intent.putExtra("buttonTxt", buttonTxt);
                startActivity(intent);
                ScanActivity.this.finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            scannedData = result.getContents();
            if (scannedData != null) {
                Intent intent = new Intent(ScanActivity.this, AddActivity.class);
                intent.putExtra("buttonTxt", buttonTxt);
                startActivity(intent);
                ScanActivity.this.finish();

            } else {

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}


