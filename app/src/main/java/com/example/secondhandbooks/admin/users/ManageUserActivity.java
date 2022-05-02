package com.example.secondhandbooks.admin.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.adapter.UserAdapter;
import com.example.secondhandbooks.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;

public class ManageUserActivity extends AppCompatActivity {

    private RecyclerView deleteUserRecyclerview;
    private ProgressBar progressBar;
    private ArrayList<UserModel> list;
    private UserAdapter adapter;

    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_user);

        deleteUserRecyclerview = findViewById(R.id.manage_user_recyclerview);
        progressBar = findViewById(R.id.progress_bar_u);

        reference = FirebaseDatabase.getInstance().getReference().child("users");

        deleteUserRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        deleteUserRecyclerview.setHasFixedSize(true);

        getUsers();
    }

    private void getUsers() {

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel data = dataSnapshot.getValue(UserModel.class);
                    list.add(data);
                }

                adapter = new UserAdapter(ManageUserActivity.this, list);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                deleteUserRecyclerview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageUserActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}