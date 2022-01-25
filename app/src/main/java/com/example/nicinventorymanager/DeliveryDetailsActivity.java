package com.example.nicinventorymanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;
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

public class DeliveryDetailsActivity extends AppCompatActivity  {

    TextView helmetClipsTv, imageTv;
    ImageView imageViewUserImage;
    EditText editTextSite, editTextDate, editTextRemarks, editTextZoneTags, editTextLocators, editTextProxSensors, editTextGateways, editTextBlueTags, editTextChargingTrays, editTextSingleChargers, editTextPowerAdapters, editTextSingleCables, editTextCableSplitters, editTextHelmetClips;
    Button updateBtn, updateImageBtn;
    String Id, itemId, site, date, zoneTags, locators, proxSensors, gateways, blueTags, chargingTrays, singleChargers, powerAdapters, singleCables, cableSplitters, helmetClips, remarks, userImage, buttonTxt;
    Boolean deleting;
    ConnectivityManager connectivityManager;
    Calendar myCalendar;
    Bitmap rbitmap;

    private int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_delivery);

        connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        Intent intent = getIntent();
        Id = intent.getStringExtra("id");
        itemId = intent.getStringExtra("itemId");
        site = intent.getStringExtra("site");
        date = intent.getStringExtra("date");
        zoneTags = intent.getStringExtra("zonetag");
        locators = intent.getStringExtra("locator");
        proxSensors = intent.getStringExtra("proxsensor");
        gateways = intent.getStringExtra("gateway");
        blueTags = intent.getStringExtra("bluetag");
        chargingTrays = intent.getStringExtra("chargingtray");
        singleChargers = intent.getStringExtra("singlecharger");
        powerAdapters = intent.getStringExtra("poweradapter");
        singleCables = intent.getStringExtra("singlecable");
        cableSplitters = intent.getStringExtra("cablesplitter");
        remarks = intent.getStringExtra("remarks");
        buttonTxt = intent.getStringExtra("item");
        if(buttonTxt.equals("Delivery")) {
            helmetClips = intent.getStringExtra("helmetclip");
        }
        else if(buttonTxt.equals("Returns")){
            userImage = intent.getStringExtra("image");
        }

        editTextSite = (EditText)findViewById(R.id.siteDeliverUpdateEditText);
        editTextDate = (EditText)findViewById(R.id.dateDeliverUpdateEditText);
        editTextZoneTags = (EditText)findViewById(R.id.zoneTagsUpdateEditText);
        editTextLocators = (EditText)findViewById(R.id.locatorsUpdateEditText);
        editTextProxSensors = (EditText)findViewById(R.id.proxSensorUpdateEditText);
        editTextGateways = (EditText)findViewById(R.id.gatewayUpdateEditText);
        editTextBlueTags = (EditText)findViewById(R.id.blueTagUpdateEditText);
        editTextChargingTrays = (EditText)findViewById(R.id.chargingTrayUpdateEditText);
        editTextSingleChargers = (EditText)findViewById(R.id.singleChargerUpdateEditText);
        editTextPowerAdapters = (EditText)findViewById(R.id.powerAdapterUpdateEditText);
        editTextSingleCables = (EditText)findViewById(R.id.singleCableUpdateEditText);
        editTextCableSplitters = (EditText)findViewById(R.id.cableSplitterUpdateEditText);
        editTextHelmetClips = (EditText)findViewById(R.id.helmetClipsUpdateEditText);
        editTextRemarks = (EditText)findViewById(R.id.deliverRemarksUpdateEditText);

        helmetClipsTv = (TextView)findViewById(R.id.helmetClipsTv);
        imageTv = (TextView)findViewById(R.id.imageTv);

        imageViewUserImage = (ImageView)findViewById(R.id.imageViewUpdateUserImage);

        updateBtn = (Button)findViewById(R.id.deliveryUpdateBtn);
        updateImageBtn = (Button)findViewById(R.id.updateImageBtn);

        editTextSite.setText(site, TextView.BufferType.EDITABLE);
        editTextDate.setText(date, TextView.BufferType.EDITABLE);
        editTextZoneTags.setText(zoneTags, TextView.BufferType.EDITABLE);
        editTextLocators.setText(locators, TextView.BufferType.EDITABLE);
        editTextProxSensors.setText(proxSensors, TextView.BufferType.EDITABLE);
        editTextGateways.setText(gateways, TextView.BufferType.EDITABLE);
        editTextBlueTags.setText(blueTags, TextView.BufferType.EDITABLE);
        editTextChargingTrays.setText(chargingTrays, TextView.BufferType.EDITABLE);
        editTextSingleChargers.setText(singleChargers, TextView.BufferType.EDITABLE);
        editTextPowerAdapters.setText(powerAdapters, TextView.BufferType.EDITABLE);
        editTextSingleCables.setText(singleCables, TextView.BufferType.EDITABLE);
        editTextCableSplitters.setText(cableSplitters, TextView.BufferType.EDITABLE);
        if(buttonTxt.equals("Delivery")){
            editTextHelmetClips.setText(helmetClips, TextView.BufferType.EDITABLE);
        }
        else if(buttonTxt.equals("Returns")){
            Picasso.with(this).load(userImage).into(imageViewUserImage);
        }
        editTextRemarks.setText(remarks, TextView.BufferType.EDITABLE);

        deleting = false;

        if(buttonTxt.equals("Returns")){
            editTextHelmetClips.setVisibility(View.GONE);
            helmetClipsTv.setVisibility(View.GONE);
            imageTv.setVisibility(View.VISIBLE);
            imageViewUserImage.setVisibility(View.VISIBLE);
            updateImageBtn.setVisibility(View.VISIBLE);
        }
        else if(buttonTxt.equals("Delivery")){
            imageTv.setVisibility(View.GONE);
            imageViewUserImage.setVisibility(View.GONE);
            updateImageBtn.setVisibility(View.GONE);
        }

        if(imageViewUserImage.getDrawable() == null){
            updateImageBtn.setText("Add Image");
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
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
                        Toast.makeText(DeliveryDetailsActivity.this, "Please ensure you have a working internet connection!", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        updateImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

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
                new DatePickerDialog(DeliveryDetailsActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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
                postDataParams.put("item", buttonTxt);
                postDataParams.put("itemId", itemId);
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
                if(buttonTxt.equals("Delivery")) {
                    postDataParams.put("helmetclip", editTextHelmetClips.getText().toString().trim());
                }
                else if(buttonTxt.equals("Returns")) {
                    postDataParams.put("image", userImage);
                }
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
            DeliveryDetailsActivity.this.finish();
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
                AlertDialog.Builder builder = new AlertDialog.Builder(DeliveryDetailsActivity.this);

                builder.setCancelable(true);
                //builder.setTitle("Delete");
                builder.setMessage(Html.fromHtml("Are you sure you want to delete this delivery note?"));

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
                            Toast.makeText(DeliveryDetailsActivity.this, "Please ensure you have a working internet connection!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);

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
                updateImageBtn.setText("Change Image");
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