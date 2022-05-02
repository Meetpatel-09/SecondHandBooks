package com.example.secondhandbooks.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.admin.books.ManageBookActivity;
import com.example.secondhandbooks.model.BookModel;
import com.example.secondhandbooks.model.UserModel;
import com.example.secondhandbooks.user.MainActivity;
import com.example.secondhandbooks.user.ui.BookInfoActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final Context context;
    private final List<UserModel> list;

    public UserAdapter(Context context, List<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final UserModel model = list.get(position);

        String userID = model.getId();

        holder.textView.setText(model.getName());

        holder.button.setText("Remove");
        holder.button.setVisibility(View.VISIBLE);

        if (model.getImageUrl().equals("default")){
            holder.imageView.setImageResource(R.drawable.profile_img);
        } else {
            Picasso.get().load(model.getImageUrl()).into(holder.imageView);
        }

        holder.button.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to remove this User?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", (dialog, which) -> {
                removeBooks(userID);
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users");
                reference.child(model.getId()).removeValue().addOnCompleteListener(task -> {
                    Toast.makeText(context, "User Removed Successfully", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> Toast.makeText(context, "Something went wrong!!", Toast.LENGTH_SHORT).show());
            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());

            AlertDialog dialog = null;
            try {
                dialog = builder.create();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (dialog != null)
                dialog.show();
        });
    }

    private void removeBooks(String userID) {

        FirebaseDatabase.getInstance().getReference().child("books").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BookModel data = dataSnapshot.getValue(BookModel.class);{
                        assert data != null;
                        if (data.getUserId().equals(userID)) {

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("books");
                            reference.child(data.getId()).removeValue();

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        private TextView textView;
        private Button button;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.user_image_profile);
            textView = itemView.findViewById(R.id.user_name);
            button = itemView.findViewById(R.id.btn_chat);
        }
    }
}
