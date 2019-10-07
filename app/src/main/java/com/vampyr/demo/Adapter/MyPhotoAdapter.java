package com.vampyr.demo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vampyr.demo.Model.Post;
import com.vampyr.demo.R;

import java.util.List;

public class MyPhotoAdapter extends RecyclerView.Adapter<MyPhotoAdapter.ViewHolder>{

    private Context context;
    private List<Post> mPost;

    public MyPhotoAdapter(Context context, List<Post> mPost) {
        this.context = context;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);

        return new MyPhotoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Post post = mPost.get(position);

        Glide.with(context).load(post.getPostimage()).into(holder.post_image);

    }

    @Override
    public int getItemCount() {

        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);
        }
    }
}
