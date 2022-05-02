package com.example.secondhandbooks.user.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.model.MessagesModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter{

    private final Context context;
    private final ArrayList<MessagesModel> list;

    private int ITEM_SEND = 1;
    private int ITEM_RECEIVED = 2;

    public MessagesAdapter(Context context, ArrayList<MessagesModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_chat_item, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.receiver_chat_item, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessagesModel model = list.get(position);

        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.textView.setText(model.getMessage());
        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.textView.setText(model.getMessage());
        }

    }

    @Override
    public int getItemViewType(int position) {
        MessagesModel model = list.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(model.getSenderId())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVED;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.sender_message_tv);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.receiver_message_tv);
        }
    }
}
