package com.example.myapplication;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class Output extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.output_layout); // Set the content view

        // Find views after setContentView() is called
        TextView preset = findViewById(R.id.preset);
        TextView PredictClass = (TextView) findViewById(R.id.OutputPrediction);
        TextView PredictClassInfo = (TextView) findViewById(R.id.OutputPredictionInfo);
        ImageView PredictClassImg = (ImageView) findViewById(R.id.OutputImage);

        // To retrieve string from previous screen (Cloud prediction output)
        Bundle extras = getIntent().getExtras();
        String OutputPredict = extras.getString("OC");

        try {
            getPredictionText(OutputPredict, PredictClass, PredictClassInfo, PredictClassImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class predictionsHandler{

        static class predictionInfo{
            String displayText, imageName, extraInfo;
            predictionInfo(String displayText, String imageName, String extraInfo){
                this.displayText = displayText;
                this.imageName = imageName;
                this.extraInfo = extraInfo;
            }

            public String getDisplayText() {
                return displayText;
            }

            public String getImageName() {
                return imageName;
            }

            public String getExtraInfo() {
                return extraInfo;
            }
        }

    }

    public void getPredictionText(String OutputPredict, TextView PredictClass,
                                         TextView PredictClassInfo, ImageView PredictClassImg) throws IOException{
        HashMap<String, predictionsHandler.predictionInfo> conditionText = new HashMap<>();
        conditionText.put("DyshidroticEczema", new predictionsHandler.predictionInfo("Dyshidrotic Eczema",
                "deczemaimg.jpg",
                ""));
        conditionText.put("Psoriasis", new predictionsHandler.predictionInfo("Psoriasis",
                "psoriasisimg.jpg",
                ""));
        conditionText.put("SeborrheicDermatitis", new predictionsHandler.predictionInfo("Seborrheic Dermatitis",
                "sdimg.jpg",
                ""));
        conditionText.put("Measles", new predictionsHandler.predictionInfo("Measles",
                "measlesimg.jpg",
                ""));
        conditionText.put("ChickenPox", new predictionsHandler.predictionInfo("Chicken Pox",
                "chickenPoximg.jpg",
                "test test info random text"));
        conditionText.put("ContactDermatitis", new predictionsHandler.predictionInfo("Contact Dermatitis",
                "cdimg.jpg",
                ""));
        conditionText.put("TineaPedis", new predictionsHandler.predictionInfo("Athlete's Foot",
                "tinepedisImg.jpg",
                ""));
        conditionText.put("Vitiligo", new predictionsHandler.predictionInfo("Vitiligo",
                "vitiligoimg.jpg",
                ""));
        conditionText.put("Miliaria", new predictionsHandler.predictionInfo("Miliaria",
                "miliariaimg.jpg",
                ""));
        conditionText.put("Scabies", new predictionsHandler.predictionInfo("Scabies",
                "scabiesimg.jpg",
                ""));
        conditionText.put("HealthySkin", new predictionsHandler.predictionInfo("Healthy Skin",
                "healthyimg.jpg",
                "To keep your skin healthy, you can do the following:\n" +
                        "\u2022 Apply moisturizer at least once a day\n" +
                        "\u2022 Avoid damage from UV, apply sunscreen if necessary and wear protective clothing\n" +
                        "\u2022 Avoid taking hot showers to prevent skin from drying\n" +
                        "\u2022 Stay hydrated to maintain moisture in the skin"));

        predictionsHandler.predictionInfo info= conditionText.getOrDefault(OutputPredict,
                new predictionsHandler.predictionInfo("Unknown Condition",
                        "",
                        "Please try to seek additional medical opinion"));

        PredictClass.setText(info.getDisplayText());
        PredictClassInfo.setText(info.getExtraInfo());
        String imageName = info.getImageName().replace(".jpg", ""); // Remove the extension if necessary
        int resId = getResources().getIdentifier(imageName.toLowerCase(), "drawable", getPackageName());

        // Set the image resource
        if (resId != 0) { // Check if the resource ID is valid
            PredictClassImg.setImageResource(resId);
        } else {
            // Handle the case where the resource is not found
            PredictClassImg.setImageResource(R.drawable.healthyimg); // Optional: set a default image
        }
    }

}
