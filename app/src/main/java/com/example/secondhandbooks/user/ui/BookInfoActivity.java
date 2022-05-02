package com.example.secondhandbooks.user.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.admin.books.ManageBookActivity;
import com.example.secondhandbooks.model.UserModel;
import com.example.secondhandbooks.user.MainActivity;
import com.example.secondhandbooks.user.ui.chat.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class BookInfoActivity extends AppCompatActivity {

    private  ImageView book_image;
    private TextView b_title, b_category, b_price, b_author, s_name, b_ISBN, b_pages, b_language, b_publication_year;
    private Button b_chat, b_watch_list, b_remove;

    private String b_id, s_id, name_s, seller_profile_mage;

    private FirebaseUser firebaseUser;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        book_image = findViewById(R.id.book_img);

        b_title = findViewById(R.id.b_title);
        b_category = findViewById(R.id.b_category);
        b_author = findViewById(R.id.b_author);
        s_name = findViewById(R.id.s_name);
        b_price = findViewById(R.id.b_price);
        b_ISBN = findViewById(R.id.b_ISBN);
        b_pages = findViewById(R.id.b_pages);
        b_language = findViewById(R.id.b_language);
        b_publication_year = findViewById(R.id.b_publication_year);

        b_watch_list = findViewById(R.id.add_to_watch_list);
        b_remove = findViewById(R.id.remove_book);
        b_chat = findViewById(R.id.b_chat);

        Intent intent=getIntent();
        final String book_id = intent.getStringExtra("book_id");
        final String book_title = intent.getStringExtra("book_title");
        final String image = intent.getStringExtra("book_thumbnail");
        final String book_author = intent.getStringExtra("book_author");
//        final String book_desc = intent.getStringExtra("book_desc");
        final String book_cat = intent.getStringExtra("book_cat");
        final String seller_id = intent.getStringExtra("sellerId");
        final String selling_price = intent.getStringExtra("selling_price");
        final String book_pages = intent.getStringExtra("pages");
        final String book_ISBN = intent.getStringExtra("ISBN");
        final String book_language = intent.getStringExtra("language");
        final String book_publication_year = intent.getStringExtra("publicationYear");

        SharedPreferences sharedPreferences = this.getSharedPreferences("login", MODE_PRIVATE);
//        editor = sharedPreferences.edit();

        if (sharedPreferences.getString("isLogin", "false").equals("yes")) {
            b_remove.setVisibility(View.VISIBLE);
            b_watch_list.setVisibility(View.GONE);
            b_chat.setVisibility(View.GONE);
        } else {
            if (seller_id.equals(firebaseUser.getUid())) {
                b_remove.setVisibility(View.VISIBLE);
            }
            checkWatchList();
        }

        String price = "₹ " + selling_price;
        String language = "Language: " + book_language;
        String publication_year = "Publication Year: " + book_publication_year;
        String ISBN = "ISBN: " + book_ISBN;
        String pages = "Pages: " + book_pages;
        b_id = book_id;
        s_id = seller_id;

        b_title.setText(book_title);
        b_category.setText(book_cat);
        b_author.setText(book_author);
//        s_name.setText(book_title);
        b_price.setText(price);
        b_ISBN.setText(ISBN);
        b_pages.setText(pages);
        b_language.setText(language);
        b_publication_year.setText(publication_year);

        getSellerDetails();

        try {
            Picasso.get().load(image).placeholder(R.drawable.loading_shape).into(book_image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        b_chat.setOnClickListener(view -> {
            Intent i = new Intent(BookInfoActivity.this, ChatActivity.class);
            i.putExtra("sellerId", s_id);
            i.putExtra("sellerName", name_s);
            i.putExtra("image", seller_profile_mage);
            startActivity(i);
        });

        b_remove.setOnClickListener(view -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete this book?");
            builder.setCancelable(true);
            builder.setPositiveButton("Yes", (dialog, which) -> {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("books");
                reference.child(b_id).removeValue().addOnCompleteListener(task -> {
                    Toast.makeText(this, "Book Deleted Successfully", Toast.LENGTH_SHORT).show();

                    if (sharedPreferences.getString("isLogin", "false").equals("yes")) {
                        startActivity(new Intent(BookInfoActivity.this, ManageBookActivity.class));
                    } else {
                        startActivity(new Intent(BookInfoActivity.this, MainActivity.class));
                    }

                    finish();
                }).addOnFailureListener(e -> Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show());

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

    private void checkWatchList() {

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("watchlist").exists()) {
                    FirebaseDatabase.getInstance().getReference().child("watchlist").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot2) {
                            if (snapshot2.child(b_id).exists()) {
                                b_watch_list.setText(R.string.remove_from_watch_list);
                                b_watch_list.setOnClickListener(view -> {
                                    FirebaseDatabase.getInstance().getReference().child("watchlist").child(firebaseUser.getUid()).child(b_id).removeValue();
                                    Toast.makeText(BookInfoActivity.this, "Book removed from watchlist.", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                b_watch_list.setText(R.string.add_to_watch_list);
                                b_watch_list.setOnClickListener(view -> {
                                    FirebaseDatabase.getInstance().getReference().child("watchlist").child(firebaseUser.getUid()).child(b_id).setValue(true);
                                    Toast.makeText(BookInfoActivity.this, "Book added to watchlist.", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    b_watch_list.setText(R.string.add_to_watch_list);
                    b_watch_list.setOnClickListener(view -> {
                        FirebaseDatabase.getInstance().getReference().child("watchlist").child(firebaseUser.getUid()).child(b_id).setValue(true);
                        Toast.makeText(BookInfoActivity.this, "Book added to watchlist.", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSellerDetails() {
        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel data = dataSnapshot.getValue(UserModel.class);

                    assert data != null;
                    if (data.getId().equals(s_id)) {
                            s_name.setText(data.getName());
                            name_s = data.getName();
                            seller_profile_mage = data.getImageUrl();
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}