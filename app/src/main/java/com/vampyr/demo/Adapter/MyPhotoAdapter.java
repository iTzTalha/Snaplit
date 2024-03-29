package com.vampyr.demo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vampyr.demo.Activities.MainActivity;
import com.vampyr.demo.Activities.UsersActivity;
import com.vampyr.demo.Fragments.PostDetailsFragment;
import com.vampyr.demo.Model.Post;
import com.vampyr.demo.Model.Users;
import com.vampyr.demo.R;

import java.util.List;

public class MyPhotoAdapter extends RecyclerView.Adapter<MyPhotoAdapter.ViewHolder> {

    private Context context;
    private List<Post> mPost;
    private boolean isFragment;

    public MyPhotoAdapter(Context context, List<Post> mPost, boolean isFragment) {
        this.context = context;
        this.mPost = mPost;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);

        return new MyPhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Post post = mPost.get(position);

        Glide.with(context).load(post.getPostimage()).into(holder.post_image);

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFragment) {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", context.MODE_PRIVATE).edit();
                    editor.putString("postID", post.getPostid());
                    editor.apply();

                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailsFragment()).commit();
                } else {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("postID", post.getPostid());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
        }
    }
}
