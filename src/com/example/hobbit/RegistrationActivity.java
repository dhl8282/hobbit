package com.example.hobbit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class RegistrationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Intent returnIntent = new Intent();
        Log.d("hobbit", "it shows this activity is called");
        String username = getIntent().getExtras().get("SOURCE").toString();
        Log.d("hobbit", "username is " + username);
        String result = "this is the result";
//        returnIntent.putExtra("result",result);
//        setResult(RESULT_OK,returnIntent);
//        finish();
    }
}
