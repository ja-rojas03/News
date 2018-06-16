package com.example.altice.alticedoapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Locale;

public class newsUpload extends AppCompatActivity implements View.OnClickListener, LocationListener {

    //IMAGE TAG
    private static final int CHOOSE_IMAGE = 101;

    //DECLARE THE FIELDS
    private Button myImageUploadButton;
    private Button myNewsUploadButton;
    private ImageView cameraIcon;
    private EditText txtTitle;
    private EditText txtDescription;
    private Uri newsImage;


    //PROGRESS BAR
    private ProgressBar progressBar;

    //ARTICLE
    private Article article;

    //IMAGE MANAGEMENT
    private Bitmap bitmap;
    private String imageUrl;


    //FIREBASE AUTH
    private FirebaseAuth mAuth;

    //FIREBASE DATABASE
    private DatabaseReference databaseNews;

    //LOCATION MANAGEMENT
    private float minDistance = 0.f;
    private long minTime = 1000;
    private double longitude;
    private double latitude;

    //LOCATION FIELDS
    private LocationManager locationManager;
    private Location lastLocation;

    //STORAGE PERMISSIONS FIELDS
    private int READ_STORAGE = 1;
    private int WRITE_STORAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_upload);


        //ASSIGN ID'S
        myImageUploadButton = (Button) findViewById(R.id.load_img_button);
        myImageUploadButton.setOnClickListener(this);

        myNewsUploadButton = (Button) findViewById(R.id.add_news_button);
        myNewsUploadButton.setOnClickListener(this);

        cameraIcon = (ImageView) findViewById(R.id.news_Image);

        txtTitle = (EditText) findViewById(R.id.txt_title);

        txtDescription = (EditText) findViewById(R.id.txt_description);

        progressBar = (ProgressBar) findViewById(R.id.progressbarLoadImage);

        mAuth = FirebaseAuth.getInstance();

        databaseNews = FirebaseDatabase.getInstance().getReference("news");


        //LOCATION PERMISSION MANAGEMENT


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //CHECKS IF STORAGE HAS PERMISSIONS
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            //NO PERMISSIONS, LET'S ASK FOR THEM
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE);
        }


        //CKECKS IF LOCATION HAS PERMISSIONS
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }


        lastLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,minTime,minDistance,this);

        //END OF LOCATION MANAGEMENT


    }
//

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.load_img_button:
                    showImageChooser();
                break;

            case R.id.add_news_button:
                if(checkFields()){
                    loadImageOnFirebaseStorage();

                    //START MAIN ACTIVITY
                    Intent intent = new Intent(newsUpload.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
                }


        }
    }

    //CHECKS IF ANY FIELD IS EMPTY
    private boolean checkFields() {
        String title = txtTitle.getText().toString().trim();
        String description = txtDescription.getText().toString().trim();

        //CHECKS IF THE TITLE IS EMPTY
        if (title.isEmpty()) {
            txtTitle.setError("Title is required");
            txtTitle.requestFocus();
            return false;
        }

        //CHECKS IF THE DESCRIPTION IS EMPTY
        if (description.isEmpty()) {
            txtDescription.setError("Description is required");
            txtDescription.requestFocus();
            return false;
        }
        //CHECKS IF THE PICTURE HAS NOT BEEN SELECTED
        if(newsImage== null){
            Toast.makeText(this, "You have to upload a picture.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    //START GALLERY
    private void showImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select News Image."), CHOOSE_IMAGE);
    }

    //LOADS IMAGE TO STORAGE AND CREATES NEWS FOR DATABASE
    private void loadImageOnFirebaseStorage() {
        //FIREBASE STORAGE REFERENCE
        StorageReference newsImageRef = FirebaseStorage.getInstance().
                getReference("NewsPics/" + System.currentTimeMillis() + ".jpg");

        if(newsImage != null){
            progressBar.setVisibility(View.VISIBLE);
            //ADD IMAGE TO STORAGE FIREBASE
            newsImageRef.putFile(newsImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressBar.setVisibility(View.GONE);
                            //GET IMAGE URL TO CREATE ARTICLE
                            taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imageUrl = uri.toString();
                                            Log.d("newsUpload", "onSuccess: Image Url:  " + newsImage.toString());
                                            addNews();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(newsUpload.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

    //CREATES NEWS ARTICLE
    private void addNews() {
        //DECLARE FIELDS
        String title = txtTitle.getText().toString().trim();
        String description = txtDescription.getText().toString().trim();
        String locationCity = hereLocation(latitude,longitude);

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)){

            //AUTO GEGNERATED ID TO SAVE VALUES
            String id = databaseNews.push().getKey();

            //CREATE ARTICLE OBJECT
            article = new Article(imageUrl,title,description,locationCity);

            //ADD VALUE TO DATABASE
            databaseNews.child(id).setValue(article);

            Toast.makeText(this, "News Added!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "All fields should be filled ", Toast.LENGTH_SHORT).show();
        }
    }


    //MAKE CAMERA ICON IMAGE -> SELECTED IMAGE

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK
                && data != null && data.getData() != null){

            //GETS Uri DATA AND SAVES IT ON VAR
            newsImage =  data.getData();
            try {

                //GETS SELECTED IMAGE TO BITMAP
                bitmap = MediaStore.Images.Media.
                        getBitmap(getContentResolver(), newsImage);

                //MAKES IMAGEVIEW SELECTED IMAGE
                cameraIcon.setImageBitmap(bitmap);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //START OF GEOCODE:
    //GET NEAREST CITY
    public String hereLocation(double lat, double lon) {
        String ourCity ="";

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList;
        try{
            addressList = geocoder.getFromLocation(lat, lon , 1);
            if(addressList.size() > 0){
                ourCity = addressList.get(0).getLocality();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return ourCity;
    }

    //END OF GEOCODE

    //LOCATION AUTOGENERATED METHODS

    @Override
    public void onLocationChanged(Location location) {

        //SAVE LONG/LAT TO CLASS VARS
        longitude = location.getLongitude();
        latitude = location.getLatitude();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //WHEN BACK BUTTON IS PRESSED CREATES A DIALOG TO CONFIRM EXIT OF APP
    @Override
    public void onBackPressed() {
        backButtonHandler();
    }

    public void backButtonHandler() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(newsUpload.this, R.style.LightDialogTheme);

        //SETTING DIALOG TITLE
        alertDialog.setTitle("Leave news?");

        //SETTING DIALOG MESSAGE
        alertDialog.setMessage("Do you want to discard the current news?");

        // SETTING ICON TO DIALOG
        alertDialog.setIcon(R.mipmap.ic_launcher);

        // SETTING POSITIVE "YES" BUTTON
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        // SETTING NEGATIVE "NO" BUTTON
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // SHOW ALERT MESSAGE
        alertDialog.show();
    }
}
