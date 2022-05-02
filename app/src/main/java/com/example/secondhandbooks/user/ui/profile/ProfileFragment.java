package com.example.secondhandbooks.user.ui.profile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.StartActivity;
import com.example.secondhandbooks.adapter.BooksAdapter;
import com.example.secondhandbooks.model.BookModel;
import com.example.secondhandbooks.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerViewSaved;
    private BooksAdapter saveAdapter;
    private List<BookModel> myListSaved;

    private RecyclerView recyclerView;
    private BooksAdapter uploadAdapter;
    private List<BookModel> myUploadList;

    private CircleImageView imageProfile;
    private TextView fullName;

    String profileId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imageProfile = view.findViewById(R.id.image_profile);
        ImageView logOut = view.findViewById(R.id.log_out);
        fullName = view.findViewById(R.id.full_name);
        ImageView bookmarked = view.findViewById(R.id.bookmarked);
        ImageView uploaded = view.findViewById(R.id.uploaded);
        Button editProfile = view.findViewById(R.id.edit_profile);

        recyclerView = view.findViewById(R.id.recycle_view_uploaded);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myUploadList = new ArrayList<>();
        uploadAdapter = new BooksAdapter(getContext(), myUploadList);
        recyclerView.setAdapter(uploadAdapter);

        recyclerViewSaved = view.findViewById(R.id.recycle_view_bookmarked);
        recyclerViewSaved.setHasFixedSize(true);
        recyclerViewSaved.setLayoutManager(new LinearLayoutManager(getActivity()));
        myListSaved = new ArrayList<>();
        saveAdapter = new BooksAdapter(getContext(), myListSaved);
        recyclerViewSaved.setAdapter(saveAdapter);

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        if (fUser != null) {
            profileId = fUser.getUid();
        } else {
            startActivity(new Intent(getContext(), StartActivity.class));
        }

        userInfo();
        getUploadedBooks();
        getSavedBook();

        editProfile.setOnClickListener(v -> startActivity(new Intent(getContext(), EditProfileActivity.class)));

        logOut.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), StartActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

        });

        recyclerViewSaved.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        bookmarked.setOnClickListener(v -> {

            recyclerViewSaved.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        });

        uploaded.setOnClickListener(v -> {

            recyclerViewSaved.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        });

        return view;
    }

    private void getSavedBook() {
        final List<String> savedIds = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("watchlist").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    savedIds.add(dataSnapshot.getKey());
                }

                FirebaseDatabase.getInstance().getReference().child("books").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        myListSaved.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            BookModel data = dataSnapshot.getValue(BookModel.class);

                            for (String id : savedIds) {
                                assert data != null;
                                if (data.getId().equals(id)) {
                                    myListSaved.add(data);
                                }
                            }
                        }

                        saveAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUploadedBooks() {
        FirebaseDatabase.getInstance().getReference().child("books").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myUploadList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    BookModel data = dataSnapshot.getValue(BookModel.class);

                    assert data != null;
                    if (data.getId() != null) {
                        if (data.getUserId().equals(profileId)) {
                            myUploadList.add(data);
                        }
                    }
                }

                Collections.reverse(myUploadList);
                uploadAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo() {

        FirebaseDatabase.getInstance().getReference().child("users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserModel users = snapshot.getValue(UserModel.class);

                assert users != null;
                if (users.getImageUrl().equals("default")){
                    imageProfile.setImageResource(R.drawable.profile_img);
                }else {
                    Picasso.get().load(users.getImageUrl()).into(imageProfile);
                }
                fullName.setText(users.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}