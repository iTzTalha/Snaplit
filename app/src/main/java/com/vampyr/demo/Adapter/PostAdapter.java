package com.vampyr.demo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vampyr.demo.Model.Post;
import com.vampyr.demo.Model.Users;
import com.vampyr.demo.R;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context context;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> mPost) {
        this.context = context;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(position);

        Glide.with(context).load(post.getPostimage()).into(holder.image_post);

        if (post.getDescription().equals("")){
            holder.description.setVisibility(View.GONE);
        }else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        publisherInfo(holder.image_profile, holder.username, post.getPublisher());

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile,image_post,image_like,image_comment,image_share,options;
        public TextView username,postTime,likeText,commentText,description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            image_post = itemView.findViewById(R.id.post_image);
            image_like = itemView.findViewById(R.id.like);
            image_comment = itemView.findViewById(R.id.comment);
            image_share = itemView.findViewById(R.id.share);
            options = itemView.findViewById(R.id.options);
            username = itemView.findViewById(R.id.username);
            postTime = itemView.findViewById(R.id.postTime);
            likeText = itemView.findViewById(R.id.likeText);
            commentText = itemView.findViewById(R.id.commentText);
            description = itemView.findViewById(R.id.description);

        }
    }

    private void publisherInfo(final ImageView image_profile, final TextView username, String userID){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                Glide.with(context).load(users.getImageurl()).into(image_profile);
                username.setText(users.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
