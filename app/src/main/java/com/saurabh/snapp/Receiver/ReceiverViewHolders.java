package com.saurabh.snapp.Receiver;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.saurabh.snapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReceiverViewHolders extends RecyclerView.ViewHolder {

    TextView mEmail;
    CheckBox mReceive;
    public ReceiverViewHolders(@NonNull View itemView) {
        super(itemView);
        mEmail = itemView.findViewById(R.id.email);
        mReceive = itemView.findViewById(R.id.checkBox);
    }
}
