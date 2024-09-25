package com.example.myapplication;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.CursorLoader;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;


import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private Uri selectedImageUri;
    private Bitmap selectedImageBitmap;
    private ImageView selectedImage;

    OkHttpClient client;
    String getURL = "";
    String postURL = "";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // permission to use gallery and camera
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, 112);
            }
        }

        //link buttons into xml buttons
        Button galleryBtn = findViewById(R.id.btnPickImageGallery);
        Button cameraBtn = findViewById(R.id.btnPickImageCamera);
        Button confirmBtn = findViewById(R.id.btnConfirmImage);

        //if gallery button is clicked
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                //create new Intent for image chosen
                //launch pickGalleryImage function
                pickGalleryImage.launch(galleryIntent);
            }
        });

        //if camera button is clicked
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check version of android
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //check android permissions
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_CODE);
                    } else {
                        //run if permissions ok
                        openCamera();
                    }
                } else {
                    openCamera();
                }
            }
        });

        //if confirm button is clicked
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //send image into google cloud platform using REST API endpoint thing

                try {
                    uploadObject();
                } catch (IOException e) {
                    Log.e("UploadError", "Error uploading object", e);
                    Toast.makeText(MainActivity.this, "Error confirming " + e.getMessage(), Toast.LENGTH_LONG).show();
                }


                // input for testing results screen
                //String OutPredict = "HealthySkin";
                //Intent i = new Intent(MainActivity.this, Output.class);
                //i.putExtra("OC", OutPredict);
                //startActivity(i);
            }
        });

    }

    //open camera function
    private void openCamera() {
        //create instance of contentvalues to hold image from camera
        ContentValues value = new ContentValues();
        //title and description of image
        value.put(MediaStore.Images.Media.TITLE, "New Picture");
        value.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        //get URI (location) of image
        selectedImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value);
        //create new intent for camera capture
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //pass image and uri to pickCameraImage
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
        pickCameraImage.launch(cameraIntent);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void uploadObject() throws IOException {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Starting uploadObject method", Toast.LENGTH_LONG).show());

        InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
        if (imageStream == null) {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Image stream not found", Toast.LENGTH_LONG).show());
            throw new FileNotFoundException("File not found: " + selectedImageUri.toString());
        }

        // Read the image stream into a byte array
        byte[] imageBytes = new byte[imageStream.available()];
        imageStream.read(imageBytes);

        try {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Uploading to nodejs server", Toast.LENGTH_LONG).show());

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            String fileExtension = getFileExtension(selectedImageUri);
            String contentType = getContentTypeFromExtension(fileExtension);

            // Create the request body
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("image", "testimage." + fileExtension,
                            RequestBody.create(imageBytes, MediaType.parse(contentType)))
                    .build();

            // Build the request
            Request request = new Request.Builder()
                    .url("http://192.168.1.8:3000/predict") // REPLACE WITH SERVER URL
                    .post(requestBody)
                    .build();

            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Executing request...", Toast.LENGTH_LONG).show());

            // Execute request asynchronously
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Unexpected code: " + response.code(), Toast.LENGTH_LONG).show());
                        throw new IOException("Unexpected code " + response);
                    }
                    String responseBody = response.body().string();
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Upload successful: " + responseBody, Toast.LENGTH_LONG).show());
                }
            });

        } catch (Exception e) {
            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } finally {
            imageStream.close();
        }
    }






    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        if (uri != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);

                if (columnIndex != -1) {
                    fileName = cursor.getString(columnIndex);
                }
                cursor.close();
            }
        }
        return fileName;

    }

    private String getFileExtension(Uri uri) {
        String fileName = getFileNameFromUri(uri);
        if (fileName != null) {
            int lastIndexOfDot = fileName.lastIndexOf(".");
            if (lastIndexOfDot != -1) {
                return fileName.substring(lastIndexOfDot + 1);
            }
        }
        return "jpg"; // Default extension if not found
    }

    private String getContentTypeFromExtension(String extension) {
        switch (extension.toLowerCase()) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            default:
                return "application/octet-stream";
            // Default content type for unknown extensions
        }
    }


    //for resizing preview
    public Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImageUri)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImageUri);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImageUri);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(img, selectedImageUri);
        return img;
    }

    //for calculating sample size
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    //rotation of bitmap if necessary, returns normal image according to exif metadata thing
    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImageUri) throws IOException {

        //DO NOT EDIT IDK HOW THIS WORKS
        String filePath = getRealPathFromUri(selectedImageUri); // Get the file path from the URI
        ExifInterface ei = new ExifInterface(filePath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        //switch case for exif shits and rotates it accordingly
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    //DO NOT DELETE IDK HOW THIS WORKS
    //get actual path from URI instead of using selectedImageURI.getpath()
    private String getRealPathFromUri(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
    }

    //rotates the bitmap accordingly from the switch case, returns the rotated bitmap
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    //function for camera image capture
    private final ActivityResultLauncher<Intent> pickCameraImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //check if result got something
                if (result.getResultCode() == RESULT_OK) {
                    try {
                        //save original bitmap in bitmap variable
                        selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        //save rotated bitmap in another variable if rotation is necessary
                        Bitmap modifiedImageBitmap = handleSamplingAndRotationBitmap(this, selectedImageUri);
                        //link image ViewDisplay to selectedImage and set bitmap to preview image
                        selectedImage = findViewById(R.id.imageViewDisplay);
                        selectedImage.setImageBitmap(modifiedImageBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    //gallery image pick function process thing
    private final ActivityResultLauncher<Intent> pickGalleryImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //if result gets something
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    //create new intent with data from result
                    Intent data = result.getData();
                    //if data is not empty
                    if (data != null && data.getData() != null) {
                        //get selected image URI from data
                        selectedImageUri = data.getData();
                        try {
                            //save Original Bitmap in bitmap variable
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            //save rotated bitmap in another variable if rotation is necessary
                            Bitmap modifiedImageBitmap = handleSamplingAndRotationBitmap(this, selectedImageUri);
                            //link imageViewDisplay to selectedImage and set bitmap to preview
                            selectedImage = findViewById(R.id.imageViewDisplay);
                            selectedImage.setImageBitmap(modifiedImageBitmap);
                        } catch (IOException e) {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "no data from result", Toast.LENGTH_LONG).show());
                            e.printStackTrace();
                        }
                    }
                } else
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Result null 1", Toast.LENGTH_LONG).show());

            });


}

