package com.vampyr.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnKeyListener{

    private Button loginButton;
    private EditText Email, password;
    private TextView forgetPassword,creatNewAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference rootreference;
    private ConstraintLayout constraintLayout;


    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            AllowUserToLogin();
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        InitializeFields();

        creatNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                AllowUserToLogin();
            }
        });

        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        });
    }

    private void AllowUserToLogin() {
        String userPassword = password.getText().toString();
        String E_mail = Email.getText().toString();
        if(TextUtils.isEmpty(userPassword) || TextUtils.isEmpty(E_mail)){
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        } else{
            loadingBar.setTitle("Sign in");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.signInWithEmailAndPassword(E_mail,userPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                if (mAuth.getCurrentUser().isEmailVerified()){
                                    rootreference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
                                    rootreference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            loadingBar.dismiss();
                                            SendUserToMainActivity();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }else {
                                    loadingBar.dismiss();
                                    Toast.makeText(LoginActivity.this, "Please verify your Email address", Toast.LENGTH_SHORT).show();
                                    Email.setText("");
                                    password.setText("");
                                }
                            }else{
                                loadingBar.dismiss();
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void InitializeFields() {
        loginButton = (Button) findViewById(R.id.loginButton);
        Email = (EditText) findViewById(R.id.loginUserName);
        password = (EditText) findViewById(R.id.loginPasswordText);
        forgetPassword = (TextView) findViewById(R.id.forgetPasswordText);
        creatNewAccount = (TextView) findViewById(R.id.signupText);
        loadingBar = new ProgressDialog(this);
        password.setOnKeyListener(this);
        constraintLayout = (ConstraintLayout) findViewById(R.id.bgrelativeLayout);
    }

    private void SendUserToMainActivity() {
        Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent registerActivity = new Intent(LoginActivity.this, RegistrationActivity.class);
        startActivity(registerActivity);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}