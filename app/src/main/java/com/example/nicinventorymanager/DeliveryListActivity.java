package com.example.nicinventorymanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class DeliveryListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {


    ListView listView;
    TextView itemCount;
    SimpleAdapter adapter;
    ProgressDialog loading;
    EditText itemSearch;
    String buttonTxt;
    ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        buttonTxt = intent.getStringExtra("buttonTxt");

        actionBar = getSupportActionBar();
        actionBar.setTitle(buttonTxt + " Listing");

        listView = (ListView)findViewById(R.id.lv_clips);
        itemCount = (TextView)findViewById(R.id.itemCount);
        itemSearch = (EditText)findViewById(R.id.itemSearch);

        listView.setOnItemClickListener(this);

        getItems();

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getItems();
                pullToRefresh.setRefreshing(false);
            }
        });
        if(buttonTxt.equals("Delivery")) {
            itemSearch.setHint("Search site or delivery date...");
        }
        else if (buttonTxt.equals("Returns")){
            itemSearch.setHint("Search site or return date...");
        }
    }

    @Override
    public void onRestart()
    {  // After a pause OR at startup
        super.onRestart();
        getItems();
    }




    private void getItems() {

        loading =  ProgressDialog.show(this,"Loading...","Please wait...",false,true);
        loading.setCancelable(false);
        loading.setCanceledOnTouchOutside(false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxcbsTA6YsqyRaWbFoe3LECDyVvlL6PzIxN0iIgJFrZn5cGizh_XUouQuRxi_7_JxSGOg/exec?item="+buttonTxt,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        itemSearch.setText("");
                        parseItems(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(DeliveryListActivity.this, "Please ensure you have a working internet connection!", Toast.LENGTH_LONG).show();
                        loading.dismiss();
                    }
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }


    private void parseItems(String jsonResposnce) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");


            for (int i = 0; i < jarray.length(); i++) {

                String fixedDay = null;
                String month = null;
                String year = null;
                String helmetClip = null;
                String image = null;
                int dayInt;

                JSONObject jo = jarray.getJSONObject(i);

                String itemId = jo.getString("itemId");
                String date = jo.getString("date");
                if(date.length() != 0){
                    String day = date.substring(8, 10);
                    if(Integer.parseInt(day) < 31) {
                        dayInt = Integer.parseInt(day) + 1;
                    }
                    else
                    {
                        dayInt = Integer.parseInt(day);
                    }
                    fixedDay = Integer.toString(dayInt);
                    month = date.substring(5, 7);
                    year = date.substring(0, 4);
                }
                String site = jo.getString("site");
                String remarks = jo.getString("remarks");
                String zoneTag = jo.getString("zonetag");
                String locator = jo.getString("locator");
                String proxSensor = jo.getString("proxsensor");
                String gateway = jo.getString("gateway");
                String blueTag = jo.getString("bluetag");
                String chargingTray = jo.getString("chargingtray");
                String singleCharger = jo.getString("singlecharger");
                String powerAdapter = jo.getString("poweradapter");
                String singleCable = jo.getString("singlecable");
                String cableSplitter = jo.getString("cablesplitter");
                if(buttonTxt.equals("Delivery")) {
                    helmetClip = jo.getString("helmetclip");
                }
                else if(buttonTxt.equals("Returns")) {
                    image = jo.getString("image");
                }

                HashMap<String, String> item = new HashMap<>();
                    item.put("itemId", itemId);
                    item.put("date", fixedDay + "/" + month + "/" + year);
                    item.put("site", site);
                    item.put("remarks", remarks);
                    item.put("zonetag", zoneTag);
                    item.put("locator", locator);
                    item.put("proxsensor", proxSensor);
                    item.put("gateway", gateway);
                    item.put("bluetag", blueTag);
                    item.put("chargingtray", chargingTray);
                    item.put("singlecharger", singleCharger);
                    item.put("poweradapter", powerAdapter);
                    item.put("singlecable", singleCable);
                    item.put("cablesplitter", cableSplitter);
                    if(buttonTxt.equals("Delivery")) {
                        item.put("helmetclip", helmetClip);
                    }
                    else if(buttonTxt.equals("Returns")) {
                        item.put("image", image);
                    }

                list.add(item);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(buttonTxt.equals("Delivery")) {
            adapter = new SimpleAdapter(this, list, R.layout.list_row_delivery,
                    new String[]{"site", "date", "remarks", "zonetag", "locator", "proxsensor", "gateway", "bluetag", "chargingtray", "singlecharger", "poweradapter", "singlecable", "cablesplitter", "helmetclip", "itemId"}, new int[]{R.id.deliverySite, R.id.deliveryDate, R.id.deliveryRemarks});
        }
        else if(buttonTxt.equals("Returns")) {
            adapter = new ReturnsListAdapter(this, list, R.layout.list_row_delivery,
                    new String[]{"site", "date", "remarks", "image", "zonetag", "locator", "proxsensor", "gateway", "bluetag", "chargingtray", "singlecharger", "poweradapter", "singlecable", "cablesplitter", "itemId"}, new int[]{R.id.deliverySite, R.id.deliveryDate, R.id.deliveryRemarks, R.id.returnIv});
        }
        else {
            adapter = new SimpleAdapter(this, list, R.layout.list_row_delivery,
                    new String[]{"site", "date", "remarks", "zonetag", "locator", "proxsensor", "gateway", "bluetag", "chargingtray", "singlecharger", "poweradapter", "singlecable", "cablesplitter", "itemId"}, new int[]{R.id.deliverySite, R.id.deliveryDate, R.id.deliveryRemarks});
        }

        listView.setAdapter(adapter);
        loading.dismiss();

        if(buttonTxt.equals("Delivery")) {
            itemCount.setText("Total deliveries : " + listView.getAdapter().getCount());
        }
        else if (buttonTxt.equals("Returns")){
            itemCount.setText("Total returns : " + listView.getAdapter().getCount());
        }

        itemSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                adapter.getFilter().filter(charSequence);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(buttonTxt.equals("Delivery")) {
                                    itemCount.setText("Total deliveries : " + listView.getAdapter().getCount());
                                }
                                else if (buttonTxt.equals("Returns")){
                                    itemCount.setText("Total returns : " + listView.getAdapter().getCount());
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String itemId;
        String Id;
        String site;
        String remarks;
        String date;
        String zoneTag;
        String locator;
        String proxSensor;
        String gateway;
        String blueTag;
        String chargingTray;
        String singleCharger;
        String powerAdapter;
        String singleCable;
        String cableSplitter;
        String helmetClip = null;
        String image = null;

        Intent intent;
        intent = new Intent(this, DeliveryDetailsActivity.class);

        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
            itemId = map.get("itemId");
            Id = map.get("id");
            site = map.get("site");
            remarks = map.get("remarks");
            date = map.get("date");
            zoneTag = map.get("zonetag");
            locator = map.get("locator");
            proxSensor = map.get("proxsensor");
            gateway = map.get("gateway");
            blueTag = map.get("bluetag");
            chargingTray = map.get("chargingtray");
            singleCharger = map.get("singlecharger");
            powerAdapter = map.get("poweradapter");
            singleCable = map.get("singlecable");
            cableSplitter = map.get("cablesplitter");
            if(buttonTxt.equals("Delivery")) {
                helmetClip = map.get("helmetclip");
            }
            else if(buttonTxt.equals("Returns")) {
                image = map.get("image");
            }

        // String sno = map.get("sno").toString();

        // Log.e("SNO test",sno);
            intent.putExtra("itemId", itemId);
            intent.putExtra("id", Id);
            intent.putExtra("site", site);
            intent.putExtra("date", date);
            intent.putExtra("zonetag", zoneTag);
            intent.putExtra("locator", locator);
            intent.putExtra("proxsensor", proxSensor);
            intent.putExtra("gateway", gateway);
            intent.putExtra("bluetag", blueTag);
            intent.putExtra("chargingtray", chargingTray);
            intent.putExtra("singlecharger", singleCharger);
            intent.putExtra("poweradapter", powerAdapter);
            intent.putExtra("singlecable", singleCable);
            intent.putExtra("cablesplitter", cableSplitter);
            if(buttonTxt.equals("Delivery")) {
                intent.putExtra("helmetclip", helmetClip);
            }
            else if(buttonTxt.equals("Returns")) {
                intent.putExtra("image", image);
            }
            intent.putExtra("remarks", remarks);
            intent.putExtra("item", buttonTxt);


        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case R.id.delete:
                Intent intent = new Intent(DeliveryListActivity.this, DeliveryAddActivity.class);
                intent.putExtra("buttonTxt", buttonTxt);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);

    }


}
