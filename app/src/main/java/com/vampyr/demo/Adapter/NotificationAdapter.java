package com.vampyr.demo.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vampyr.demo.Activities.MainActivity;
import com.vampyr.demo.Activities.NotificationsActivity;
import com.vampyr.demo.Activities.UsersActivity;
import com.vampyr.demo.Fragments.PostDetailsFragment;
import com.vampyr.demo.Fragments.ProfileFragment;
import com.vampyr.demo.Model.Notifications;
import com.vampyr.demo.Model.Post;
import com.vampyr.demo.Model.Users;
import com.vampyr.demo.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    private List<Notifications> mNotifications;

    public NotificationAdapter(Context mContext, List<Notifications> mNotifications) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_notification, parent, false);

        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Notifications notifications = mNotifications.get(position);

        holder.text.setText(notifications.getText());

        getUserInfo(holder.image_profile, holder.username, notifications.getUserid());

        if (notifications.isIspost()) {
            holder.post_image.setVisibility(View.VISIBLE);
            getPostImage(holder.post_image, notifications.getPostid());
        } else {
            holder.post_image.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notifications.isIspost()) {
//                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
//                    editor.putString("postid",notifications.getPostid());
//                    editor.apply();
//
//                    startActivity(mContext);
//
//                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailsFragment()).commit();
                } else {
//                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
//                    editor.putString("profileid",notifications.getPostid());
//                    editor.apply();
//
//                    UserActivity(mContext);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, post_image;
        public TextView username, text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            text = itemView.findViewById(R.id.comment);

        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                Glide.with(mContext).load(users.getImageurl()).into(imageView);
                username.setText(users.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostImage(final ImageView imageView, String postid) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getPostimage()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    public static void UserActivity(Context context) {
        context.startActivity(new Intent(context, UsersActivity.class));
    }
}
