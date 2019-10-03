package com.vampyr.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private MaterialEditText phoneNumberText, verifyText;
    private Button btn_submit, btn_login;
    private ImageView btn_backToLogin;
    private TextView logintocontinue,resendCode;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    FirebaseAuth mAuth;
    ProgressDialog loadingBar;

    com.rilixtech.widget.countrycodepicker.CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        verifyText = findViewById(R.id.editVerifyCode);
        phoneNumberText = findViewById(R.id.editPhoneNumber);
        btn_submit = findViewById(R.id.submitPhoneNumber);
        btn_backToLogin = findViewById(R.id.btn_phoneback);
        btn_login = findViewById(R.id.loginPhone);
        logintocontinue = findViewById(R.id.logintocontinue);
        resendCode = findViewById(R.id.resendCode);

        ccp = (com.rilixtech.widget.countrycodepicker.CountryCodePicker) findViewById(R.id.CCP);
        ccp.registerPhoneNumberTextView(phoneNumberText);
        ccp.enableHint(false);

        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String phoneNumber = phoneNumberText.getText().toString();
                String phoneNumber = ccp.getFullNumberWithPlus();

                if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 10) {
                    phoneNumberText.setError("Enter valid Phone number");
                    phoneNumberText.requestFocus();
                    return;
                } else {

                    loadingBar.setTitle("Phone verification");
                    loadingBar.setMessage("Sending verification code...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks
                }

            }
        });

        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = ccp.getFullNumberWithPlus();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        PhoneLoginActivity.this,               // Activity (for callback binding)
                        mCallbacks);        // OnVerificationStateChangedCallbacks
            }

        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String verificationCode = verifyText.getText().toString();

                if (TextUtils.isEmpty(verificationCode)) {
                    verifyText.setError("Please the phone number");
                    verifyText.requestFocus();
                    return;
                } else {

                    loadingBar.setTitle("Login in");
                    loadingBar.setMessage("Please wait...");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                loadingBar.dismiss();
                phoneNumberText.setError("Invalid phone number");
                phoneNumberText.requestFocus();

                phoneNumberText.setVisibility(View.VISIBLE);
                btn_submit.setVisibility(View.VISIBLE);
                logintocontinue.setVisibility(View.VISIBLE);
                verifyText.setVisibility(View.INVISIBLE);
                btn_login.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                phoneNumberText.setVisibility(View.INVISIBLE);
                btn_submit.setVisibility(View.INVISIBLE);
                logintocontinue.setVisibility(View.INVISIBLE);
                verifyText.setVisibility(View.VISIBLE);
                btn_login.setVisibility(View.VISIBLE);
                ccp.setVisibility(View.INVISIBLE);
                resendCode.setVisibility(View.VISIBLE);


                loadingBar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Verification code sent", Toast.LENGTH_SHORT).show();
            }
        };

        btn_backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PhoneLoginActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            loadingBar.dismiss();
                            startActivity(new Intent(PhoneLoginActivity.this,PhoneProfileActivity.class));

                        } else {

                            String msg = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error: "+msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
