package com.example.secondhandbooks.admin.category;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.secondhandbooks.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RemoveCategoryActivity extends AppCompatActivity {

    private RecyclerView cat_rv;
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_category);

        cat_rv = findViewById(R.id.rv_cat);

        cat_rv.setHasFixedSize(true);
        cat_rv.setLayoutManager(new LinearLayoutManager(RemoveCategoryActivity.this));

        getCategories();
    }

    private void getCategories() {
        final List<String> catItems = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("category").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    catItems.add(dataSnapshot.getKey());
                }

                adapter = new CategoryAdapter(RemoveCategoryActivity.this, catItems);
                adapter.notifyDataSetChanged();
                cat_rv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}