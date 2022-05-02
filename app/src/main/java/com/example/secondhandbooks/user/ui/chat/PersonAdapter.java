package com.example.secondhandbooks.user.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.model.UserModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder>{

    private final Context context;
    private final List<UserModel> list;

    public PersonAdapter(Context context, List<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new PersonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        final UserModel model = list.get(position);

        holder.textView.setText(model.getName());

        holder.button.setText("Chat");
        holder.button.setVisibility(View.VISIBLE);

        if (model.getImageUrl().equals("default")){
            holder.imageView.setImageResource(R.drawable.profile_img);
        } else {
            Picasso.get().load(model.getImageUrl()).into(holder.imageView);
        }

        holder.button.setOnClickListener(view -> {
            Intent i = new Intent(context, ChatActivity.class);
            i.putExtra("sellerId", model.getId());
            i.putExtra("sellerName", model.getName());
            i.putExtra("image", model.getImageUrl());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PersonViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        private TextView textView;
        private Button button;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.user_image_profile);
            textView = itemView.findViewById(R.id.user_name);
            button = itemView.findViewById(R.id.btn_chat);
        }
    }
}
