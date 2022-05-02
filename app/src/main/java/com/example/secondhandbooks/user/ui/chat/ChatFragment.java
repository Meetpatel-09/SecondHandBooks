package com.example.secondhandbooks.user.ui.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatFragment extends Fragment {

    private RecyclerView recyclerViewChats;
    private PersonAdapter adapter;
    private List<UserModel> list;

    private FirebaseAuth auth;

    String profileId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerViewChats = view.findViewById(R.id.chat_recyclerview);
        recyclerViewChats.setHasFixedSize(true);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = new ArrayList<>();
        adapter = new PersonAdapter(getContext(), list);
        recyclerViewChats.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();

        profileId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        getChatters();

        return view;
    }

    private void getChatters() {
        final List<String> savedIds = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("chat").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    savedIds.add(dataSnapshot.getKey());
                }

                FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            UserModel data = dataSnapshot.getValue(UserModel.class);

                            for (String id : savedIds) {
                                assert data != null;
                                if (data.getId().equals(id)) {
                                    list.add(data);
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();
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
}