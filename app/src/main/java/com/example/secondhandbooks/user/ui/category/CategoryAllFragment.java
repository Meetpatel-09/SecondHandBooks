package com.example.secondhandbooks.user.ui.category;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.adapter.BooksAdapter;
import com.example.secondhandbooks.model.BookModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryAllFragment extends Fragment {

    private RecyclerView recyclerViewBook;
    private BooksAdapter adapter;
    private List<BookModel> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category_all, container, false);

        String data = requireContext().getSharedPreferences("CATEGORY", Context.MODE_PRIVATE).getString("categoryName", null);

        TextView categoryName = view.findViewById(R.id.category_name);
        categoryName.setText(data);

        recyclerViewBook = view.findViewById(R.id.recycler_view_books);
        recyclerViewBook.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);

        recyclerViewBook.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        adapter = new BooksAdapter(getContext(), list);
        recyclerViewBook.setAdapter(adapter);

        showNewAvailable();

        return view;
    }

    private void showNewAvailable() {

        FirebaseDatabase.getInstance().getReference().child("books").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BookModel model = dataSnapshot.getValue(BookModel.class);

                    String data = requireContext().getSharedPreferences("CATEGORY", Context.MODE_PRIVATE).getString("categoryName", null);
                    if (model.getCategory().equals(data))
                        list.add(model);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}