package com.example.nicinventorymanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
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

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;
    TextView itemCount;
    SimpleAdapter adapter;
    ProgressDialog loading;
    EditText itemSearch;
    String buttonTxt, prevActivity;
    ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        buttonTxt = intent.getStringExtra("buttonTxt");
        prevActivity = intent.getStringExtra("prevActivity");

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
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbw5P_KpnVd88uz_7XR7OCKrBcC3e_UFt4ahyPyf8YmN5xql0JlozMEQS6zErHdy98GnmA/exec?item="+buttonTxt,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                        itemSearch.setText("");
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ListActivity.this, "Please ensure you have a working internet connection!", Toast.LENGTH_LONG).show();
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

                JSONObject jo = jarray.getJSONObject(i);
                String hw = null;
                String fw = null;

                String itemId = jo.getString("itemId");
                String id = jo.getString("id");
                String site = jo.getString("site");
                String status = jo.getString("status");
                String remarks = jo.getString("remarks");
                String columnName = jo.getString("columnName");
                if(buttonTxt.equals("Zone Tags") || buttonTxt.equals("Gateways") || buttonTxt.equals("Zone Locators"))
                {
                    hw = jo.getString("hw");
                    fw = jo.getString("fw");
                }

                HashMap<String, String> item = new HashMap<>();
                    item.put("itemId", itemId);
                    item.put("id", id);
                    item.put("site", site);
                    item.put("status", status);
                    item.put("remarks", remarks);
                    item.put("columnName", columnName);
                    if(buttonTxt.equals("Zone Tags") || buttonTxt.equals("Gateways") || buttonTxt.equals("Zone Locators"))
                    {
                        item.put("hw", hw);
                        item.put("fw", fw);
                    }

                list.add(item);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
            if(buttonTxt.equals("Zone Tags") || buttonTxt.equals("Gateways") || buttonTxt.equals("Zone Locators")){
                adapter = new SimpleAdapter(this, list, R.layout.list_row,
                        new String[]{"id", "site", "status", "remarks", "itemId", "columnName", "hw", "fw"}, new int[]{R.id.siteId, R.id.itemSite, R.id.itemStatus, R.id.itemRemarks});
            }
            else{
            adapter = new SimpleAdapter(this, list, R.layout.list_row,
                    new String[]{"id", "site", "status", "remarks", "itemId", "columnName"}, new int[]{R.id.siteId, R.id.itemSite, R.id.itemStatus, R.id.itemRemarks});
            }


        listView.setAdapter(adapter);
        loading.dismiss();

        itemCount.setText("Total " + (buttonTxt.toLowerCase()) + ": " + listView.getAdapter().getCount());

        if(listView.getAdapter().getCount() == 0 && prevActivity != null){
            Toast.makeText(ListActivity.this, Html.fromHtml("Please type in the <b>exact name</b> of the sheet you want to view and that the sheet has data!"), Toast.LENGTH_LONG).show();
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
                                itemCount.setText("Total " + (buttonTxt.toLowerCase()) + ": " + adapter.getCount());
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
        String hw = null;
        String fw = null;
        String site;
        String status;
        String remarks;
        String columnName;
        Intent intent = new Intent(this, DetailsActivity.class);
        HashMap<String,String> map =(HashMap)parent.getItemAtPosition(position);
            itemId = map.get("itemId");
            Id = map.get("id");
        if(buttonTxt.equals("Zone Tags") || buttonTxt.equals("Gateways") || buttonTxt.equals("Zone Locators")) {
            hw = map.get("hw");
            fw = map.get("fw");
        }
            site = map.get("site");
            status = map.get("status");
            remarks = map.get("remarks");
            columnName = map.get("columnName");

        // String sno = map.get("sno").toString();

        // Log.e("SNO test",sno);
            intent.putExtra("itemId", itemId);
            intent.putExtra("id", Id);
            intent.putExtra("site", site);
            intent.putExtra("status", status);
            intent.putExtra("remarks", remarks);
            intent.putExtra("columnName", columnName);
            intent.putExtra("buttonTxt", buttonTxt);
            if(buttonTxt.equals("Zone Tags") || buttonTxt.equals("Gateways") || buttonTxt.equals("Zone Locators")){
                intent.putExtra("hw", hw);
                intent.putExtra("fw", fw);
            }


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
                Intent intent = new Intent(ListActivity.this, ScanActivity.class);
                intent.putExtra("buttonTxt", buttonTxt);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);

    }

}
