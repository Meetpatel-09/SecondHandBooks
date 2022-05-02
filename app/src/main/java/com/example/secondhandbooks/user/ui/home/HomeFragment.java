package com.example.secondhandbooks.user.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.secondhandbooks.R;
import com.example.secondhandbooks.StartActivity;
import com.example.secondhandbooks.adapter.BooksAdapter;
import com.example.secondhandbooks.model.BookModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private BooksAdapter adapter;
    private ArrayList<BookModel> list;

    private DatabaseReference reference;

    RecyclerView recycler_home_page_books;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        reference = FirebaseDatabase.getInstance().getReference().child("books");

        ImageView logOut = view.findViewById(R.id.log_out);

        recycler_home_page_books = view.findViewById(R.id.recycler_home_page_books);
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);

        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        ArrayList<SlideModel> images = new ArrayList<>();
        images.add(new SlideModel(R.drawable.bookbanner, null));
        images.add(new SlideModel(R.drawable.bookbanner2, null));

        imageSlider.setImageList(images, ScaleTypes.CENTER_CROP);

        recycler_home_page_books.setHasFixedSize(true);
        recycler_home_page_books.setLayoutManager(new LinearLayoutManager(getActivity()));

        logOut.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

        });

        showNewAvailable();

        pullToRefresh.setOnRefreshListener(() -> {
            showNewAvailable();
            pullToRefresh.setRefreshing(false);
        });

        return view;
    }

    private void showNewAvailable() {

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BookModel model = dataSnapshot.getValue(BookModel.class);
                    list.add(0, model);
                }

                adapter = new BooksAdapter(getContext(), list);
                adapter.notifyDataSetChanged();
                recycler_home_page_books.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}