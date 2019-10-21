package com.example.personalproject;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    TextView textTargetUri;
    ImageView targetImage;
    Button scanImage;
    protected boolean sucessfulUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button loadImageButton = (Button) findViewById(R.id.loadImage);
        textTargetUri = (TextView) findViewById(R.id.targetURI);
        targetImage = (ImageView) findViewById(R.id.imageView);

        loadImageButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View arg0){
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }});
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        scanImage = (Button) findViewById(R.id.scanImage);

        if(resultCode == RESULT_OK){
            sucessfulUpload = true;
            Uri targetUri = data.getData();
            textTargetUri.setText(targetUri.toString());
            Bitmap bitmap;
            scanImage.setVisibility(View.VISIBLE);
            try{
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                targetImage.setImageBitmap(bitmap);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }
}
