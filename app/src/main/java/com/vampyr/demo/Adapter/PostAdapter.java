package com.vampyr.demo.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vampyr.demo.Activities.CommentsActivity;
import com.vampyr.demo.Activities.FollowersActivity;
import com.vampyr.demo.Activities.UsersActivity;
import com.vampyr.demo.Fragments.PostDetailsFragment;
import com.vampyr.demo.Fragments.ProfileFragment;
import com.vampyr.demo.Model.Post;
import com.vampyr.demo.Model.Users;
import com.vampyr.demo.R;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPost.get(position);

        Glide.with(context).load(post.getPostimage())
                .apply(new RequestOptions().placeholder(R.drawable.image_placeholder))
                .into(holder.image_post);

        if (post.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        publisherInfo(holder.image_profile, holder.username, post.getPublisher());
        isLiked(post.getPostid(), holder.image_like);
        nrLikes(holder.likeText, post.getPostid());
        getComments(post.getPostid(), holder.commentText);

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

               // ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                UserProfileActivity(context);
            }
        });


        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", context.MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();

               // ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                UserProfileActivity(context);
            }
        });


//        holder.image_post.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", context.MODE_PRIVATE).edit();
//                editor.putString("postID", post.getPostid());
//                editor.apply();
//
//                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailsFragment()).commit();
//            }
//        });



        holder.image_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.image_like.getTag().equals("Like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid())
                            .child(firebaseUser.getUid())
                            .setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid())
                            .child(firebaseUser.getUid())
                            .removeValue();

                }
            }
        });

        holder.image_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra("postID", post.getPostid());
                intent.putExtra("publisherID", post.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.commentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra("postID", post.getPostid());
                intent.putExtra("publisherID", post.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.Edit:
                                EditPost(post.getPostid());
                                return true;
                            case R.id.delete:
                                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                                 FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostid())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                return true;
                            case R.id.report:
                                Toast.makeText(context, "Report clicked!", Toast.LENGTH_SHORT).show();
                                return true;
                            case R.id.savePost:
                                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                                            .child(post.getPostid()).setValue(true);
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popupMenu.inflate(R.menu.post_menu);
                if (!post.getPublisher().equals(firebaseUser.getUid())) {
                    popupMenu.getMenu().findItem(R.id.Edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });

        holder.likeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FollowersActivity.class);
                intent.putExtra("id", post.getPostid());
                intent.putExtra("title","Likes");
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, image_post, image_like, image_comment, image_share, options;
        public TextView username, postTime, likeText, commentText, description;

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

    private void publisherInfo(final ImageView image_profile, final TextView username, String userID) {

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

    private void isLiked(String postID, final ImageView imageView) {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("Liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void nrLikes(final TextView likes, String postID) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int totalLikes = (int) dataSnapshot.getChildrenCount();

                likes.setText(Integer.toString(totalLikes));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getComments(String postID, final TextView comments) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalComments = (int) dataSnapshot.getChildrenCount();
                comments.setText(Integer.toString(totalComments));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void EditPost(final String postID) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("Edit Post");

        final EditText editText = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        getText(postID, editText);

        alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("description", editText.getText().toString().trim());

                FirebaseDatabase.getInstance().getReference("Posts").child(postID).updateChildren(hashMap);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void getText(String postID, final EditText editText) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                editText.setText(dataSnapshot.getValue(Post.class).getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void UserProfileActivity(Context context) {
        context.startActivity(new Intent(context, UsersActivity.class));
    }
}
