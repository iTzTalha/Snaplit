package com.vampyr.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StartActivity extends AppCompatActivity{

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference rootreference;
    private Button loginButton,signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootreference = FirebaseDatabase.getInstance().getReference();

        loginButton = (Button) findViewById(R.id.loginButton2);
        signupButton = (Button) findViewById(R.id.registerButton2);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToLoginActivity();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null){
            SendUserToMainActivity();
        }
    }


    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToRegisterActivity() {
        Intent registerActivity = new Intent(StartActivity.this, RegistrationActivity.class);
        startActivity(registerActivity);
    }

    private void SendUserToMainActivity() {
        Intent mainActivity = new Intent(StartActivity.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }
}
