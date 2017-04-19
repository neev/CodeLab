package com.test.neerajal.codelab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class LandingPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    public void stripe_integration (View view){

        Intent stripe = new Intent(this,MainActivity.class);
        startActivity(stripe);

    }

    public void braintree_integration (View view){

        Intent braintree = new Intent(this,Braintree_Checkout.class);
        startActivity(braintree);

    }

    public void DropinUI (View view){

       Intent braintree = new Intent(this,Braintree_DropIn_UI.class);
        startActivity(braintree);

    }

}
