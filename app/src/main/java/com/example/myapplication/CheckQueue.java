package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

public class CheckQueue {
    private Context context; // Save the context to use runOnUiThread
    private String results;

    public CheckQueue(Context context) {
        this.context = context;

        // Initialize SQS client
        SqsClient sqsClient = SqsClient.builder().build();

        // URL of queue in AWS
        String queueUrl = "https://sqs.ap-southeast-2.amazonaws.com/585768157196/PredictResultsQueue.fifo";

        // Start polling in a background thread
        new Thread(() -> {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(1)
                    .waitTimeSeconds(20)
                    .build();

            while (true) {
                try {
                    // Receive the latest message from SQS
                    List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

                    if (!messages.isEmpty()) {
                        Message message = messages.get(0);

                        // Get the result from the message body
                        results = message.body();

                        // Delete the message from the queue
                        sqsClient.deleteMessage(DeleteMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .receiptHandle(message.receiptHandle())
                                .build());

                        // Navigate to the second screen, CHECK IF WORKING PROPERLY
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> {
                            Intent intent = new Intent(context, Output.class);
                            intent.putExtra("OC", results);
                            context.startActivity(intent);
                        });

                        break; // Exit loop after handling the message
                    } else {
                        Toast.makeText(context, "Waiting for new message...", Toast.LENGTH_SHORT).show();
                        Log.d("CheckQueue", "Waiting for new message...");
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Error polling SQS", Toast.LENGTH_SHORT).show();
                    Log.e("CheckQueue", "Error polling SQS: ", e);
                }
            }
        }).start();
    }
}