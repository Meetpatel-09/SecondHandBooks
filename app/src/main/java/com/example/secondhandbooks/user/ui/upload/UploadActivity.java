package com.example.secondhandbooks.user.ui.upload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.user.MainActivity;
import com.example.secondhandbooks.user.ui.category.CategoryAllFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UploadActivity extends AppCompatActivity {

    private ImageView bookImageView;

    private Spinner category;

    private EditText bookTitle;
    private EditText author;
    private EditText publicationYear;
    private EditText ISBN;
    private EditText language;
    private EditText pages;
    private EditText price;

    private String sAuthor;
    private String sBookTitle;
    private String sPublicationYear;
    private String sISBN;
    private String sLanguage;
    private String sPages;
    private String sPrice;
    private String sCategory;

    private FirebaseAuth auth;

    private StorageTask<UploadTask.TaskSnapshot> uploadTask;

    private DatabaseReference reference;
    private FirebaseStorage storage;

    private Uri mImageUri;
    String downloadUrl = "";
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        reference = FirebaseDatabase.getInstance().getReference();

        pd = new ProgressDialog(this);

        MaterialCardView addImage = findViewById(R.id.add_image);
        bookImageView = findViewById(R.id.book_image);

        category = findViewById(R.id.reg_category);

        bookTitle = findViewById(R.id.book_title);
        author = findViewById(R.id.author_name);
        publicationYear = findViewById(R.id.publish_year);
        ISBN = findViewById(R.id.ISBN);
        language = findViewById(R.id.language);
        pages = findViewById(R.id.pages);
        price = findViewById(R.id.price);

        setSpinner();

        Button uploadNotice = findViewById(R.id.upload_notice_button);
        Button cancel = findViewById(R.id.cancel_upload);

        addImage.setOnClickListener(v -> CropImage.activity().setCropShape(CropImageView.CropShape.RECTANGLE).start(UploadActivity.this));

        uploadNotice.setOnClickListener(v -> validateData());

        cancel.setOnClickListener(v -> startActivity(new Intent(UploadActivity.this, MainActivity.class)));

    }

    private void setSpinner() {
        final List<String> catItems = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    catItems.add(dataSnapshot.getKey());
                }

                final ArrayAdapter<String> adapter = new ArrayAdapter<>(UploadActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, catItems);
                category.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void validateData() {

        sCategory = category.getSelectedItem().toString();

        sBookTitle = bookTitle.getText().toString();
        sAuthor = author.getText().toString();
        sPublicationYear = publicationYear.getText().toString();
        sISBN = ISBN.getText().toString();
        sLanguage = language.getText().toString();
        sPages = pages.getText().toString();
        sPrice = price.getText().toString();

        if (sBookTitle.isEmpty()) {
            bookTitle.setError("Required");
            bookTitle.requestFocus();
        } else if (sAuthor.isEmpty()) {
            author.setError("Required");
            author.requestFocus();
        } else if (sPublicationYear.isEmpty()) {
            publicationYear.setError("Required");
            publicationYear.requestFocus();
        } else if (sISBN.isEmpty()) {
            ISBN.setError("Required");
            ISBN.requestFocus();
        } else if (sLanguage.isEmpty()) {
            language.setError("Required");
            language.requestFocus();
        } else if (sPages.isEmpty()) {
            pages.setError("Required");
            pages.requestFocus();
        } else if (sPrice.isEmpty()) {
            price.setError("Required");
            price.requestFocus();
        } else if (sCategory.equals("Select Category")) {
            price.setError("Select Category");
            price.requestFocus();
        } else if (mImageUri == null) {
            price.setError("Select Image");
            price.requestFocus();
        } else {
            uploadBookImage();
        }
    }

    private void uploadBookImage() {
        pd.setMessage("Uploading...");
        pd.show();
        String uniqueString = UUID.randomUUID().toString();

        final StorageReference referenceForProfile = storage.getReference().child("books").child(uniqueString+".jpeg");

        uploadTask = referenceForProfile.putFile(mImageUri);
        uploadTask.addOnCompleteListener(UploadActivity.this, task -> {
            if (task.isSuccessful()) {
                uploadTask.addOnSuccessListener(taskSnapshot -> referenceForProfile.getDownloadUrl().addOnSuccessListener(uri -> {
                    downloadUrl = String.valueOf(uri);
                    uploadData();
                }));
            } else {
                pd.dismiss();
                Toast.makeText(UploadActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadData() {
        DatabaseReference dbRef = reference.child("books");
        final String uniqueKey = dbRef.push().getKey();

        HashMap<String, String> map = new HashMap<>();
        map.put("id", uniqueKey);
        map.put("userId", Objects.requireNonNull(auth.getCurrentUser()).getUid());
        map.put("title", sBookTitle);
        map.put("author", sAuthor);
        map.put("publicationYear", sPublicationYear);
        map.put("ISBN", sISBN);
        map.put("category", sCategory);
        map.put("language", sLanguage);
        map.put("pages", sPages);
        map.put("price", sPrice);
        map.put("bookImage", downloadUrl);

        assert uniqueKey != null;
        dbRef.child(uniqueKey).setValue(map).addOnSuccessListener(unused -> {
            pd.dismiss();
            Toast.makeText(UploadActivity.this, "Book Uploaded Successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UploadActivity.this, MainActivity.class));
            finish();
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(UploadActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(UploadActivity.this, MainActivity.class));
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            assert result != null;

            mImageUri = result.getUri();
            bookImageView.setImageURI(mImageUri);
            bookImageView.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }
}