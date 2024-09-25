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
            PredictInfo(OutputPredict, PredictClass, PredictClassInfo, PredictClassImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void PredictInfo(String OutPredict, TextView PredictClass,
                            TextView PredictClassInfo, ImageView PredictClassImg) throws IOException {

        InputStream inputStream;
        Drawable drawable;
        Bitmap bitmap;
        switch (OutPredict){
            case "DyshidroticEczema":
                PredictClass.setText("Dyshidrotic Eczema");
                inputStream = getAssets().open("DEczemaImg.jpg");
                drawable = Drawable.createFromStream(inputStream, null);
                PredictClassImg.setImageDrawable(drawable);


                break;
            case "Measles":
                PredictClass.setText("Measles");
                inputStream = getAssets().open("MeaslesImg.jpg");
                drawable = Drawable.createFromStream(inputStream, null);
                PredictClassImg.setImageDrawable(drawable);
                break;
            case "ChickenPox":
                PredictClass.setText("Chicken Pox");
                inputStream = getAssets().open("ChickenPoxImg.jpg");
                drawable = Drawable.createFromStream(inputStream, null);
                PredictClassImg.setImageDrawable(drawable);
                break;
            case "TineaPedis":
                PredictClass.setText("Athelete's Foot");
                inputStream = getAssets().open("TineaPedisImg.jpg");
                drawable = Drawable.createFromStream(inputStream, null);
                PredictClassImg.setImageDrawable(drawable);
                break;
            case "Vitiligo":
                PredictClass.setText("Vitiligo");
                inputStream = getAssets().open("VitiligoImg.jpg");
                drawable = Drawable.createFromStream(inputStream, null);
                PredictClassImg.setImageDrawable(drawable);
                break;
            case "HealthySkin":
                PredictClass.setText("Healthy Skin");
                PredictClassInfo.setText("To keep your skin healthy, you can do the following:\n" +
                        "\u2022 Apply moisturizer at least once a day\n" +
                        "\u2022 Avoid damage from UV, apply sunscreen if necessary and wear protective clothing\n" +
                        "\u2022 Avoid taking hot showers to prevent skin from drying\n" +
                        "\u2022 Stay hydrated to maintain moisture in the skin");
                PredictClassImg.setImageResource(R.drawable.healthyimg);

                break;
            default:
                PredictClass.setText("Unknown condition");
                break;
        }
    }

}
