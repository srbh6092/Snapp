package com.saurabh.snapp.Follow;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.saurabh.snapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FollowViewHolders extends RecyclerView.ViewHolder {

    TextView mEmail;
    Button mFollow;
    public FollowViewHolders(@NonNull View itemView) {
        super(itemView);
        mEmail = itemView.findViewById(R.id.email);
        mFollow = itemView.findViewById(R.id.follow);
    }
}
