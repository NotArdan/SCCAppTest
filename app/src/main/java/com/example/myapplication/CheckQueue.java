package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;

import org.json.JSONObject;

import java.util.List;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;



public class CheckQueue {
    private Context context; // Save the context to use runOnUiThread
    private String  resultClass, confidenceLevel;
    private long startTime, endTime, latency;

    public CheckQueue(Context context, CognitoCachingCredentialsProvider credentialsProvider) {
        this.context = context;

        try{
            // start timer latency testing
            startTime = System.currentTimeMillis();

            // Initialize SQS client
            AmazonSQS sqsClient = new AmazonSQSClient(credentialsProvider);

            // URL of queue in AWS
            String queueUrl = "https://sqs.ap-southeast-2.amazonaws.com/585768157196/PredictResultsQueue.fifo";

            // Start polling in a background thread
            new Thread(() -> {
                ReceiveMessageRequest receiveRequest = new ReceiveMessageRequest()
                        .withQueueUrl(queueUrl)
                        .withMaxNumberOfMessages(1)
                        .withWaitTimeSeconds(5);

                while (true) {
                    try {
                        // Receive the latest message from SQS
                        ReceiveMessageResult result = sqsClient.receiveMessage(receiveRequest);
                        List<Message> messages = result.getMessages();
                        Log.d("CheckQueue", "Message received"+messages.size());

                        if (!messages.isEmpty()) {
                            //latency testing receive message from SQS
                            endTime = System.currentTimeMillis();
                            latency = endTime - startTime;
                            Log.d("Latency", "Client-Server Latency receive result: " + latency + " ms");

                            //get latest message available
                            Message message = messages.get(0);

                            // Get the result from the message body
                            String messageBody = message.getBody();

                            Log.d("CheckQueue", "Message body"+messageBody);

                            //boolean flag to check if message content retrieved
                            boolean messageProcessed = false;

                            //////////////////////WIP RESULTS ARE BOTH CLASS AND CONFIDENCE, GET BOTH
                            try{
                                //get string given specific parameter, if none found, fallback values
                                JSONObject json = new JSONObject(messageBody);
                                resultClass = json.optString("results", "Unknown");
                                confidenceLevel = json.optString("score", "N/A");
                                //set flag to true to allow delete message in SQS
                                messageProcessed = true;
                                Log.d("CheckQueue", "Result class:"+resultClass+", Confidence level:"+confidenceLevel);
                            } catch (Exception e){
                                resultClass = "Unknown error";
                                confidenceLevel = "N/A error";
                                Log.e("CheckQueue", "Error parsing json message", e);

                            }

                            if(messageProcessed == true){
                                // Delete the message from the queue if contents retrieved
                                sqsClient.deleteMessage(new DeleteMessageRequest()
                                        .withQueueUrl(queueUrl)
                                        .withReceiptHandle(message.getReceiptHandle()));
                                Log.d("CheckQueue", "Message deleted from SQS");
                            }


                            // Navigate to the second screen, CHECK IF WORKING PROPERLY
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(() -> {
                                Intent intent = new Intent(context, Output.class);
                                intent.putExtra("OC", resultClass);
                                intent.putExtra("CL", confidenceLevel);
                                context.startActivity(intent);
                            });

                            break; // Exit loop after handling the message
                        } else {
                            Toast.makeText(context, "Waiting for new message...", Toast.LENGTH_SHORT).show();
                            Log.d("CheckQueue", "Waiting for new message...");
                        }
                    } catch (Exception e) {
                        Log.e("CheckQueue", "Error polling SQS: ", e);
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(() -> Toast.makeText(context, "Checking for results...", Toast.LENGTH_SHORT).show());
                    }
                }
            }).start();
        } catch (Exception e){
            Log.e("CheckQueue", "error initializing SQS client", e);
            Toast.makeText(context, "Failed to initialize SQS Client: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}