package com.example.secondhandbooks.admin.category;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.admin.AdminHomeActivity;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final Context context;
    private final List<String> list;

    public CategoryAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {

        holder.catName.setText(list.get(position));

        holder.btnRemove.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("category").child(list.get(position)).removeValue();
            Toast.makeText(context, "Category Removed.", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, AdminHomeActivity.class));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView catName;
        private Button btnRemove;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            catName = itemView.findViewById(R.id.tv_cat);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}
