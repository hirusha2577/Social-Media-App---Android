package com.example.vtafamily;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference userDbRef;

    private  static  final  int CAMERA_REQUEST_CODE = 100;
    private  static  final  int STORAGE_REQUEST_CODE = 200;
    private  static  final  int IMAGE_PICK_CAMERA_CODE = 300;
    private  static  final  int IMAGE_PICK_GALLERY_CODE = 400;
    String [] cameraPermissions;
    String [] storagePermissions;

    ProgressDialog pd;

    ImageView postImage;
    EditText postDis;
    Button postuploadBtn;

    String uname, email, uid, udp;

    Uri image_rui = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);



        //init permissions arrays
        cameraPermissions = new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();
        checkUserStatus();

        userDbRef = FirebaseDatabase.getInstance().getReference("Users");
        Query query = userDbRef.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    uname = "" + snapshot.child("username").getValue();
                    udp   = "" + snapshot.child("imageURL").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postImage = findViewById(R.id.postImage);
        postDis = findViewById(R.id.postDis);
        postuploadBtn = findViewById(R.id.postuploadBtn);

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicDilog();
            }
        });

        postuploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String description = postDis.getText().toString().trim();

                if (TextUtils.isEmpty(description)){
                    Toast.makeText(AddPostActivity.this, "Enter description...", Toast.LENGTH_SHORT).show();
                }
                if(image_rui == null){
                    uploadData(description, "noImage");
                }else {
                    uploadData(description, String.valueOf(image_rui));
                }
            }
        });

    }

    private void uploadData(final String description, final String uri) {
        pd.setMessage("Publishing post....");
        pd.show();

        final String timestamp = String.valueOf(System.currentTimeMillis());
        String filPathAndName = "Posts/" + "post_" + timestamp;

        if (!uri.equals("noImage")){
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filPathAndName);
            ref.putFile(Uri.parse(uri))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());

                            String downloadUri = uriTask.getResult().toString();

                            if (uriTask.isSuccessful()){

                                HashMap<Object,String> hashMap = new HashMap<>();

                                hashMap.put("user_id", uid);//ok
                                hashMap.put("uname", uname );//ok
                                hashMap.put("email", email);//ok
                                hashMap.put("udp", udp);//ok
                                hashMap.put("pId", timestamp);//ok
                                hashMap.put("pDescr", description);//ok
                                hashMap.put("pImage", downloadUri);//ok
                                hashMap.put("pTime", timestamp);//ok

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                ref.child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(AddPostActivity.this, "Post published", Toast.LENGTH_SHORT).show();

                                                postDis.setText("");
                                                postImage.setImageURI(null);
                                                image_rui = null;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            //image ekak nathuwa post ekak dana eka
            HashMap<Object,String> hashMap = new HashMap<>();

            hashMap.put("user_id", uid);
            hashMap.put("uname", uname );
            hashMap.put("email", email);
            hashMap.put("udp", udp);
            hashMap.put("pId", timestamp);
            hashMap.put("pDescr", description);
            hashMap.put("pImage", "noImage");
            hashMap.put("pTime", timestamp);

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
            ref.child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this, "Post published", Toast.LENGTH_SHORT).show();

                            postDis.setText("");
                            postImage.setImageURI(null);
                            image_rui = null;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(AddPostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }

    private void checkUserStatus() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null){
            email = user.getEmail();
            uid = user.getUid();
        }else {
            startActivity(new Intent(this,StartActivity.class));
            finish();
        }
    }



    private void showImagePicDilog() {
        String [] options = {"Camera", "Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image from");
        //set option to dialog
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //item click handle
                if(which==0){
                    //camera
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }else {
                        pickFromCamera();
                    }
                }
                if (which==1){
                    //gallery
                    if (!checkStoragePermission()){
                        requeststoragePermission();
                    }else {
                        pickFromGallery();
                    }
                }

            }
        });
        //create and show dialog
        builder.create().show();

    }




    private void pickFromGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);

    }

    private void pickFromCamera() {

        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Temp Discr");
        image_rui = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);



        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_rui);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);

    }



    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return  result;
    }

    private void requeststoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return  result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions, CAMERA_REQUEST_CODE);
    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Camera & Storage both permission are necessary.....", Toast.LENGTH_SHORT).show();
                    }
                } else {

                }
            }
            break;
            case  STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if ( storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Storage permission necessary.....", Toast.LENGTH_SHORT).show();
                    }
                }else {

                }

            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                image_rui = data.getData();
                postImage.setImageURI(image_rui);
            }else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                postImage.setImageURI(image_rui);
            }
        }

    }
}
