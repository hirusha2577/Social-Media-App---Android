package com.example.vtafamily;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.vtafamily.Adapter.PostAdapter;
import com.example.vtafamily.Model.Post;
import com.example.vtafamily.Model.User;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ThereProfileActivity extends AppCompatActivity {

    RecyclerView recyclerview_posts;
    TextView username;
    CircleImageView profile_image;

    FirebaseAuth firebaseAuth;
    FirebaseUser fuser;
    DatabaseReference databaseReference;


    List<Post> postList;
    PostAdapter postAdapter;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);

        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Profile..");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);*/

        recyclerview_posts = findViewById(R.id.recyclerview_posts);
        username = findViewById(R.id.username);
        profile_image= findViewById(R.id.profile_image);

        firebaseAuth = FirebaseAuth.getInstance();

        //click kara kenage uid eke ganno
        Intent intent = getIntent();
        uid = intent.getStringExtra("user_id");

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String name = "" + snapshot.child("username").getValue();
                    String pimage = "" + snapshot.child("imageURL").getValue();

                    username.setText(name);  // adala kenage name eka hriyata eno
                    try{
                        Picasso.get().load(pimage).into(profile_image); // profile image ekath hariyata enoo
                    }catch (Exception e){
                        Picasso.get().load(user.getImageURL()).into(profile_image);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        firebaseAuth = FirebaseAuth.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerview_posts.setLayoutManager(layoutManager);

        postList = new ArrayList<>();

        checkUserStatus();
        loadHisPosts();

    }

    private void loadHisPosts() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        Query query = reference.orderByChild("user_id").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post mypost = snapshot.getValue(Post.class);

                    //list ekata add karano
                    postList.add(mypost);

                    //adapter eka
                    postAdapter = new PostAdapter(ThereProfileActivity.this, postList);
                    //set this adapter torecycleview
                    recyclerview_posts.setAdapter(postAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ThereProfileActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private  void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null){
            uid = user.getUid();
        }else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }


}
