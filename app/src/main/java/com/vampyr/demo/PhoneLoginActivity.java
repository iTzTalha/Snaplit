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
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private MaterialEditText phoneNumberText, verifyText;
    private Button btn_submit, btn_login;
    private ImageView btn_backToLogin;
    private TextView logintocontinue, resendCode;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    String phoneNumber;
    String currentUserId;

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
                phoneNumber = ccp.getFullNumberWithPlus();

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
                    verifyText.setError("Invalid code");
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

              //  signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {


                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    loadingBar.dismiss();
                    phoneNumberText.setError("Invalid phone number");
                    phoneNumberText.requestFocus();

                    phoneNumberText.setVisibility(View.VISIBLE);
                    btn_submit.setVisibility(View.VISIBLE);
                    logintocontinue.setVisibility(View.VISIBLE);
                    verifyText.setVisibility(View.INVISIBLE);
                    btn_login.setVisibility(View.INVISIBLE);

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    loadingBar.dismiss();
                    phoneNumberText.setError("You're sending too many verification code, Please try again later");
                    phoneNumberText.requestFocus();
                    return;
                }

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
                            currentUserId = mAuth.getUid();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                            reference.orderByChild("phone").equalTo(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue() != null) {
                                        //it means user already registered
                                        startActivity(new Intent(PhoneLoginActivity.this, MainActivity.class));
                                        finish();

                                    } else {
                                        //It is new users
                                        //write an entry to your user table
                                        //writeUserEntryToDB();

                                        currentUserId = mAuth.getUid();
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("id", currentUserId);
                                        hashMap.put("phone", phoneNumber);
                                        hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/fir-5efa8.appspot.com/o/profile-placeholder.png?alt=media&token=0f72e718-b845-4e7b-865c-76d08340f9a8");

                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    loadingBar.dismiss();
                                                    Intent profileIntent = new Intent(PhoneLoginActivity.this,MainActivity.class);
                                                    startActivity(profileIntent);
                                                    finish();

                                                } else {
                                                    String message = task.getException().toString();
                                                    Toast.makeText(PhoneLoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        } else {

                            String msg = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error: " + msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
