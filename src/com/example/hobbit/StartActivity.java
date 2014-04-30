package com.example.hobbit;

import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class StartActivity extends Activity {

	private Button startButton;
	private EditText inputIdText;
	private EditText inputPwdText;
	private String inputId = "";
	private String inputPwd = "";
	final Context context = this;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
