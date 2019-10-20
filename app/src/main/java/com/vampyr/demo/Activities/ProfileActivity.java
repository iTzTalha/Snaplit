package com.vampyr.demo.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hbb20.CountryCodePicker;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vampyr.demo.Fragments.ProfileFragment;
import com.vampyr.demo.Model.Users;
import com.vampyr.demo.R;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    public static int PreqCode = 1;

    TextView tv_change,editBio,editUsrname;
    ImageView close;

    CircleImageView imageView;

    FirebaseUser firebaseUser;

    private Uri mImageUri;
    private StorageTask uploadTask;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tv_change = findViewById(R.id.tv_change);
        close = findViewById(R.id.close);
        imageView = findViewById(R.id.image_profile);
        editBio = findViewById(R.id.editBio);
        editUsrname = findViewById(R.id.editUsername);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users users = dataSnapshot.getValue(Users.class);
                editUsrname.setText(users.getUsername());
                editBio.setText(users.getBio());
                Glide.with(getApplicationContext()).load(users.getImageurl()).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        editUsrname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,EditUsernameActivity.class));
            }
        });

        editBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,BioActivity.class));
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  if (Build.VERSION.SDK_INT >= 22){

                    if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                                ActivityCompat.requestPermissions(ProfileActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PreqCode);
                            }
                        }
                    }else {
                        CropImage.activity()
                                .setAspectRatio(1, 1)
                                .setCropShape(CropImageView.CropShape.OVAL)
                                .start(ProfileActivity.this);
                    }
                }else {

                    CropImage.activity()
                            .setAspectRatio(1, 1)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .start(ProfileActivity.this);
                }*/
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(ProfileActivity.this);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* if (Build.VERSION.SDK_INT >= 22){

                    if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                                ActivityCompat.requestPermissions(ProfileActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PreqCode);
                            }
                        }
                    }else {
                        CropImage.activity()
                                .setAspectRatio(1, 1)
                                .setCropShape(CropImageView.CropShape.OVAL)
                                .start(ProfileActivity.this);
                    }
                }else {

                    CropImage.activity()
                            .setAspectRatio(1, 1)
                            .setCropShape(CropImageView.CropShape.OVAL)
                            .start(ProfileActivity.this);
                }*/
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(ProfileActivity.this);
            }
        });


    }

    private String getFileExtention(Uri uri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading");
        progressDialog.show();

        if (mImageUri != null){

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() +"."+ getFileExtention(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){

                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageurl", ""+myUrl);

                        reference.updateChildren(hashMap);
                        progressDialog.dismiss();
                    }else {

                        Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {

            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            uploadImage();
        }else {

          //  Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}
