package com.example.hobbit;

import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class StartActivity extends FragmentActivity {

    private MainFragment mainFragment;
	private Button startButton;
	private EditText inputIdText;
	private EditText inputPwdText;
	private String inputId = "";
	private String inputPwd = "";
	final Context context = this;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_start);


//        if (savedInstanceState == null) {
//            // Add the fragment on initial activity setup
//            mainFragment = new MainFragment();
//            getSupportFragmentManager().beginTransaction()
//                    .add(android.R.id.content, mainFragment).commit();
//        } else {
//            // Or set the fragment from restored state info
//            mainFragment = (MainFragment) getSupportFragmentManager()
//                    .findFragmentById(android.R.id.content);
//        }
        setContentView(R.layout.activity_start);
        addKeyListener();
    }

    private void addKeyListener() {

    	inputIdText = (EditText) findViewById(R.id.inputId);
    	inputPwdText = (EditText) findViewById(R.id.inputPwd);
    	startButton = (Button) findViewById(R.id.startButton);

    	startButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				inputId = inputIdText.getText().toString();
				inputPwd = inputPwdText.getText().toString();
				Log.d("test", "id is + " + inputId);
				Log.d("test", "pwd is + " + inputPwd);
				VerifyUserTask task = new VerifyUserTask();
			    task.execute();
				Intent intent = new Intent(context, MainMenuActivity.class);
                startActivity(intent);
			}
		});
    }

    private class VerifyUserTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            verifyIdPwd(inputId, inputPwd);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
//          textView.setText(result);
        }
    }

    private void verifyIdPwd(String inputId, String inputPwd) {
        try {
            Log.d("hobbitStart", "start mongo");
            MongoClientURI uri = new MongoClientURI("mongodb://admin:admin@ds029640.mongolab.com:29640/hobbitdb");
            MongoClient client = new MongoClient(uri);
            Log.d("hobbitStart", "DB is " + client.toString());
            DB db = client.getDB(uri.getDatabase());
            Log.d("hobbitStart", "mongo db connected successfully");
            DBCollection coll = db.getCollection("users");
            Log.d("hobbitStart", "mongo db collection connected successfully");
            Log.d("hobbitStart", "mongo db count is " + coll.getCount());
            DBCursor cursor = coll.find();
            while (cursor.hasNext()) {
                Log.d("hobbitStart", "user name is @@@@@ " + cursor.next());
            }
        } catch (UnknownHostException e) {
             System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
	}

    private void generateHashKeyForFB() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("hobbit",Base64.encodeToString(md.digest(),
                         Base64.DEFAULT));

            }
        } catch (NameNotFoundException e) {

            e.printStackTrace();

        } catch (NoSuchAlgorithmException ex) {

            ex.printStackTrace();
        }
    }
}
