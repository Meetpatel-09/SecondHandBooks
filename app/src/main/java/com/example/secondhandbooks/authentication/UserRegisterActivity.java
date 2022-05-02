package com.example.secondhandbooks.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.user.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class UserRegisterActivity extends AppCompatActivity {

    private EditText fName, email, pwd, cPwd, num;
    private String sName;
    private String sEmail;
    private String sPwd;
    private String sNum;

    private FirebaseAuth auth;
    private DatabaseReference reference;

    private ProgressDialog pd;

    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        auth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        pd = new ProgressDialog(this);

        fName = findViewById(R.id.reg_name);
        email = findViewById(R.id.reg_email);
        pwd = findViewById(R.id.reg_password);
        cPwd = findViewById(R.id.reg_c_password);
        num = findViewById(R.id.reg_phone);
        Button btnReg = findViewById(R.id.btn_reg);
        TextView openLogIn = findViewById(R.id.tv_login);

        SharedPreferences sharedPreferences = this.getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (sharedPreferences.getString("isLogin", "false").equals("yes")) {
            editor.putString("isLogin", "false");
            editor.commit();
        }

        btnReg.setOnClickListener(v -> validateData());

        openLogIn.setOnClickListener(v -> {
            startActivity(new Intent(UserRegisterActivity.this, UserLogInActivity.class));
            finish();
        });
    }

    private void openMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void openCProfile() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void validateData() {
        sName = fName.getText().toString();
        sEmail = email.getText().toString();
        sPwd = pwd.getText().toString();
        String sCPwd = cPwd.getText().toString();
        sNum = num.getText().toString();

        if (sName.isEmpty()) {
            fName.setError("Required");
            fName.requestFocus();
        } else if (sEmail.isEmpty()) {
            email.setError("Required");
            email.requestFocus();
        } else if (sPwd.isEmpty()) {
            pwd.setError("Required");
            pwd.requestFocus();
        } else if (sCPwd.isEmpty()) {
            pwd.setError("Required");
            pwd.requestFocus();
        } else if (sNum.isEmpty()) {
            num.setError("Required");
            num.requestFocus();
        } else if (sNum.length() != 10) {
            num.setError("Invalid");
            num.requestFocus();
        } else {
            if (!sPwd.equals(sCPwd)) {
                cPwd.setError("Password not match");
            } else {
                createUser();
            }
        }
    }

    private void createUser() {
        pd.setMessage("Loading...");
        pd.show();
        auth.createUserWithEmailAndPassword(sEmail, sPwd).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                uploadData();
            } else {
                pd.dismiss();
                Toast.makeText(UserRegisterActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(UserRegisterActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadData() {

        HashMap<String, String> map = new HashMap<>();
        map.put("id", Objects.requireNonNull(auth.getCurrentUser()).getUid());
        map.put("name", sName);
        map.put("email", sEmail);
        map.put("phone", sNum);
        map.put("imageUrl", "default");

        reference.child("users").child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                pd.dismiss();
                Toast.makeText(UserRegisterActivity.this, "Registration Successful!!!", Toast.LENGTH_SHORT).show();
                openCProfile();
            } else {
                pd.dismiss();
                Toast.makeText(UserRegisterActivity.this, "Error : " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(UserRegisterActivity.this, "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}