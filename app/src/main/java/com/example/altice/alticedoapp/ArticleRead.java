package com.example.altice.alticedoapp;


import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ArticleRead extends AppCompatActivity {

    //DECLARE THE FIELDS
    private TextView title;
    private TextView description;
    private ImageView image;
    private TextView location;
    private ImageView shareButton;
    private String imageUrl;
    private String articleTitle;
    private String articleDescription;
    private String articleLocation;
    private Uri articleImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_read);

        //ASSIGN ID'S
        title = (TextView) findViewById(R.id.article_news_title);
        description = (TextView) findViewById(R.id.article_news_description);
        image = (ImageView) findViewById(R.id.article_news_image);
        location = (TextView) findViewById(R.id.publish_location);
        shareButton = (ImageView) findViewById(R.id.share_button);

        getIncomingIntent();

        //SHARE BUTTON ON CLICK LISTENER
        shareButton.setOnClickListener(v -> {
            sharePost();

        });
    }

    //GET ARTICLE'S INFORMATION
    private void getIncomingIntent(){


        //ASK IF INTENT COMES FROM RECYCLER VIEW
        if(getIntent().hasExtra("image_url")
                && getIntent().hasExtra("article_title")
                && getIntent().hasExtra("article_description"))
        {
            //FILL VARIABLES WITH ARTICLE'S INFO

            imageUrl = getIntent().getStringExtra("image_url");
            articleTitle = getIntent().getStringExtra("article_title");
            articleDescription = getIntent().getStringExtra("article_description");
            articleLocation = getIntent().getStringExtra("article_location");
            articleImage = Uri.parse(imageUrl);

            createArticle(imageUrl,articleTitle,articleDescription , articleLocation);


        }
    }
    //CREATES THE ARTICLE ON THE ACTIVITY
    private void createArticle(String imageUrl, String articleTitle, String articleDescription , String articleLocation) {
        title.setText(articleTitle);
        description.setText(articleDescription);
        Glide.with(this).load(imageUrl).into(image);
        location.setText("Published From: " + articleLocation);

    }


    //OVERRIDES onBackPressed TO FINISH THIS ACTIVITY WHENEVER BACK BUTTON IS PRESSED
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public void sharePost() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        Intent shareIntent;

        //BITMAP GETS IMAGE STORED ON IMAGEVIEW
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        //CREATES A DIRECTORY FOR IMG
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/Share.jpeg";
        OutputStream out = null;
        //CREATE A PATH TO STORE IMG
        File file=new File(path);
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        path=file.getPath();
        Uri bmpUri = Uri.parse("file://"+path);
        //CREATES A SHARE INTENT TO SHARE INFORMATION
        shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //SENDS IMAGE ON SHARE
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        //SENDS DESCRIBED TEXT ON SHARE
        shareIntent.putExtra(Intent.EXTRA_TEXT,"Title: " + articleTitle + "\n" +
                                        "Description: " +  articleDescription + "\n" +
                                         "Published From: " + articleLocation + "\nCreated with my News! app.");
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent,"Share with: " ));

    }
}

