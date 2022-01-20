package com.example.nicinventorymanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    ImageButton zoneTagBtn, proximityBtn, locatorBtn, gatewayBtn, blueTagBtn, deliveryBtn;
    TextView welcomeTv;
    String userName;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zoneTagBtn = (ImageButton)findViewById(R.id.zoneTagBtn);
        proximityBtn = (ImageButton)findViewById(R.id.proximitySensorBtn);
        locatorBtn = (ImageButton)findViewById(R.id.zoneLocatorBtn);
        gatewayBtn = (ImageButton)findViewById(R.id.gatewayBtn);
        blueTagBtn = (ImageButton)findViewById(R.id.blueTagBtn);
        deliveryBtn = (ImageButton)findViewById(R.id.deliveryBtn);

        welcomeTv = (TextView)findViewById(R.id.welcomeText);

        zoneTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                String buttonTxt = "Zone Tags";
                intent.putExtra("buttonTxt", buttonTxt);
                startActivity(intent);
            }
        });

        proximityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                String buttonTxt = "Proximity Sensors";
                intent.putExtra("buttonTxt", buttonTxt);
                startActivity(intent);
            }
        });

        locatorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                String buttonTxt = "Zone Locators";
                intent.putExtra("buttonTxt", buttonTxt);
                startActivity(intent);
            }
        });

        gatewayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                String buttonTxt = "Gateways";
                intent.putExtra("buttonTxt", buttonTxt);
                startActivity(intent);
            }
        });

        blueTagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                String buttonTxt = "Blue Tags";
                intent.putExtra("buttonTxt", buttonTxt);
                startActivity(intent);
            }
        });

        deliveryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseActionActivity.class);
                String buttonTxt = "Delivery";
                intent.putExtra("buttonTxt", buttonTxt);
                startActivity(intent);
            }
        });



        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        userName = acct.getDisplayName();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestScopes(new Scope("https://www.googleapis.com/auth/spreadsheets"))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        welcomeTv.setText("Welcome, " + userName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.signout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case R.id.delete:
                signOut();
                break;
            case R.id.searchItem:
                LinearLayout layout = new LinearLayout(this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setGravity(Gravity.CENTER_HORIZONTAL);
                final EditText input = new EditText(this);
                layout.setPadding(70, 0, 70, 0);
                layout.addView(input);

                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setView(layout);
                alert.setMessage(Html.fromHtml("Type in the <b>exact name</b> of the sheet you want to view (not case-sensitive)"));

// Set an EditText view to get user input

                //alert.setView(input);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(MainActivity.this, ListActivity.class);
                        String buttonTxt = input.getText().toString().trim();
                        intent.putExtra("buttonTxt", buttonTxt);
                        intent.putExtra("prevActivity", "misc");
                        startActivity(intent);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                alert.show();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "Signed out!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this, com.example.nicinventorymanager.SignInActivity.class));
                        MainActivity.this.finish();
                    }
                });
    }

}