package com.example.secondhandbooks.user.ui.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private CircleImageView imageProfile;

    private MaterialEditText fullName;
    private MaterialEditText mobileNumber;

    private FirebaseUser fUser;

    private String profileImageUrl;

    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView close = findViewById(R.id.close);
        imageProfile = findViewById(R.id.image_profile);

        TextView changePhoto = findViewById(R.id.change_photo);
        fullName = findViewById(R.id.ep_full_name);
        mobileNumber = findViewById(R.id.ep_mobile_number);

        Button save = findViewById(R.id.bt_save);
        Button cancel = findViewById(R.id.bt_cancel);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();

        FirebaseDatabase.getInstance().getReference().child("users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserModel users = snapshot.getValue(UserModel.class);

                assert users != null;
                fullName.setText(users.getName());
                mobileNumber.setText(users.getPhone());
                if (users.getImageUrl().equals("default")) {
                    imageProfile.setImageResource(R.drawable.profile_img);
                } else {
                    Picasso.get().load(users.getImageUrl()).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        close.setOnClickListener(v -> finish());

        cancel.setOnClickListener(v -> finish());

        changePhoto.setOnClickListener(v -> CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this));

        imageProfile.setOnClickListener(v -> CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this));

        save.setOnClickListener(v ->
            updateProfile()
        );
    }

    private void updateProfile() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("name", Objects.requireNonNull(fullName.getText()).toString());
        map.put("phone", Objects.requireNonNull(mobileNumber.getText()).toString());

        FirebaseDatabase.getInstance().getReference().child("users").child(fUser.getUid()).updateChildren(map);
        Toast.makeText(this, "Profile Updated.", Toast.LENGTH_SHORT).show();
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            assert result != null;

            Uri mImageUri = result.getUri();
            imageProfile.setImageURI(mImageUri);
            uploadProfileImage(mImageUri);

        } else {
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadProfileImage(Uri ImageUri) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        final StorageReference referenceForProfile = storage.getReference().child("users")
                .child(fUser.getUid()).child("profile.jpeg");

        uploadTask = referenceForProfile.putFile(ImageUri);
        uploadTask.addOnCompleteListener(EditProfileActivity.this, task -> {
            if (task.isSuccessful()) {
                uploadTask.addOnSuccessListener(taskSnapshot -> referenceForProfile.getDownloadUrl().addOnSuccessListener(uri -> {
                    profileImageUrl = String.valueOf(uri);
                    updateDatabase(profileImageUrl);
                    pd.dismiss();
                }));
            } else {
                pd.dismiss();
                Toast.makeText(EditProfileActivity.this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDatabase(String imageURL) {
        FirebaseDatabase.getInstance().getReference().child("users").child(fUser.getUid()).child("imageUrl").setValue(imageURL);
    }
}