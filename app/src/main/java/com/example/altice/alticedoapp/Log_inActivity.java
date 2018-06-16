package com.example.altice.alticedoapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Log_inActivity extends AppCompatActivity implements View.OnClickListener{

    //FIREBASE FIELDS
    FirebaseAuth mAuth;

    //DECLARE THE FIELDS
    EditText editTextEmail;
    EditText editTextPassword;
    ProgressBar progressBar;
    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        //ASSIGN ID'S
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        textView = (TextView) findViewById(R.id.textViewSignup);
        button = (Button) findViewById(R.id.buttonLogin);


        textView.setOnClickListener(this);
        button.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textViewSignup:
                finish();
                //START SIGN UP ACTIVITY
                Intent intent = new Intent(Log_inActivity.this, Sign_upActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//Closes other tabs opened.
                startActivity(intent);
                break;

            case R.id.buttonLogin:
                userLogin();
                break;

        }

    }

    private void userLogin() {
        //DECLARE TEXT FIELDS
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //CHECKS IF EMAIL ES EMPTY
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        //CHECKS IF EMAIL IS IN CORRECT STRUCTURE
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        //CHECKS IF PASSWORD IS EMPTY
        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        //CHECKS IF PASSWORD WORD IS LARGER THAN 6 LETTER
        if (password.length() < 6) {
            editTextPassword.setError("Minimum lenght of password should be 6");
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);


        //SIGNIN USING EMAIL AND PASSWORD
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        //CHECKS IF LOGIN WAS SUCCESSFUL
                        if (task.isSuccessful()) {
                            checkIfEmailVerified();

                        } else {
                            //LOGIN NOT SUCCESSFUL
                            Toast.makeText(Log_inActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkIfEmailVerified()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified())
        {
            Toast.makeText(Log_inActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
            finish();
            //CHECKS IF ITENT COMES FROM MAIN ACTIVITY
            if(getIntent().hasExtra("MainAxtivity->AddNews")){
                Intent intent = new Intent(Log_inActivity.this,newsUpload.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(Log_inActivity.this, MainActivity.class);
                //CLOSE ALL OTHER TABS OPENED
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        }
        else
        {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Email is not verified", Toast.LENGTH_SHORT).show();
        }
    }



}
