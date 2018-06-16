package com.example.altice.alticedoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class signOut extends AppCompatActivity implements View.OnClickListener{

    //DECLARE THE FIELDS
    private Button signOutButton;
    private TextView tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_out);


        //ASSIGN ID'S
        signOutButton = (Button) findViewById(R.id.buttonSignOut);
        signOutButton.setOnClickListener(this);

        tvEmail = (TextView) findViewById(R.id.textViewUser);
        tvEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonSignOut:
                //SIGN OUT CURRENT ACCOUNT
                finish();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(signOut.this, Log_inActivity.class);
                //CLOSE ALL TABS OPENED
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //START LOGIN ACTIVITY
                startActivity(intent);
                break;
        }
    }
}
