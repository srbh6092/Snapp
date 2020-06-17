package com.saurabh.snapp.Story;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.saurabh.snapp.DisplayImageActivity;
import com.saurabh.snapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView mEmail;
    public LinearLayout mLayout;
    public StoryViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mEmail = itemView.findViewById(R.id.email);
        mLayout = itemView.findViewById(R.id.layout);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), DisplayImageActivity.class);
        Bundle b=new Bundle();
        b.putString("userID",mEmail.getTag().toString());
        b.putString("chatOrStory",mLayout.getTag().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
