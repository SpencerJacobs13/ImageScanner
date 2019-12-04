package com.example.personalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.android.gms.vision.face.FaceDetector;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ScanImageActivity extends AppCompatActivity {
    static final String TAG = "ScanImageActivity";
    Bitmap imageMap;
    List<ImageLabel> labelList;
    TextView outputTextView;

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
            analyzeImageForLabels(imageMap);
            analyzeImageForFaces(imageMap);
        }
    }

    protected void analyzeImageForLabels(Bitmap imageMap){
        outputTextView = findViewById(R.id.textOutputView);
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
                String topLabels = "";
                Log.d(TAG, "onSuccess: Number of Labels: " + labelList.size());
                //if there are more than 5 appropriate labels, we only want the top 5
                if(labelList.size() >= 5) {
                    for(int i = 0; i < 5; i++){
                        topLabels += labelList.get(i) + "\n";
                    }
                }else{
                    //if there are fewer than 5, we want to fetch all of them
                    for(int i = 0; i < labelList.size(); i++){
                        topLabels += labelList.get(i) + "\n";
                    }
                }
                Log.d(TAG, "onSuccess: " + topLabels);
                outputTextView.setText(topLabels);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScanImageActivity.this, "Whoops. Failed.", Toast.LENGTH_SHORT).show();
            }
        });
    }//end analyze for labels


    protected void analyzeImageForFaces(Bitmap imageMap){
        FaceDetector faceDetector = new FaceDetector.Builder(this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        Frame frame = new Frame.Builder().setBitmap(imageMap).build();

        SparseArray<Face> faces = faceDetector.detect(frame);

        Log.d(TAG, "analyzeImageForFaces: " + faces.size());

        for(int i = 0; i < faces.size(); ++i){
            Face face = faces.valueAt(i);
        }

        faceDetector.release();
    }

    protected void analyzeImageForInformation(Bitmap imageMap){
        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        Vision vision = visionBuilder.build();

        //converting our bitmap into a byte array to be used with Google Vision
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageMap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArrayOfImage = stream.toByteArray();

        Image inputImage = new Image();
        inputImage.encodeContent(byteArrayOfImage);

        Feature feature = new Feature();
        feature.setType("IMAGE_PROPERTIES");

        AnnotateImageRequest request = new  AnnotateImageRequest();
        //request.setFeatures(feature);


        //unsure of this next part:
//        AnnotateImageRequest request = new AnnotateImageRequest();
//        request.setImage(inputImage);
//        request.setFeatures(Arrays.asList(desiredFeature));
//
//        BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
//        batchRequest.setRequests(Arrays.asList(request));
//
//        List<ImageProperties> imageProperties;
//        try {
//            BatchAnnotateImagesResponse batchResponse = vision.images().annotate(batchRequest).execute();
//            //imageProperties = new ArrayList<>();
//            imageProperties = batchResponse.getResponses().get(0).getImagePropertiesAnnotation();
//        }catch(IOException e){
//            e.printStackTrace();
//        }


    }


}//end class
