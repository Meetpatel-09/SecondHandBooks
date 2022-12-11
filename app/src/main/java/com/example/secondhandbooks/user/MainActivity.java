package com.example.secondhandbooks.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.secondhandbooks.R;
import com.example.secondhandbooks.user.ui.category.CategoryFragment;
import com.example.secondhandbooks.user.ui.chat.ChatFragment;
import com.example.secondhandbooks.user.ui.home.HomeFragment;
import com.example.secondhandbooks.user.ui.profile.ProfileFragment;
import com.example.secondhandbooks.user.ui.upload.UploadActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private Fragment selectorFragment;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {

                case R.id.navigation_home :
                    selectorFragment = new HomeFragment();
                    break;

                case R.id.navigation_category :
                    selectorFragment = new CategoryFragment();
                    break;

                case R.id.navigation_upload :
                    selectorFragment = null;
                    startActivity(new Intent(MainActivity.this, UploadActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    break;

                case R.id.navigation_chat :
                    selectorFragment = new ChatFragment();
                    break;

                case R.id.navigation_profile :
                    selectorFragment = new ProfileFragment();
                    break;
            }

            if (selectorFragment != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
            }

            return true;
        });

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }
}