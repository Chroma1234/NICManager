package com.example.nicinventorymanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class AddActivity extends AppCompatActivity {
    
    String scannedData = com.example.nicinventorymanager.ScanActivity.scannedData,sheetToken, userName, buttonTxt;
    String[] statuses = { "", "Working", "Physical Damage", "No Power" };
    Button addBtn;
    TextView hwTv, fwTv;
    EditText idEditText, hwEditText, fwEditText, remarksEditText, siteEditText;
    AutoCompleteTextView statusEditText;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        Intent intent = getIntent();
        buttonTxt = intent.getStringExtra("buttonTxt");

        hwTv = (TextView)findViewById(R.id.zoneNameTv);
        fwTv = (TextView)findViewById(R.id.zoneIdTv);

        idEditText = (EditText)findViewById(R.id.idEditText);
        hwEditText = (EditText)findViewById(R.id.zoneNameEditText);
        fwEditText = (EditText)findViewById(R.id.zoneIdEditText);
        remarksEditText = (EditText)findViewById(R.id.remarksEditText);
        siteEditText = (EditText)findViewById(R.id.siteEditText);
        statusEditText = (AutoCompleteTextView) findViewById(R.id.statusEditText);

        addBtn = (Button)findViewById(R.id.addBtn);
        if(scannedData != null) {
            idEditText.setText(scannedData, TextView.BufferType.EDITABLE);
        }

        if(buttonTxt.equals("Zone Tags") || buttonTxt.equals("Gateways")){
            hwEditText.setVisibility(View.VISIBLE);
            fwEditText.setVisibility(View.VISIBLE);
            hwTv.setVisibility(View.VISIBLE);
            fwTv.setVisibility(View.VISIBLE);
            hwTv.setText("Hardware Version");
            fwTv.setText("Firmware Version");
        }
        else if(buttonTxt.equals("Zone Locators")){
            hwEditText.setVisibility(View.VISIBLE);
            fwEditText.setVisibility(View.VISIBLE);
            hwTv.setVisibility(View.VISIBLE);
            fwTv.setVisibility(View.VISIBLE);
            hwTv.setText("Zone Name");
            fwTv.setText("Zone ID");
        }
        else{
            hwTv.setVisibility(View.GONE);
            fwTv.setVisibility(View.GONE);
            hwEditText.setVisibility(View.GONE);
            fwEditText.setVisibility(View.GONE);
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idEditText.getText().toString().equalsIgnoreCase(""))
                {
                    idEditText.setError("Required field");
                }
                else if(siteEditText.getText().toString().equalsIgnoreCase(""))
                {
                    siteEditText.setError("Required field");
                }
                else {
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        new SendRequest().execute();
                    }
                    else{
                        Toast.makeText(AddActivity.this, "Please ensure you have a working internet connection!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice, statuses);
        //Find TextView control
        //Set the number of characters the user must type before the drop down list is shown
        statusEditText.setThreshold(0);
        //Set the adapter
        statusEditText.setAdapter(adapter);

        statusEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    statusEditText.showDropDown();

            }
        });

        statusEditText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                statusEditText.showDropDown();
                return false;
            }
        });

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        sheetToken = acct.getIdToken();
        userName = acct.getDisplayName();
    }

    public class SendRequest extends AsyncTask<String, Void, String> {


        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{

                //Enter script URL Here
                URL url = new URL("hhttps://script.google.com/macros/s/AKfycbw9ornLZdQ0MKdqtDeVEu1tbbFcr2lpyFFu378szHM5ekaluoORh7Usa8X-upe2JADO/exec");

                JSONObject postDataParams = new JSONObject();

                //int i;
                //for(i=1;i<=70;i++)


                //    String usn = Integer.toString(i);

                //Passing scanned code as parameter

                postDataParams.put("sdata",idEditText.getText().toString().trim());
                if(buttonTxt.equals("Zone Tags") || buttonTxt.equals("Gateways") || buttonTxt.equals("Zone Locators")) {
                    postDataParams.put("hw", hwEditText.getText().toString().trim());
                    postDataParams.put("fw", fwEditText.getText().toString().trim());
                }
                postDataParams.put("site", siteEditText.getText().toString().trim());
                postDataParams.put("status", statusEditText.getText().toString().trim());
                postDataParams.put("remark", remarksEditText.getText().toString().trim());
                postDataParams.put("item", buttonTxt);

                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), "Successfully added!",
                    Toast.LENGTH_LONG).show();
            AddActivity.this.finish();

        }

    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}