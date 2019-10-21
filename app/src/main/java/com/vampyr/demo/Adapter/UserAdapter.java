package com.vampyr.demo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vampyr.demo.Activities.MainActivity;
import com.vampyr.demo.Activities.UsersActivity;
import com.vampyr.demo.Fragments.ProfileFragment;
import com.vampyr.demo.Model.Users;
import com.vampyr.demo.R;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<Users> mUsers;

    FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<Users> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final Users users = mUsers.get(position);

        holder.followText.setVisibility(View.VISIBLE);
        holder.username.setText(users.getUsername());
        holder.fullname.setText(users.getBio());
        Glide.with(mContext).load(users.getImageurl()).into(holder.image_Profile);
        isFollowing(users.getId(), holder.followText);

        if (users.getId().equals(firebaseUser.getUid())) {

            holder.followText.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("profileid", users.getId());
                    editor.apply();
                    startActivity(mContext);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView fullname;
        public CircleImageView image_Profile;
        public TextView followText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_Profile = itemView.findViewById(R.id.image_profile);
            followText = itemView.findViewById(R.id.follow_btn);
        }
    }


    private void isFollowing(final String userid, final TextView button) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(userid).exists()) {
                    button.setText("following");
                } /*else {
                    button.setText("follow");
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, UsersActivity.class));
    }
}
