package com.vampyr.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity implements View.OnKeyListener {

    Button signUpButton;
    EditText username, password, email;
    TextView alreadyhaveAccount;

    FirebaseAuth mAuth;
    ProgressDialog loadingBar;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();


        initializeFields();

        alreadyhaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToLoginActivity();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingBar.setTitle("Creating new account");
                loadingBar.setMessage("Please wait, While we are creating new account for you...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                String UserName = username.getText().toString();
                String userPassword = password.getText().toString();
                String E_mail = email.getText().toString();


                if (TextUtils.isEmpty(userPassword) || TextUtils.isEmpty(E_mail) || TextUtils.isEmpty(UserName)) {
                    loadingBar.dismiss();
                    Toast.makeText(RegistrationActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else if (userPassword.length() < 6) {
                    loadingBar.dismiss();
                    Toast.makeText(RegistrationActivity.this, "Password must be atleast 6 character", Toast.LENGTH_SHORT).show();
                } else {
                    createNewAccount(UserName, E_mail, userPassword);
                }
            }
        });
    }

    private void createNewAccount(final String username, String Email, String UserPassword) {

        mAuth.createUserWithEmailAndPassword(Email, UserPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String currentUserId = mAuth.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", currentUserId);
                            hashMap.put("username", username.toLowerCase());
                            hashMap.put("bio", "");
                            hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/fir-5efa8.appspot.com/o/profileicon.png?alt=media&token=e32c352b-05e1-4ff8-b56e-74a1e01270f4");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        loadingBar.dismiss();
                                        SendUserToMainActivity();
                                    }
                                }
                            });
                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(RegistrationActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }

    private void initializeFields() {
        signUpButton = (Button) findViewById(R.id.registerButton);
        password = (EditText) findViewById(R.id.registerPasswordText);
        email = (EditText) findViewById(R.id.registerEmailText);
        username = (EditText) findViewById(R.id.usernameRegText);
        alreadyhaveAccount = (TextView) findViewById(R.id.loginText);
        loadingBar = new ProgressDialog(this);
        password.setOnKeyListener(this);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToMainActivity() {
        Intent mainActivity = new Intent(RegistrationActivity.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {

        }

        return false;
    }
}
