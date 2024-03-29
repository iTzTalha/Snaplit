package com.vampyr.demo.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vampyr.demo.R;

import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity{

    View view = this.getCurrentFocus();

    Button signUpButton;
    EditText username, password, email;
    TextView alreadyhaveAccount,phoneNumberLoginText;
    ImageView phoneImage;

    FirebaseAuth mAuth;
    ProgressDialog loadingBar;
    DatabaseReference reference;
    String currentUserId;


    ConstraintLayout constraintLayout;

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
            public void onClick(final View view) {

                hideKeyboard();

                loadingBar.setTitle("Creating new account");
                loadingBar.setMessage("Please wait...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                final String UserName = username.getText().toString().trim();
                final String userPassword = password.getText().toString();
                final String E_mail = email.getText().toString().trim();
                Query usernameQuery = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username").equalTo(UserName);
                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0){
                            loadingBar.dismiss();
                            hideKeyboard();
                            username.setError("Username is taken");
                            username.setText("");
                            email.setText("");
                            password.setText("");
                        }else if (TextUtils.isEmpty(UserName)){
                            loadingBar.dismiss();
                            username.setError("Please enter the username");
                            username.requestFocus();
                            return;
                        }else if (username.length() < 3){
                            loadingBar.dismiss();
                            username.setError("Username must be between 3 - 25 characters");
                            username.requestFocus();
                            return;
                        }else if (TextUtils.isEmpty(E_mail) ){
                            loadingBar.dismiss();
                            email.setError("Please enter the email address");
                            email.requestFocus();
                            return;
                        }else if (!Patterns.EMAIL_ADDRESS.matcher(E_mail).matches()) {
                            loadingBar.dismiss();
                            email.setError("Please enter a valid email address");
                            email.requestFocus();
                            return;
                        }else if (TextUtils.isEmpty(userPassword)) {
                            loadingBar.dismiss();
                            password.setError("Please enter the password");
                            password.requestFocus();
                            return;
                        }else if (userPassword.length() < 6) {
                            loadingBar.dismiss();
                            password.setError("Password must be at least 6 character");
                        }else {
                            createNewAccount(UserName, E_mail, userPassword);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        phoneImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this,PhoneLoginActivity.class));
                finish();
            }
        });

        phoneNumberLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this,PhoneLoginActivity.class));
                finish();
            }
        });

    }

    private void createNewAccount(final String username, final String Email, String UserPassword) {

        mAuth.createUserWithEmailAndPassword(Email, UserPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            final FirebaseUser currentUser = mAuth.getCurrentUser();
                            currentUserId = mAuth.getUid();
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("id", currentUserId);
                                    hashMap.put("username", username.toLowerCase().replace(" ","").trim());
                                    hashMap.put("bio", "");
                                    hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/fir-5efa8.appspot.com/o/profile-placeholder.png?alt=media&token=0f72e718-b845-4e7b-865c-76d08340f9a8");
                                    hashMap.put("email",Email);

                                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                loadingBar.dismiss();
                                                Toast.makeText(RegistrationActivity.this, "Verification Email sent to "+ currentUser.getEmail(), Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
                                                finish();
                                            }else {
                                                String message = task.getException().toString();
                                                Toast.makeText(RegistrationActivity.this, "Error: "+message , Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
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
        constraintLayout = (ConstraintLayout) findViewById(R.id.bg_regLayout);
        phoneNumberLoginText = (TextView) findViewById(R.id.phoneNumberLoginText);
        phoneImage = (ImageView) findViewById(R.id.phoneImage);
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    public void hideKeyboard(){
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
