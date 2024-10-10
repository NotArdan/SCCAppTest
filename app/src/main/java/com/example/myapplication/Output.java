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

    //PLEASE EDIT OUTPUTPREDICT STRING TO MATCH MODEL OUTPUT
    public void getPredictionText(String OutputPredict, TextView PredictClass,
                                         TextView PredictClassInfo, ImageView PredictClassImg) throws IOException{
        HashMap<String, predictionsHandler.predictionInfo> conditionText = new HashMap<>();
        conditionText.put("DyshidroticEczema", new predictionsHandler.predictionInfo("Dyshidrotic Eczema",
                "deczemaimg.jpg",
                "To treat dyshidrotic eczema, do the following:\n\n"+
                        "• Apply moisturizing lotion or cream to treat the dry skin\n"+
                        "• Remove accessories that may cause excessive sweating\n"+
                        "• Take appropriate medications such as corticosteroid cream or ointment\n"+
                        "• Avoiding exposure to certain substances and possible allergens\n"+
                        "• Treatment approach may vary depending on the severity of condition, consider dermatologist consultation"));

        conditionText.put("Psoriasis", new predictionsHandler.predictionInfo("Psoriasis",
                "psoriasisimg.jpg",
                "To treat psoriasis, do the following:\n\n"+
                        "• Apply topical treatment in affected areas (corticosteroids, retinoids, etc.)\n"+
                        "• Exposure to sunlight (or heliotherapy might improve psoriasis\n"+
                        "• Take appropriate medications prescribed by your doctor\n"+
                        "• Treatment approach may vary depending on the severity of condition, consider dermatologist consultation"));

        conditionText.put("SeborrheicDermatitis", new predictionsHandler.predictionInfo("Seborrheic Dermatitis",
                "sdimg.jpg",
                "To treat seborrheic dermatitis, do the following:\n\n"+
                        "• Apply medications that have antifungal properties to reduce rash, itch, and scale\n"+
                        "• Corticosteroid can also be used to reduce the flare-up\n"+
                        "• Changing of lifestyle might also improve the condition (reducing stress)\n"+
                        "• Treatment approach may vary depending on the severity of condition, consider dermatologist consultation"));

        conditionText.put("Measles", new predictionsHandler.predictionInfo("Measles",
                "measlesimg.jpg",
                "To treat measles, do the following:\n\n"+
                        "• Affected individual without immunity can be given measles vaccine within 72 hours of exposure to the virus\n"+
                        "• Additional medication can be taken to reduce accompanying symptoms of measles (fever reduces, etc.)\n"+
                        "• Isolate and prevent contact the affected individual to prevent transmission of virus\n"+
                        "• Sanitize the surfaces that the infected individual has interacted with to prevent the virus from spreading"));

        conditionText.put("ChickenPox", new predictionsHandler.predictionInfo("Chicken Pox",
                "chickenPoximg.jpg",
                "To treat chicken pox, do the following:\n\n"+
                        "• Antihistamines can be used to help reduce itching\n"+
                        "• Calamine can be applied on the skin to also reduce the itching\n"+
                        "• Isolate and prevent contact the affected individual to prevent transmission of virus\n"+
                        "• The condition will eventually disappear within a week\n"+
                        "• If the infected individual is considered high-risk (pregnant, infant, weakened immune system), consider doctor consultation"));

        conditionText.put("ContactDermatitis", new predictionsHandler.predictionInfo("Contact Dermatitis",
                "cdimg.jpg",
                "To treat contact dermatitis, do the following:\n\n"+
                        "• Identify what may have caused the irritation, and avoid or minimize exposure to it\n"+
                        "• Topical treatment can be used to treat the affected areas\n"+
                        "• Antihistamines can be used to help reduce itching\n"+
                        "• Over-the-counter itch creams and ointments can also be used to relieve the symptoms"+
                        "• In severe cases where the affected individual has trouble breathing, consider going to the ER"));

        conditionText.put("TineaPedis", new predictionsHandler.predictionInfo("Athlete's Foot",
                "tinepedisImg.jpg",
                "To treat athlete's foot, do the following:\n\n"+
                        "• Topical treatment with antifungal properties can be used to treat the condition\n"+
                        "• Oral antifungal medication can also be used\n"+
                        "• For prevention, feet should be washed at least twice a day and use footwear with proper ventilation\n"+
                        "• For oral medication, refer to a doctor for the necessary prescription"));

        conditionText.put("Vitiligo", new predictionsHandler.predictionInfo("Vitiligo",
                "vitiligoimg.jpg",
                "To treat vitiligo, do the following:\n\n"+
                        "• There is no actual treatment for vitiligo, but methods to reduce unevenness of skin color is possible\n"+
                        "• Corticosteroid cream can be used to return color to white patches of skin\n"+
                        "• Use of light (or phototherapy) can help return color to the skin\n"+
                        "• Depigmentation can also be done to remove color from dark areas of the skin\n"+
                        "• Consider consulting with a dermatologist for some of the treatments listed above"));

        conditionText.put("Miliaria", new predictionsHandler.predictionInfo("Miliaria",
                "miliariaimg.jpg",
                "To treat miliaria, do the following:\n\n"+
                        "• Calamine lotion can be applied to reduce the itching of affected areas\n"+
                        "• Mild topical steroids can also be used\n"+
                        "• Various methods that can reduce or prevent excessive sweating can be done for prevention\n"+
                        "• Wear breathable clothes and avoid wearing excessive and tight clothing"));

        conditionText.put("Scabies", new predictionsHandler.predictionInfo("Scabies",
                "scabiesimg.jpg",
                "To treat scabies, do the following:\n\n"+
                        "• Topical creams (scabicides) can be applied on affected areas\n"+
                        "• Oral medications can also be taken (Ivermectin)\n"+
                        "• Antihistamines and corticosteroid creams can be used to relief symptoms\n"+
                        "• Affected individuals must avoid skin-to-skin contact to prevent transmission\n"+
                        "• Sanitize the clothes and items used by the infested person to remove possible mites\n"+
                        "• Consider consulting with a dermatologist for the treatments listed above"));

        conditionText.put("HealthySkin", new predictionsHandler.predictionInfo("Healthy Skin",
                "healthyimg.jpg",
                "To keep your skin healthy, you can do the following:\n" +
                        "• Apply moisturizer at least once a day\n" +
                        "• Avoid damage from UV, apply sunscreen if necessary and wear protective clothing\n" +
                        "• Avoid taking hot showers to prevent skin from drying\n" +
                        "• Stay hydrated to maintain moisture in the skin"));

        predictionsHandler.predictionInfo info= conditionText.getOrDefault(OutputPredict,
                new predictionsHandler.predictionInfo("Unknown Condition",
                        "",
                        "Please try to seek additional medical opinion to ensure accurate identification of the condition."));

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
