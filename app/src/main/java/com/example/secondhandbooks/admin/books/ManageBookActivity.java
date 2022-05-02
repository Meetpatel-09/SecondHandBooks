package com.example.secondhandbooks.admin.books;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.adapter.BooksAdapter;
import com.example.secondhandbooks.model.BookModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class ManageBookActivity extends AppCompatActivity {

    private RecyclerView deleteBookRecyclerview;
    private ProgressBar progressBar;
    private ArrayList<BookModel> list;
    private BooksAdapter adapter;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_book);

        deleteBookRecyclerview = findViewById(R.id.manage_book_recyclerview);
        progressBar = findViewById(R.id.progress_bar);

        reference = FirebaseDatabase.getInstance().getReference().child("books");

        deleteBookRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        deleteBookRecyclerview.setHasFixedSize(true);

        getBook();

    }

    private void getBook() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BookModel data = dataSnapshot.getValue(BookModel.class);
                    list.add(data);
                }

                adapter = new BooksAdapter(ManageBookActivity.this, list);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                deleteBookRecyclerview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageBookActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}