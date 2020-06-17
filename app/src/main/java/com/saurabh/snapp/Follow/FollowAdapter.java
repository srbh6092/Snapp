package com.saurabh.snapp.Follow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.saurabh.snapp.R;
import com.saurabh.snapp.UserInformation;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FollowAdapter extends RecyclerView.Adapter<FollowViewHolders> {

    private List<FollowObject> userList;
    private Context context;

    public FollowAdapter(List<FollowObject> userList, Context context)
    {
        this.userList=userList;
        this.context=context;
    }

    @NonNull
    @Override
    public FollowViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_followers,null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        FollowViewHolders rcv = new FollowViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final FollowViewHolders holder, int position) {
        holder.mEmail.setText(userList.get(position).getEmail());

        if(UserInformation.listFollowing.contains(userList.get(holder.getLayoutPosition()).getUid()))
            holder.mFollow.setText("Following");
        else
            holder.mFollow.setText("Follow");

        holder.mFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(holder.mFollow.getText().equals("Follow"))
                {
                    holder.mFollow.setText("Following");
                    FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Following").child(userList.get(holder.getLayoutPosition()).getUid()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Users").child(userList.get(holder.getLayoutPosition()).getUid()).child("Followers").child(userId).setValue(true);
                }
                else
                {
                    holder.mFollow.setText("Follow");
                    FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Following").child(userList.get(holder.getLayoutPosition()).getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Users").child(userList.get(holder.getLayoutPosition()).getUid()).child("Followers").child(userId).removeValue();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
