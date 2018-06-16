package com.example.altice.alticedoapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class Sign_upActivity extends AppCompatActivity implements View.OnClickListener {

    //DECLARE THE FIELDS
    ProgressBar progressBar;
    EditText editTextEmail, editTextPassword, editTextConfirmPassword;

    //FIREBASE FIELDS
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        //ASSIGN ID'S
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirnPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        mAuth = FirebaseAuth.getInstance();

        //ASSIGN ON CLICK LISTENER TO TEXT AND SIGNUP BUTTON
        findViewById(R.id.buttonSignUp).setOnClickListener(this);
        findViewById(R.id.textViewLogin).setOnClickListener(this);
    }

    //REGISTER A USER USING EMAIL AND PASSWORD
    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String passwordConfirm = editTextConfirmPassword.getText().toString().trim();

        //CHECKS IF EMAIL IS EMPTY
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

        //CHECKS IF CONFIRM IS THE SAME AS PASSWORD
        if (!(password.matches(passwordConfirm))){
            editTextConfirmPassword.setError("Passwords must match");
            editTextConfirmPassword.requestFocus();
            return;
        }


        progressBar.setVisibility(View.VISIBLE);

        //CREATES USER USING EMAIL AND ACCOUNT
        mAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    //CREATING AN ACCOUNT WAS SUCCESSFUL, SEND EMAIL TO USER
                    sendVerificationEmail();
                } else {

                    //CHECKS IF EMAIL IS ALREADY REGISTERED
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(Sign_upActivity.this, "You are already registered",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(Sign_upActivity.this, task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


    }

    //VERIFY USER THROUGH AN EMAIL SENT TO USER'S EMAIL
    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //EMAIL SENT
                            Toast.makeText(Sign_upActivity.this, "An email was sent to your account", Toast.LENGTH_SHORT).show();
                            // AFTER EMAIL IS SENT LOGOUT
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(Sign_upActivity.this, Log_inActivity.class));
                            finish();
                        }
                        else
                        {
                            // VERIFICATION EMAIL WAS NOT SEND BY ANY ERROR
                            Toast.makeText(Sign_upActivity.this, "There was an error sending the email, try verifying if email exists", Toast.LENGTH_SHORT).show();
                            //RESTART THE ACTIVITY
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(getIntent());

                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonSignUp:
                registerUser();
                break;

            case R.id.textViewLogin:
                finish();
                //START LOGIN ACTIVITY
                Intent intent = new Intent(this, Log_inActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }
}
