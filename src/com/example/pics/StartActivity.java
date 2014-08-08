package com.example.pics;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.example.hobbit.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class StartActivity extends FragmentActivity {

    private static final String TAG = "hobbit" + StartActivity.class.getSimpleName();
    private MainFragment mainFragment;
    private Button startButton;
    private EditText inputIdText;
    private EditText inputPwdText;
    private String inputId = "";
    private String inputPwd = "";
    final Context context = this;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //generateHashKeyForFB();
        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, mainFragment).commit();
        } else {
            // Or set the fragment from restored state info
            mainFragment = (MainFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
        setContentView(R.layout.activity_login);
        //addKeyListener();
    }

//    private void addKeyListener() {
//        inputIdText = (EditText) findViewById(R.id.inputId);
//        inputPwdText = (EditText) findViewById(R.id.inputPwd);
//        startButton = (Button) findViewById(R.id.startButton);
//
//        startButton.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                inputId = inputIdText.getText().toString();
//                inputPwd = inputPwdText.getText().toString();
//                Intent intent = new Intent(context, EnterMissionActivity.class);
//                startActivity(intent);
//            }
//        });
//    }

    private void generateHashKeyForFB() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG,Base64.encodeToString(md.digest(),
                         Base64.DEFAULT));
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }
}
