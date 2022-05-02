
package com.example.secondhandbooks.admin.category;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.admin.AdminHomeActivity;
import com.google.firebase.database.FirebaseDatabase;

public class AddCategoryActivity extends AppCompatActivity {

    private EditText etCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        etCat = findViewById(R.id.cat_name);
        Button btnCat = findViewById(R.id.btn_add_cat);

        btnCat.setOnClickListener(v -> {
            String category = etCat.getText().toString();

            if (category.isEmpty()) {
                etCat.setError("Please Enter Category");
                etCat.requestFocus();
            } else {
                FirebaseDatabase.getInstance().getReference().child("category").child(category).setValue(true).addOnSuccessListener(unused -> {
                    Toast.makeText(AddCategoryActivity.this, "Category Add Successfully", Toast.LENGTH_SHORT).show();
                    etCat.setText("");
                    startActivity(new Intent(AddCategoryActivity.this, AdminHomeActivity.class));
                }).addOnFailureListener(e -> Toast.makeText(AddCategoryActivity.this, e.toString(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}