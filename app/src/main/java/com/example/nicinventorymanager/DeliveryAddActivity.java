package com.example.nicinventorymanager;

import android.app.DatePickerDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class DeliveryAddActivity extends AppCompatActivity {

    String sheetToken, userName, userImage, buttonTxt;
    Button addBtn, addImageBtn;
    TextView helmetClipsTv, addImageTv;
    ImageView imageViewUserImage;
    EditText editTextSite, editTextDate, editTextRemarks, editTextZoneTags, editTextLocators, editTextProxSensors, editTextGateways, editTextBlueTags, editTextChargingTrays, editTextSingleChargers, editTextPowerAdapters, editTextSingleCables, editTextCableSplitters, editTextHelmetClips;
    Calendar myCalendar;
    Bitmap rbitmap;

    private int PICK_IMAGE_REQUEST = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_delivery);

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        Intent intent = getIntent();
        buttonTxt = intent.getStringExtra("buttonTxt");

        editTextSite = (EditText)findViewById(R.id.siteDeliverEditText);
        editTextDate = (EditText)findViewById(R.id.dateDeliverEditText);
        editTextZoneTags = (EditText)findViewById(R.id.zoneTagsEditText);
        editTextLocators = (EditText)findViewById(R.id.locatorsEditText);
        editTextProxSensors = (EditText)findViewById(R.id.proxSensorEditText);
        editTextGateways = (EditText)findViewById(R.id.gatewayEditText);
        editTextBlueTags = (EditText)findViewById(R.id.blueTagEditText);
        editTextChargingTrays = (EditText)findViewById(R.id.chargingTrayEditText);
        editTextSingleChargers = (EditText)findViewById(R.id.singleChargerEditText);
        editTextPowerAdapters = (EditText)findViewById(R.id.powerAdapterEditText);
        editTextSingleCables = (EditText)findViewById(R.id.singleCableEditText);
        editTextCableSplitters = (EditText)findViewById(R.id.cableSplitterEditText);
        editTextHelmetClips = (EditText)findViewById(R.id.helmetClipsEditText);
        editTextRemarks = (EditText)findViewById(R.id.deliverRemarksEditText);

        helmetClipsTv = (TextView)findViewById(R.id.helmetClipsTv);
        addImageTv = (TextView)findViewById(R.id.imageTv);

        imageViewUserImage = (ImageView)findViewById(R.id.imageViewUserImage);

        addBtn = (Button)findViewById(R.id.deliveryAddBtn);
        addImageBtn = (Button)findViewById(R.id.addImageBtn);

        if(buttonTxt.equals("Returns")){
            editTextHelmetClips.setVisibility(View.GONE);
            helmetClipsTv.setVisibility(View.GONE);
            addImageTv.setVisibility(View.VISIBLE);
            addImageBtn.setVisibility(View.VISIBLE);
        }
        else{
            addImageTv.setVisibility(View.GONE);
            addImageBtn.setVisibility(View.GONE);
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextSite.getText().toString().equalsIgnoreCase(""))
                {
                    editTextSite.setError("Required field");
                }

                else if(editTextDate.getText().toString().equalsIgnoreCase(""))
                {
                    editTextSite.setError("Required field");
                }

                else if(editTextZoneTags.getText().toString().equalsIgnoreCase(""))
                {
                    editTextZoneTags.setError("Required field");
                }

                else if(editTextLocators.getText().toString().equalsIgnoreCase(""))
                {
                    editTextLocators.setError("Required field");
                }

                else if(editTextProxSensors.getText().toString().equalsIgnoreCase(""))
                {
                    editTextProxSensors.setError("Required field");
                }

                else if(editTextGateways.getText().toString().equalsIgnoreCase(""))
                {
                    editTextGateways.setError("Required field");
                }

                else if(editTextBlueTags.getText().toString().equalsIgnoreCase(""))
                {
                    editTextBlueTags.setError("Required field");
                }

                else if(editTextChargingTrays.getText().toString().equalsIgnoreCase(""))
                {
                    editTextChargingTrays.setError("Required field");
                }

                else if(editTextSingleChargers.getText().toString().equalsIgnoreCase(""))
                {
                    editTextSingleChargers.setError("Required field");
                }

                else if(editTextPowerAdapters.getText().toString().equalsIgnoreCase(""))
                {
                    editTextPowerAdapters.setError("Required field");
                }

                else if(editTextSingleCables.getText().toString().equalsIgnoreCase(""))
                {
                    editTextSingleCables.setError("Required field");
                }

                else if(editTextCableSplitters.getText().toString().equalsIgnoreCase(""))
                {
                    editTextCableSplitters.setError("Required field");
                }

                else if(editTextHelmetClips.getText().toString().equalsIgnoreCase("") && !buttonTxt.equals("Returns"))
                {
                    editTextHelmetClips.setError("Required field");
                }
                else {
                    if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        new SendRequest().execute();
                    }
                    else{
                        Toast.makeText(DeliveryAddActivity.this, "Please ensure you have a working internet connection!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        addImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        sheetToken = acct.getIdToken();
        userName = acct.getDisplayName();

        myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        editTextDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(DeliveryAddActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public class SendRequest extends AsyncTask<String, Void, String> {


        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{

                //Enter script URL Here
                URL url = new URL("https://script.google.com/macros/s/AKfycbw9ornLZdQ0MKdqtDeVEu1tbbFcr2lpyFFu378szHM5ekaluoORh7Usa8X-upe2JADO/exec");

                JSONObject postDataParams = new JSONObject();

                //int i;
                //for(i=1;i<=70;i++)


                //    String usn = Integer.toString(i);

                //Passing scanned code as parameter
                postDataParams.put("item", buttonTxt);
                postDataParams.put("site", editTextSite.getText().toString().trim());
                postDataParams.put("date", editTextDate.getText().toString().trim());
                postDataParams.put("zonetag", editTextZoneTags.getText().toString().trim());
                postDataParams.put("locator", editTextLocators.getText().toString().trim());
                postDataParams.put("proxsensor", editTextProxSensors.getText().toString().trim());
                postDataParams.put("gateway", editTextGateways.getText().toString().trim());
                postDataParams.put("bluetag", editTextBlueTags.getText().toString().trim());
                postDataParams.put("chargingtray", editTextChargingTrays.getText().toString().trim());
                postDataParams.put("singlecharger", editTextSingleChargers.getText().toString().trim());
                postDataParams.put("poweradapter", editTextPowerAdapters.getText().toString().trim());
                postDataParams.put("singlecable", editTextSingleCables.getText().toString().trim());
                postDataParams.put("cablesplitter", editTextCableSplitters.getText().toString().trim());
                postDataParams.put("helmetclip", editTextHelmetClips.getText().toString().trim());
                if(buttonTxt.equals("Returns")) {
                    postDataParams.put("image", userImage);
                }
                postDataParams.put("remark", editTextRemarks.getText().toString().trim());

                Log.e("params",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
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
            DeliveryAddActivity.this.finish();

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

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editTextDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                rbitmap = getResizedBitmap(bitmap,250);//Setting the Bitmap to ImageView
                userImage = getStringImage(rbitmap);
                imageViewUserImage.setImageBitmap(rbitmap);
                addImageBtn.setText("Change Image");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);

    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        return encodedImage;
    }

}