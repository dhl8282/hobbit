package com.example.hobbit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

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
				verifyIdPwd(inputId, inputPwd);
				Intent intent = new Intent(context, MainMenuActivity.class);
                startActivity(intent);
			}
		});
    }

    private void verifyIdPwd(String inputId, String inputPwd) {
		// TODO Auto-generated method stub

	}
}
