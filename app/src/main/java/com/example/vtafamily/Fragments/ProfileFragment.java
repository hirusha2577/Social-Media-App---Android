package com.example.vtafamily.Fragments;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vtafamily.Adapter.PostAdapter;
import com.example.vtafamily.AddPostActivity;
import com.example.vtafamily.MainActivity;
import com.example.vtafamily.Model.Post;
import com.example.vtafamily.Model.User;
import com.example.vtafamily.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    TextView username;
    CircleImageView profile_image;
    ImageView cover_image;
    RecyclerView recyclerview_posts;


    FirebaseUser fuser;
    FirebaseAuth firebaseAuth;
    StorageReference storageReference, storageReference2;

    private static final int P_IMAGE_REQUEST = 1;
    private static final int C_IMAGE_REQUEST = 2;
    private Uri uri;
    private StorageTask uploadTask;

    DatabaseReference databaseReference;

    List<Post> postList;
    PostAdapter postAdapter;
    String uid;




    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

            username = view.findViewById(R.id.username);
            profile_image= view.findViewById(R.id.profile_image);
            cover_image = view.findViewById(R.id.cover_image);
            recyclerview_posts = view.findViewById(R.id.recyclerview_posts);


        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        storageReference2 = FirebaseStorage.getInstance().getReference("C_uploads");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());

                if (user.getImageURL().equals("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }else {
                    //Glide.with(getContext()).load(user.getImageURL()).into(profile_image);
                    Picasso.get().load(user.getImageURL()).into(profile_image);
                }

                if(user.getC_imageURL().equals("default")){
                    cover_image.setColorFilter(R.color.design_default_color_primary);
                }else {
                    Picasso.get().load(user.getC_imageURL()).into(cover_image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
 //profile image on click eka
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_P_image();
            }
        });
 //cover image on click eka
        cover_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_C_image();
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerview_posts.setLayoutManager(layoutManager);

        postList = new ArrayList<>();

        checkUserStatus();
        loadMyposts();

        return view;
    }



    private void loadMyposts() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = reference.orderByChild("user_id").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post mypost = snapshot.getValue(Post.class);

                    //add to list
                    postList.add(mypost);

                    //adapter
                    postAdapter = new PostAdapter(getActivity(), postList);
                    //set this adapter torecycleview
                    recyclerview_posts.setAdapter(postAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void open_P_image() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, P_IMAGE_REQUEST);
    }
    private void open_C_image() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, C_IMAGE_REQUEST );
    }

    private String getFilExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private  void uploard_P_Image(){
        final ProgressDialog pd =new ProgressDialog(getContext());
        pd.setMessage("uploading");
        pd.show();

        if (uri != null){
            final StorageReference fileReference= storageReference.child(System.currentTimeMillis()
                +"."+getFilExtension(uri));

            uploadTask=fileReference.putFile(uri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                   if (!task.isSuccessful()){
                       throw task.getException();
                   }
                   return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downlodUri = task.getResult();
                        String mUri=downlodUri.toString();

                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        databaseReference.updateChildren(map);

                        pd.dismiss();
                    }else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }

    }

    private  void uploard_C_Image(){
        final ProgressDialog pd =new ProgressDialog(getContext());
        pd.setMessage("uploading");
        pd.show();

        if (uri != null){
            final StorageReference fileReference= storageReference2.child(System.currentTimeMillis()
                    +"."+getFilExtension(uri));

            uploadTask=fileReference.putFile(uri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downlodUri = task.getResult();
                        String mUri=downlodUri.toString();

                        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("C_imageURL", mUri);
                        databaseReference.updateChildren(map);

                        pd.dismiss();
                    }else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == P_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            uri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload is preogress", Toast.LENGTH_SHORT).show();
            }else {
                uploard_P_Image();

            }
        }
        if (requestCode == C_IMAGE_REQUEST && resultCode == RESULT_OK
              &&data !=null && data.getData() != null){
            uri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload is preogress", Toast.LENGTH_SHORT).show();
            }else {
                uploard_C_Image();
            }
        }
    }




    private  void checkUserStatus(){

            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                uid = user.getUid();
            } else {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }

    }

}
