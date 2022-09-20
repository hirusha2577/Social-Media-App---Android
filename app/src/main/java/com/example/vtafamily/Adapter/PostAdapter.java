package com.example.vtafamily.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vtafamily.Model.Post;
import com.example.vtafamily.R;
import com.example.vtafamily.ThereProfileActivity;
import com.squareup.picasso.Picasso;


import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyHolder> {

    Context context;
    List<Post> postList;

    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent,false);
        return new PostAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        final String user_id = postList.get(position).getUser_id();
        String email = postList.get(position).getEmail();
        String uname = postList.get(position).getUname();
        String udp = postList.get(position).getUdp();
        String pId = postList.get(position).getpId();
        String pDescr = postList.get(position).getpDescr();
        String pImage = postList.get(position).getpImage();
        String pTimestamp = postList.get(position).getpTime();
        
        //time convert
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimestamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        holder.username.setText(uname);
        holder.posttime.setText(pTime);
        holder.postDis.setText(pDescr);

        //if post image eka nathnam eka hangana
        if (pImage.equals("noImage")){
            //hide image
            holder.postImage.setVisibility(View.GONE);

        }else {
            try{
                Picasso.get().load(pImage).into(holder.postImage);
            }catch (Exception e){

            }
        }

        try{
            Picasso.get().load(udp).placeholder(R.mipmap.ic_launcher).into(holder.profile_image);
        }catch (Exception e){

        }




        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "More", Toast.LENGTH_SHORT).show();
            }
        });
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
            }
        });
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "comments", Toast.LENGTH_SHORT).show();
            }
        });
        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "share", Toast.LENGTH_SHORT).show();
            }
        });

       // post ekak linear layot eka click karahama
        holder.postLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ThereProfileActivity.class);
                intent.putExtra("user_id", user_id);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

      CircleImageView profile_image;
      TextView username,posttime,postDis,postLikes;
      ImageButton moreBtn;
      ImageView postImage;
      Button likeBtn,commentBtn,shareBtn;
      LinearLayout postLinearLayout;

      public MyHolder(@NonNull View itemView) {
          super(itemView);

          profile_image = itemView.findViewById(R.id.profile_image);
          username = itemView.findViewById(R.id.username);
          posttime = itemView.findViewById(R.id.posttime);
          postDis = itemView.findViewById(R.id.postDis);
          postLikes = itemView.findViewById(R.id.postLikes);
          moreBtn = itemView.findViewById(R.id.moreBtn);
          postImage = itemView.findViewById(R.id.postImage);
          likeBtn = itemView.findViewById(R.id.likeBtn);
          commentBtn = itemView.findViewById(R.id.commentBtn);
          shareBtn = itemView.findViewById(R.id.shareBtn);
          postLinearLayout = itemView.findViewById(R.id.postLinearLayout);
      }
  }
}
