package com.example.personalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScanImageActivity extends AppCompatActivity {
    static final String TAG = "ScanImageActivity";
    Bitmap imageMap;
    List<ImageLabel> labelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_image);

        ImageView imageView = findViewById(R.id.scanImageView);

        Intent receiveIntent = getIntent();
        if(receiveIntent != null){
            //String imageURI = receiveIntent.getStringExtra("imageURI");
            String imageURI = receiveIntent.getStringExtra("imageURI");
            Log.d(TAG, "onCreate: " + imageURI);
            Uri image = Uri.parse(imageURI);
            try{
                imageMap = BitmapFactory.decodeStream(getContentResolver().openInputStream(image));
                imageView.setImageBitmap(imageMap);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }

            analyzeImage(imageMap);

        }
    }

    protected void analyzeImage(Bitmap imageMap){
        labelList = new ArrayList<>();
        FirebaseVisionImage firebaseImage = FirebaseVisionImage.fromBitmap(imageMap);
        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();

        labeler.processImage(firebaseImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
            @Override
            public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                for(FirebaseVisionImageLabel label : labels){
                    String text = label.getText();
                    String entityId = label.getEntityId();
                    float confidence = label.getConfidence();
                    ImageLabel newLabel = new ImageLabel(confidence, entityId, text);
                    labelList.add(newLabel);
                }

                System.out.println(labelList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScanImageActivity.this, "Whoops. Failed.", Toast.LENGTH_SHORT).show();
            }
        });





    }//end analyze
}//end class
