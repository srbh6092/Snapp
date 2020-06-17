package com.saurabh.snapp.Receiver;

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

public class ReceiverAdapter extends RecyclerView.Adapter<ReceiverViewHolders> {

    private List<ReceiverObject> userList;
    private Context context;

    public ReceiverAdapter(List<ReceiverObject> userList, Context context)
    {
        this.userList=userList;
        this.context=context;
    }

    @NonNull
    @Override
    public ReceiverViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_receiver,null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ReceiverViewHolders rcv = new ReceiverViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final ReceiverViewHolders holder, int position) {
        holder.mEmail.setText(userList.get(position).getEmail());
        holder.mReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean receiveState = !userList.get(holder.getLayoutPosition()).getReceive();
                userList.get(holder.getLayoutPosition()).setReceive(receiveState);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
}
