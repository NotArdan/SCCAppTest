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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;


import java.io.File;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;


import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;


//aws android imports?


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    private Uri selectedImageUri;
    private Bitmap selectedImageBitmap;
    private ImageView selectedImage;


    private S3Client s3Client;
    String filePath;

    //private AwsBasicCredentials creds = AwsBasicCredentials.create(Constants.ACCESS_ID, Constants.SECRET_KEY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // permission to use gallery and camera
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 112);
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
                try {
                    if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
                        if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                            String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission,112);
                        } else {
                            openCamera();
                        }
                    } else {
                        openCamera();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Error opening cam " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        //if confirm button is clicked
        //currently not working, previously working
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //send image into google cloud platform using OkHTTP endpoint thing
                try {
                    uploadObject();
                } catch (IOException e) {
                    Log.e("UploadError", "Error uploading object", e);
                    Toast.makeText(MainActivity.this, "Error confirming " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
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

    private S3Uploader s3Uploader;

    private void uploadObject() throws IOException {
        final String bucketName = "sccapp-testbucket";
        final String key = "imageuploads/uploadedimage."+getFileExtension(selectedImageUri);

        //call uploader class
        s3Uploader = new S3Uploader(this);

        //path to image file from gallery or cam
        File file = new File(filePath);
        s3Uploader.uploadFile(bucketName, key, file);

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
        filePath = getRealPathFromUri(selectedImageUri); // Get the file path from the URI
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

