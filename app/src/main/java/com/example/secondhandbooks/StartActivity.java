package com.example.secondhandbooks;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.secondhandbooks.authentication.AdminLogInActivity;
import com.example.secondhandbooks.authentication.UserLogInActivity;
import com.example.secondhandbooks.authentication.UserRegisterActivity;
import com.example.secondhandbooks.user.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Button btnUserReg = findViewById(R.id.btn_user_reg);
        Button btnUserLogIn = findViewById(R.id.btn_user_login);
        Button btnAdminLogIn = findViewById(R.id.btn_admin_login);

        btnUserReg.setOnClickListener(this);
        btnUserLogIn.setOnClickListener(this);
        btnAdminLogIn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_user_reg:
                startActivity(new Intent(this, UserRegisterActivity.class));
                break;

            case R.id.btn_user_login:
                startActivity(new Intent(this, UserLogInActivity.class));
                break;

            case R.id.btn_admin_login:
                startActivity(new Intent(this, AdminLogInActivity.class));
                break;
        }
    }
}