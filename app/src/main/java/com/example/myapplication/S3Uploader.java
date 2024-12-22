package com.example.myapplication;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

public class S3Uploader {
    private CognitoCachingCredentialsProvider credentialsProvider;
    private TransferUtility transferUtility;
    private Context context; // Save the context to use runOnUiThread

    public S3Uploader(Context context) {
        this.context = context;

        // Initialize the Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "ap-southeast-2:fc42a298-1285-4a2d-a35e-0886a26cb56f", // Identity Pool ID
                Regions.AP_SOUTHEAST_2 // Region
        );

        // Initialize the TransferUtility
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
        transferUtility = TransferUtility.builder()
                .context(context)
                .s3Client(s3Client)
                .build();
    }

    public void uploadFile(String bucketName, String key, File file) {
        // Start the upload
        TransferObserver observer = transferUtility.upload(bucketName, key, file);

        // Set up a listener to monitor the upload progress
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                runOnUiThread(() -> {
                    if (state == TransferState.COMPLETED) {
                        Toast.makeText(context, "Upload completed!", Toast.LENGTH_SHORT).show();
                        Log.d("S3Uploader", "Upload completed.");
                    } else if (state == TransferState.FAILED) {
                        Toast.makeText(context, "Upload failed!", Toast.LENGTH_SHORT).show();
                        Log.e("S3Uploader", "Upload failed.");
                    }
                });
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                double progress = (double) bytesCurrent / (double) bytesTotal * 100;
                runOnUiThread(() -> Toast.makeText(context, "Upload progress: " + (int) progress + "%", Toast.LENGTH_SHORT).show());
                Log.d("S3Uploader", "Upload progress: " + progress + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                runOnUiThread(() -> Toast.makeText(context, "Error during upload: " + ex.getMessage(), Toast.LENGTH_LONG).show());
                Log.e("S3Uploader", "Error during upload", ex);
            }
        });
    }

    private void runOnUiThread(Runnable action) {
        if (context instanceof MainActivity) {
            ((MainActivity) context).runOnUiThread(action);
        }
    }
}
