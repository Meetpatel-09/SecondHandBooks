package com.example.secondhandbooks.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.admin.AdminHomeActivity;

public class AdminLogInActivity extends AppCompatActivity {

    private EditText etEmail, etPwd;
    private TextView show;

    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_log_in);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        SharedPreferences sharedPreferences = this.getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.getString("isLogin", "false").equals("yes")) {
            openDash();
        }

        etEmail = findViewById(R.id.email);
        etPwd = findViewById(R.id.password);

        show = findViewById(R.id.tv_show);

        RelativeLayout login = findViewById(R.id.login_btn);

        show.setOnClickListener(v -> {
            if (etPwd.getInputType() == 144) {
                etPwd.setInputType(129);
                show.setText(R.string.hide);
            } else {
                etPwd.setInputType(144);
                show.setText(R.string.show);
            }
            etPwd.setSelection(etPwd.getText().length());
        });

        login.setOnClickListener(v -> validateData());
    }

    private void validateData() {
        String email = etEmail.getText().toString();
        String pwd = etPwd.getText().toString();

        if (email.isEmpty()) {
            etEmail.setError("Please enter email");
            etEmail.requestFocus();
        } else if (pwd.isEmpty()) {
            etPwd.setError("Please enter email");
            etPwd.requestFocus();
        } else if (email.equals("admin@gmail.com") && pwd.equals("123456")) {
            editor.putString("isLogin", "yes");
            editor.commit();
            openDash();
        } else {
            Toast.makeText(this, "Please enter correct email and password.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDash() {
        startActivity(new Intent(AdminLogInActivity.this, AdminHomeActivity.class));
        finish();
    }
}