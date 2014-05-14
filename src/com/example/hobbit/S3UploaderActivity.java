/*
 * Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.example.hobbit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3UploaderActivity extends Activity {
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // This sample App is for demonstration purposes only.
    // It is not secure to embed your credentials into source code.
    // DO NOT EMBED YOUR CREDENTIALS IN PRODUCTION APPS.
    // We offer two solutions for getting credentials to your mobile App.
    // Please read the following article to learn about Token Vending Machine:
    // * http://aws.amazon.com/articles/Mobile/4611615499399490
    // Or consider using web identity federation:
    // * http://aws.amazon.com/articles/Mobile/4617974389850313
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private AmazonS3Client s3Client = new AmazonS3Client(
            new BasicAWSCredentials(Constants.AWS_ACCESS_KEY_ID,
                    Constants.AWS_SECRET_KEY));

    private static final int PHOTO_SELECTED = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s3_uploader);

        s3Client.setRegion(Region.getRegion(Regions.US_WEST_2));
        String filePath = (String) getIntent().getExtras().get("path");
        String id = (String) getIntent().getExtras().get("id");
        new S3PutObjectTask(filePath, id).execute();
    }

    // Display an Alert message for an error or failure.
    protected void displayAlert(String title, String message) {

        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle(title);
        confirm.setMessage(message);

        confirm.setNegativeButton(
                S3UploaderActivity.this.getString(R.string.ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        confirm.show().show();
    }

    protected void displayErrorAlert(String title, String message) {

        AlertDialog.Builder confirm = new AlertDialog.Builder(this);
        confirm.setTitle(title);
        confirm.setMessage(message);

        confirm.setNegativeButton(
                S3UploaderActivity.this.getString(R.string.ok),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        S3UploaderActivity.this.finish();
                    }
                });
        confirm.show().show();
    }

    private class S3PutObjectTask extends AsyncTask<Uri, Void, S3TaskResult> {

        ProgressDialog dialog;
        private String mFilePath, mId;

        public S3PutObjectTask(String filePath, String id) {
            mFilePath = filePath;
            mId = id;
       }

        protected void onPreExecute() {
            dialog = new ProgressDialog(S3UploaderActivity.this);
            dialog.setMessage(S3UploaderActivity.this
                    .getString(R.string.uploading));
            dialog.setCancelable(false);
            dialog.show();
        }

        protected S3TaskResult doInBackground(Uri... uris) {
            InputStream in = null;

            try {
                in = new FileInputStream(mFilePath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");

            S3TaskResult result = new S3TaskResult();

            // Put the image data into S3.
            try {
//              s3Client.createBucket(Constants.getPictureBucket());
                PutObjectRequest por = new PutObjectRequest(
                        Constants.PICTURE_BUCKET, mId,
                        in, metadata);
                // Make the picture public
                por.setCannedAcl(CannedAccessControlList.PublicRead);
                result.setUri(makeUrl(mId));
                s3Client.putObject(por);
            } catch (Exception exception) {

                result.setErrorMessage(exception.getMessage());
            }
            return result;
        }

        protected void onPostExecute(S3TaskResult result) {
            dialog.dismiss();
            if (result.getErrorMessage() != null) {
                displayErrorAlert(
                        S3UploaderActivity.this
                                .getString(R.string.upload_failure_title),
                        result.getErrorMessage());
            }
        }
    }

    private String makeUrl(String id) {
        return Constants.HTTP_US_WEST_2 + "/" + Constants.PICTURE_BUCKET + "/" + id;
    }

    private class S3TaskResult {
        String errorMessage = null;
        String uri = null;

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}
