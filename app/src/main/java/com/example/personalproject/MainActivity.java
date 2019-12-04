package com.example.personalproject;

import java.io.FileNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";
    static final int TAKE_PICTURE_REQUEST = 2;
    static final int UPLOAD_PICTURE_REQUEST = 1;
    static final int WRITE_EXTERNAL_REQUEST = 3;
    TextView textTargetUri;
    ImageView targetImage;
    Button scanImage;
    Bitmap imageMap;
    String imageURI;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textTargetUri = findViewById(R.id.targetURI);
        targetImage = findViewById(R.id.imageView);
        scanImage = findViewById(R.id.scanImage);

        scanImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScanImageActivity.class);
                intent.putExtra("imageURI", imageURI);

                startActivity(intent);
            }
        });

    }

    //not used yet, might use later.
    protected boolean sucessfulUpload;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.takePicture:
                //dispatchTakePictureIntent();
                takePicture();
                return true;
            case R.id.uploadPicture:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, UPLOAD_PICTURE_REQUEST);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cam_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        scanImage = findViewById(R.id.scanImage);

        if(requestCode == UPLOAD_PICTURE_REQUEST && resultCode == RESULT_OK){
            scanImage.setClickable(true);
            sucessfulUpload = true;
            Uri targetUri = data.getData();
            textTargetUri.setText(targetUri.toString());
            Log.d(TAG, "onActivityResult: " + targetUri);
            //Bitmap bitmap;
            imageURI = targetUri.toString();
            scanImage.setVisibility(View.VISIBLE);
            try{
                imageMap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                targetImage.setImageBitmap(imageMap);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }else if(requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            targetImage.setImageBitmap(imageBitmap);
        }
    }//end onActivityResult

//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            File photoFile = null;
//            try{
//                photoFile = createImageFile();
//            }catch (IOException e){
//                //there was an error creating the file
//                Toast.makeText(this, "Error processing image.", Toast.LENGTH_SHORT).show();
//            }
//            //continue only if the file upload worked properly
//            if(photoFile != null){
//                Uri photoURI = FileProvider.getUriForFile(this, "com.example.personalproject", photoFile);
//                imageURI = photoURI.toString();
//
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST);
//            }
//        }
//    }

//    private File createImageFile() throws IOException{
//            //create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//
//        File image = File.createTempFile(
//                imageFileName, //prefix
//                ".jpg", //suffix
//                storageDir);  //directory
//
//        currentPhotoPath = image.getAbsolutePath();
//        return image;
//    }

    public void takePicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PICTURE_REQUEST);
    }






}//end MainActivity class
