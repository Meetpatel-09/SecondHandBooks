package com.example.secondhandbooks.user.ui.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.model.MessagesModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private EditText etSendMessage;
    private ImageView sendMessage;
    private ImageView backIcon;

    private CircleImageView profileImage;
    private TextView tvReceiverName;

    private RecyclerView rvChat;

    private String enteredMessage;
    private String senderId, senderName, receiverId, receiverName, sellerImage;

    FirebaseDatabase firebaseDatabase;

    private String senderRoom, receiverRoom;

    private String currentTime;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;

    private MessagesAdapter adapter;
    private ArrayList<MessagesModel> list;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        etSendMessage = findViewById(R.id.et_message);
        sendMessage = findViewById(R.id.send_message_icon);
        backIcon = findViewById(R.id.bank_icon);

        profileImage = findViewById(R.id.receiver_profile_image);
        tvReceiverName = findViewById(R.id.receiver_name);

        rvChat = findViewById(R.id.rv_chat);
        list = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(linearLayoutManager);
        adapter = new MessagesAdapter(ChatActivity.this, list);
        rvChat.setAdapter(adapter);

        Intent intent = getIntent();

        assert fUser != null;
        senderId = fUser.getUid();
        receiverId = getIntent().getStringExtra("sellerId");
        receiverName = getIntent().getStringExtra("sellerName");
        sellerImage = getIntent().getStringExtra("image");

        senderRoom = senderId + receiverId;
        receiverRoom = receiverId + senderId;

        backIcon.setOnClickListener(v -> finish());

        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("hh:mm a");

        setSenderProfile();

        getMessages();

        sendMessage.setOnClickListener(v -> sendMessage());
    }

    private void setSenderProfile() {
        tvReceiverName.setText(receiverName);

        if (!sellerImage.isEmpty()) {
            Picasso.get().load(sellerImage).placeholder(R.drawable.profile_img).into(profileImage);
        }
    }

    private void getMessages() {
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages");
        adapter = new MessagesAdapter(ChatActivity.this, list);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessagesModel model = dataSnapshot.getValue(MessagesModel.class);
                    list.add(model);
                }
                adapter.notifyDataSetChanged();
                rvChat.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage() {
        enteredMessage = etSendMessage.getText().toString();

        if (enteredMessage.isEmpty()) {
            Toast.makeText(this, "Please enter message then click on send", Toast.LENGTH_SHORT).show();
        } else {
            Date date = new Date();
            currentTime = simpleDateFormat.format(calendar.getTime());

            long d = date.getTime();

            MessagesModel model = new MessagesModel(enteredMessage, senderId, receiverId, d, currentTime);

            firebaseDatabase.getReference().child("chat").child(senderId).child(receiverId).setValue("true");
            firebaseDatabase.getReference().child("chat").child(receiverId).child(senderId).setValue("true");

            firebaseDatabase.getReference().child("chats").child(senderRoom).child("messages").push().setValue(model).addOnCompleteListener(task -> firebaseDatabase.getReference().child("chats").child(receiverRoom).child("messages").push().setValue(model).addOnCompleteListener(task1 -> {
                etSendMessage.setText(null);
                etSendMessage.clearFocus();
                getMessages();
                etSendMessage.requestFocus();
            })).addOnFailureListener(e -> {
                Toast.makeText(ChatActivity.this, "Something went wrong while sending message", Toast.LENGTH_SHORT).show();
                etSendMessage.setText(null);
                etSendMessage.clearFocus();
            });
        }
    }

}