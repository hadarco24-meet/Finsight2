package com.example.finsight1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvNameIcon, tvUsername2;
    private RadioGroup rbGroup;
    private SwitchCompat swDarkMode;
    private Button btnClearData, btnLogout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = FirebaseFirestore.getInstance();

        tvNameIcon = findViewById(R.id.tvNameIcon);
        tvUsername2 = findViewById(R.id.tvUsername2);

        if (User.currentUser != null) {
            String name = User.currentUser.getUsername();
            tvUsername2.setText(name);
            String letter = name.substring(0, 1).toUpperCase();
            tvNameIcon.setText(letter);
        }

        rbGroup = findViewById(R.id.rbGroup);
        rbGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbShekel)
                {
                    User.currentUser.setCurrency("₪");
                }
                else if (checkedId == R.id.rbDollar)
                {
                    User.currentUser.setCurrency("$");
                }
                else if (checkedId == R.id.rbEuro)
                {
                    User.currentUser.setCurrency("€");
                }
                saveUserToFirebase();
            }
        });

        swDarkMode = findViewById(R.id.swDarkMode);
        swDarkMode.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        swDarkMode.setOnCheckedChangeListener((v, isChecked) -> {
            User.currentUser.setDarkMode(isChecked);
            saveUserToFirebase();

            if (isChecked) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        });

        btnClearData = findViewById(R.id.btnClearData);
        btnClearData.setOnClickListener(v -> showDeleteConfirmation());

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            User.currentUser = null;
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home)
            {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            }
            else if (id == R.id.nav_settings)
            {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            else if (id == R.id.nav_insights)
            {
                startActivity(new Intent(this, InsightsActivity.class));
                return true;
            }
            return false;
        });
    }

    private void saveUserToFirebase() {
        db.collection("users")
                .document(User.currentUser.getUsername())
                .set(User.currentUser)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show());
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete all data")
                .setMessage("Are you sure you want to delete all goals?")
                .setPositiveButton("Yes, delete", (dialog, which) -> {
                    User.currentUser.getGoals().clear();
                    saveUserToFirebase();
                    Toast.makeText(this, "deleted all goals", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No, cancel", null)
                .show();
    }
}