package com.example.nicinventorymanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class DetailsActivity extends AppCompatActivity  {

    TextView columnNameTv, hwTv, fwTv;
    EditText editTextId, editTextHw, editTextFw, editTextSite, editTextRemarks;
    AutoCompleteTextView editTextStatus;
    Button updateBtn;
    String Id, hw, fw, site, status, remarks, itemId, buttonTxt, columnName;
    String[] statuses = { "", "Working", "Physical Damage", "No Power" };
    Boolean deleting;
    ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit);

        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        Intent intent = getIntent();
        Id = intent.getStringExtra("id");
        site = intent.getStringExtra("site");
        status = intent.getStringExtra("status");
        remarks = intent.getStringExtra("remarks");
        itemId = intent.getStringExtra("itemId");
        columnName = intent.getStringExtra("columnName");
        buttonTxt = intent.getStringExtra("buttonTxt");
        if(buttonTxt.equals("Zone Tags") || buttonTxt.equals("Gateways") || buttonTxt.equals("Zone Locators")) {
            hw = intent.getStringExtra("hw");
            fw = intent.getStringExtra("fw");
        }

        columnNameTv = (TextView)findViewById(R.id.columnNameTv);
        hwTv = (TextView)findViewById(R.id.hwTv);
        fwTv = (TextView)findViewById(R.id.fwTv);

        editTextId = (EditText)findViewById(R.id.siteDeliverUpdateEditText);
        editTextHw = (EditText)findViewById(R.id.hwUpdateEditText);
        editTextFw = (EditText)findViewById(R.id.fwUpdateEditText);
        editTextSite = (EditText)findViewById(R.id.dateDeliverUpdateEditText);
        editTextRemarks = (EditText)findViewById(R.id.locatorsUpdateEditText);
        editTextStatus = (AutoCompleteTextView)findViewById(R.id.zoneTagsUpdateEditText);

        updateBtn = (Button)findViewById(R.id.deliveryUpdateBtn);

        columnNameTv.setText(columnName);

        editTextId.setText(Id, TextView.BufferType.EDITABLE);
        if(buttonTxt.equals("Zone Tags") || buttonTxt.equals("Gateways")){
            editTextHw.setVisibility(View.VISIBLE);
            editTextFw.setVisibility(View.VISIBLE);
            hwTv.setVisibility(View.VISIBLE);
            fwTv.setVisibility(View.VISIBLE);
            hwTv.setText("Hardware Version");
            fwTv.setText("Firmware Version");
            editTextHw.setText(hw, TextView.BufferType.EDITABLE);
            editTextFw.setText(fw, TextView.BufferType.EDITABLE);
        }
        else if(buttonTxt.equals("Zone Locators")){
            editTextHw.setVisibility(View.VISIBLE);
            editTextFw.setVisibility(View.VISIBLE);
            hwTv.setVisibility(View.VISIBLE);
            fwTv.setVisibility(View.VISIBLE);
            hwTv.setText("Zone Name");
            fwTv.setText("Zone ID");
            editTextHw.setText(hw, TextView.BufferType.EDITABLE);
            editTextFw.setText(fw, TextView.BufferType.EDITABLE);
        }
        else{
            hwTv.setVisibility(View.GONE);
            fwTv.setVisibility(View.GONE);
            editTextHw.setVisibility(View.GONE);
            editTextFw.setVisibility(View.GONE);
        }
        editTextSite.setText(site, TextView.BufferType.EDITABLE);
        editTextRemarks.setText(remarks, TextView.BufferType.EDITABLE);
        editTextStatus.setText(status, TextView.BufferType.EDITABLE);

        deleting = false;

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextId.getText().toString().equalsIgnoreCase(""))
                {
                    editTextId.setError("Required field");
                }

                else {
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        new SendRequest().execute();
                    }
                    else{
                        Toast.makeText(DetailsActivity.this, "Please ensure you have a working internet connection!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_singlechoice, statuses);
        //Find TextView control
        //Set the number of characters the user must type before the drop down list is shown
        editTextStatus.setThreshold(0);
        //Set the adapter
        editTextStatus.setAdapter(adapter);

        editTextStatus.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editTextStatus.showDropDown();

            }
        });

        editTextStatus.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editTextStatus.showDropDown();
                return false;
            }
        });

    }

    public class SendRequest extends AsyncTask<String, Void, String> {


        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{

                URL url = null;

                //Enter script URL Here
                if(deleting == false) {
                    url = new URL("https://script.google.com/macros/s/AKfycbyNWhBMAgNe2cTgDXJrn5WcKNrjoTCLylhjw8E3tB8SjtuNdwkRUyXKqyi5nO1yWzlE/exec?item=" + buttonTxt);
                }
                else if (deleting)
                {
                    url = new URL("https://script.google.com/macros/s/AKfycbxMdQGrWJaxBEmErDtzbJRsyH-c6bEyrbytd0AkCE1re8w9x2g8RtwiOZU6XEPf81V8Ww/exec?item=" + buttonTxt);
                }

                JSONObject postDataParams = new JSONObject();

                //int i;
                //for(i=1;i<=70;i++)


                //    String usn = Integer.toString(i);

                //Passing scanned code as parameter
                postDataParams.put("itemId", itemId);
                postDataParams.put("sdata", editTextId.getText().toString().trim());
                if(buttonTxt.equals("Zone Tags") || buttonTxt.equals("Gateways") || buttonTxt.equals("Zone Locators")){
                    postDataParams.put("hw", editTextHw.getText().toString().trim());
                    postDataParams.put("fw", editTextFw.getText().toString().trim());
                }
                postDataParams.put("site", editTextSite.getText().toString().trim());
                postDataParams.put("status", editTextStatus.getText().toString().trim());
                postDataParams.put("remark", editTextRemarks.getText().toString().trim());


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
            if(deleting == false) {
                Toast.makeText(getApplicationContext(), "Successfully updated!",
                        Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Successfully deleted!",
                        Toast.LENGTH_LONG).show();
            }
            DetailsActivity.this.finish();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailsActivity.this);

                builder.setCancelable(true);
                //builder.setTitle("Delete");
                builder.setMessage(Html.fromHtml("Are you sure you want to delete <b>" + Id + "</b>?"));

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleting =   true;
                        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                            //we are connected to a network
                            new SendRequest().execute();
                        }
                        else{
                            Toast.makeText(DetailsActivity.this, "Please ensure you have a working internet connection!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

}