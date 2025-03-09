package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
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
        TextView OutputPredProb = (TextView) findViewById(R.id.OutputPredProb);

        // To retrieve string from previous screen (Cloud prediction output)
        Bundle extras = getIntent().getExtras();
        String OutputPredict = extras.getString("OC");
        String OutputProb = extras.getString("CL");

        try {
            getPredictionText(OutputPredict, PredictClass, PredictClassInfo, PredictClassImg, OutputProb, OutputPredProb);
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
                                         TextView PredictClassInfo, ImageView PredictClassImg, String OutputProb, TextView OutputPredProb) throws IOException{
        HashMap<String, predictionsHandler.predictionInfo> conditionText = new HashMap<>();
        conditionText.put("DEczema", new predictionsHandler.predictionInfo("Dyshidrotic Eczema",
                "deczemaimg.jpg",
                "More information about dyshidrotic eczema:\n\n"+
                        "• Dyshidrotic eczema is a skin condition that causes small, itchy, fluid-filled" +
                        "blisters, typically on the palms of the hands, sides of the fingers, and soles of the feet." +
                        "The blisters can be painful, and the skin may crack, peel, or become scaly as it heals.\n"+
                        "• Apply moisturizing lotion or cream to treat the dry skin\n"+
                        "• Remove accessories that may cause excessive sweating\n"+
                        "• Avoiding exposure to certain substances and possible allergens\n"+
                        "• Treatment approach may vary depending on the severity of condition, consider dermatologist consultation"));

        conditionText.put("Psoriasis", new predictionsHandler.predictionInfo("Psoriasis",
                "psoriasisimg.jpg",
                "More information about Psoriasis:\n\n" +
                        "• Psoriasis is a chronic autoimmune skin condition that causes rapid buildup of" +
                        "skin cells, leading to scaling, inflammation, and redness. It often appears as thick, silvery scales" +
                        "and red patches on the skin, but it can vary in severity and presentation.\n"+
                        "• Apply topical treatment in affected areas (corticosteroids, retinoids, etc.)\n"+
                        "• Take appropriate medications prescribed by your doctor\n"+
                        "• Treatment approach may vary depending on the severity of condition, consider dermatologist consultation"));

        conditionText.put("SDermatitis", new predictionsHandler.predictionInfo("Seborrheic Dermatitis",
                "sdimg.jpg",
                "More information about Seborrheic Dermatitis:\n\n" +
                        "• Seborrheic dermatitis is a common, chronic skin condition that mainly affects" +
                        "areas rich in oil glands, like the scalp, face, and upper chest. It causes redness, " +
                        "greasy scales, and dandruff-like flakes.\n"+
                        "• Apply medications that have antifungal properties to reduce rash, itch, and scale\n"+
                        "• Reducing stress and fatigue might improve the condition.\n"+
                        "• Treatment approach may vary depending on the severity of condition, consider dermatologist consultation"));

        conditionText.put("Measles", new predictionsHandler.predictionInfo("Measles",
                "measlesimg.jpg",
                "More information about Measles:\n\n" +
                        "• Measles is a highly contagious viral infection caused by the measles virus." +
                        "Symptoms are high fever, cough, runny nose, red/watery eyes, followed by a red, blotchy" +
                        "rash that starts on the face and spreads downwards. Small white spots may appear inside the mouth.\n"+
                        "• Medication can be taken to reduce accompanying symptoms of measles.\n"+
                        "• Isolate and prevent contact with the affected individual to prevent transmission of virus\n"+
                        "• Sanitize the surfaces that the infected individual has interacted with to prevent the virus from spreading\n" +
                        "• Consult a doctor for a more precise treatment."));

        conditionText.put("CPox", new predictionsHandler.predictionInfo("Chicken Pox",
                "chickenPoximg.jpg",
                "More information about Chickenpox:\n\n" +
                        "• Chickenpox is a highly contagious infection caused by varicella-zoster virus (VZV). Symptoms are itchy" +
                        "rash with red spots and fluid-filled blisters that crust over; fever, fatigue, loss of appetite, and headache.\n"+
                        "• Antihistamines or Calamine can be used to help reduce itching\n"+
                        "• Isolate and prevent contact with the affected individual to prevent transmission of virus\n"+
                        "• If the infected individual is considered high-risk (pregnant, infant, weakened immune system), consider doctor consultation"));

        conditionText.put("CDermatitis", new predictionsHandler.predictionInfo("Contact Dermatitis",
                "cdimg.jpg",
                "More information about Contact Dermatitis:\n\n" +
                        "• Contact dermatitis is an inflammatory skin condition caused by direct contact with an irritant or" +
                        "allergen. Symptoms are red, itchy, inflamed skin, dryness, burning or stinging, blisters, or cracking of" +
                        "skin in severe cases.\n"+
                        "• Identify what may have caused the irritation, and avoid or minimize exposure to it\n"+
                        "• Antihistamines can be used to help reduce itching, and other ointments or creams can be used for" +
                        "other symptoms.\n"+
                        "• In severe cases where the affected individual has trouble breathing, consider going to the ER for doctor consultation."));

        conditionText.put("TPedis", new predictionsHandler.predictionInfo("Athlete's Foot",
                "tinepedisImg.jpg",
                "More information about Athlete's Foot:\n\n" +
                        "• Athlete's foot is a common fungal infection that affects the skin on the feet, particularly between" +
                        "the toes. Symptoms are itching, burning or stinging, red, scaly, or peeling skin, cracking, blisters in some cases," +
                        "and can somtimes cause an unpleasant odor.\n"+
                        "• Topical treatment with antifungal properties can be used to treat the condition\n"+
                        "• Oral antifungal medication can also be used\n"+
                        "• For prevention, feet should be washed at least twice a day and use footwear with proper ventilation\n"+
                        "• For oral medication, refer to a doctor for the necessary prescription"));

        conditionText.put("Vitiligo", new predictionsHandler.predictionInfo("Vitiligo",
                "vitiligoimg.jpg",
                "More information about Vitiligo:\n\n" +
                        "• Vitiligo is a chronic condition that causes loss of pigment (melanin) leading to white patches on the skin." +
                        "Symptoms are patches of depigmented (white) skin; commonly on the face, hands, arms, feet, and around body" +
                        "openings (mouth, eyes). Hair in affected areas may turn white.\n"+
                        "• There is no actual treatment for vitiligo, but methods to reduce unevenness of skin color is possible\n"+
                        "• Consider consulting with a dermatologist for professional treatments."));

        conditionText.put("Miliaria", new predictionsHandler.predictionInfo("Miliaria",
                "miliariaimg.jpg",
                "More information about Miliaria:\n\n" +
                        "• Miliaria, commonly known as heat rash or prickly heat, is a skin condition caused by blocked sweat ducts," +
                        "leading to trapped sweat under the skin. Symptoms are small red bumps, itching, prickling, or a stinging" +
                        "sensation. In severe cases, it may cause inflamed or pus-filled lesions.\n"+
                        "• Calamine lotion or topical steroids can be applied to reduce the itching of affected areas\n"+
                        "• Various methods that can reduce or prevent excessive sweating can be done for prevention\n"+
                        "• Wear breathable clothes and avoid wearing excessive and tight clothing\n" +
                        "• Consider consulting a dermatologist for more severe cases."));

        conditionText.put("Scabies", new predictionsHandler.predictionInfo("Scabies",
                "scabiesimg.jpg",
                "More information about Scabies:\n\n" +
                        "• Scabies is a highly contagious skin infestation caused by the Sarcoptes scabiei mite, which burrows" +
                        "into the skin. Symptoms are intense itching (especially at night), red bumps, rashes, and thin, irregular" +
                        "burrow tracks (lines) on the skin. Common areas are the wrists, fingers, elbows, armpits, waist, and genital area.\n"+
                        "• Antihistamines and corticosteroid creams can be used to relief symptoms\n"+
                        "• Affected individuals must avoid skin-to-skin contact to prevent transmission\n"+
                        "• Sanitize the clothes and items used by the infested person to remove possible mites\n"+
                        "• Consider consulting with a dermatologist for professional treatment."));


        predictionsHandler.predictionInfo info= conditionText.getOrDefault(OutputPredict,
                new predictionsHandler.predictionInfo("Healthy Skin",
                        "healthyimg.jpg",
                        "• You have healthy skin. It appears smooth, hydrated, and undamaged. The skin is the outer protective" +
                                "layer of the body, and it is good to keep healthy.\n\n" +
                                "To keep your skin healthy, you can do the following:\n" +
                                "• Apply moisturizer at least once a day\n" +
                                "• Avoid damage from UV, apply sunscreen if necessary and wear protective clothing\n" +
                                "• Avoid taking hot showers to prevent skin from drying\n" +
                                "• Stay hydrated to maintain moisture in the skin"));

        PredictClass.setMovementMethod(new ScrollingMovementMethod());
        PredictClass.setText(info.getDisplayText());
        PredictClassInfo.setText(info.getExtraInfo());
        String imageName = info.getImageName().replace(".jpg", ""); // Remove the extension if necessary
        int resId = getResources().getIdentifier(imageName.toLowerCase(), "drawable", getPackageName());
        OutputPredProb.setText(new StringBuilder(OutputPredProb.getText()).append(OutputProb));

        // Set the image resource
        if (resId != 0) { // Check if the resource ID is valid
            PredictClassImg.setImageResource(resId);
        } else {
            // Handle the case where the resource is not found
            PredictClassImg.setImageResource(R.drawable.healthyimg); // Optional: set a default image
        }
    }

}
