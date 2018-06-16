package com.example.altice.alticedoapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //PERMISSIONS FIELD
    private int GPS_PERMISSION_CODE = 1;

    //DECLARE THE FIELDS
    private TextView noNewstv;
    private RecyclerView rv;
    private myRecyclerViewAdapter adapter;
    private FloatingActionButton add;
    private FloatingActionButton user;

    //ARTICLES FIELDS

    ArrayList<Article> articles = new ArrayList<>();

    //FIREBASE DATABSE FIELDS
    private FirebaseDatabase mFirebaseDatabase
            = FirebaseDatabase.getInstance();

    private DatabaseReference myRef
            = mFirebaseDatabase.getReference("news");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //PERMISSIONS FOR GPS
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION_CODE);

        //ASSIGN ID'S
        noNewstv = (TextView) findViewById(R.id.no_news);

        add = (FloatingActionButton) findViewById(R.id.addBtton);
        add.setOnClickListener(this);

        user = (FloatingActionButton) findViewById(R.id.userButton);
        user.setOnClickListener(this);

        rv = (RecyclerView) findViewById(R.id.my_Recycler_View);
        rv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        //DATABASE READ DATA
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //CLEARS ARTICLES ARRAY LIST
                articles.clear();
                if (dataSnapshot.hasChildren()) {
                    noNewstv.setVisibility(View.GONE);
                } else {
                    noNewstv.setVisibility(View.VISIBLE);
                }
                for (DataSnapshot articleSnapshot : dataSnapshot.getChildren()) {
                    //SAVES EACH ARTICLE TO THE ARRAY
                    Article article = articleSnapshot.getValue(Article.class);
                    Log.wtf("Article", article.getTitle());
                    articles.add(0,article);
                }
                //NOTIFIES THE RECYCLER VIEW ADAPTER THAT THE ARTICLES ARRAY WAS CHANGED
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "An Error happened loading the database", Toast.LENGTH_SHORT).show();
            }
        });

        //SETS RECYCLER VIEW ADAPTER TO MAIN ACTIVITY
        adapter = new myRecyclerViewAdapter(MainActivity.this, articles);
        rv.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBtton:
                //IF NO USER IS LOGGED IN
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    startActivity(new Intent(MainActivity.this, newsUpload.class));
                    break;
                } else {
                    //USER IS LOGGED IN
                    Intent intent = new Intent(MainActivity.this, Log_inActivity.class);
                    intent.putExtra("MainAxtivity->AddNews", 101);
                    startActivity(intent);
                    break;
                }

            case R.id.userButton:
                //IF NO USER IS LOGGED IN
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    Intent intentUser = new Intent(MainActivity.this, signOut.class);
                    startActivity(intentUser);
                    break;
                } else {
                    //USER IS LOGGED IN
                    Intent intentUser = new Intent(MainActivity.this, Log_inActivity.class);
                    startActivity(intentUser);
                    break;
                }

        }
    }


    //WHEN BACK BUTTON IS PRESSED CREATES A DIALOG TO CONFIRM EXIT OF APP
    @Override
    public void onBackPressed() {
        backButtonHandler();
    }

    public void backButtonHandler() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.LightDialogTheme);

        //SETTING DIALOG TITLE
        alertDialog.setTitle("Leave application?");

        //SETTING DIALOG MESSAGE
        alertDialog.setMessage("Are you sure you want to leave the application?");

        // SETTING ICON TO DIALOG
        alertDialog.setIcon(R.mipmap.ic_launcher);

        // SETTING POSITIVE "YES" BUTTON
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                android.os.Process.killProcess(android.os.Process.myPid());
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