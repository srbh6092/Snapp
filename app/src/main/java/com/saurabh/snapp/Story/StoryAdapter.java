package com.saurabh.snapp.Story;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saurabh.snapp.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StoryAdapter extends RecyclerView.Adapter<StoryViewHolders> {

    private List<StoryObject> userList;
    private Context context;

    public StoryAdapter(List<StoryObject> userList, Context context)
    {
        this.userList=userList;
        this.context=context;
    }

    @NonNull
    @Override
    public StoryViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_story,null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        StoryViewHolders rcv = new StoryViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final StoryViewHolders holder, int position) {
        holder.mEmail.setText(userList.get(position).getEmail());
        holder.mEmail.setTag(userList.get(position).getUid());
        holder.mLayout.setTag(userList.get(position).getChatOrStory());
    }

    @Override
    public int getItemCount() {
        return this.userList.size();
    }
}
