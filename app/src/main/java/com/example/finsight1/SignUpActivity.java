package com.example.finsight1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private EditText etUsernameSignup, etPasswordSignup;
    private Button btnSignup, btnBack;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        etUsernameSignup = findViewById(R.id.etUsernameSignup);
        etPasswordSignup = findViewById(R.id.etPasswordSignup);
        btnSignup = findViewById(R.id.btnSignup);
        btnBack = findViewById(R.id.btnBack);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });

        btnBack.setOnClickListener(view -> {
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void createUser() {
        String username = etUsernameSignup.getText().toString().trim();
        String password = etPasswordSignup.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        Toast.makeText(this, "this username is already taken", Toast.LENGTH_SHORT).show();
                    } else {
                        User newUser = new User(username, password);
                        db.collection("users")
                                .add(newUser)
                                .addOnSuccessListener(doc -> {
                                    Toast.makeText(this, "new user created", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                })
                                .addOnFailureListener(err -> {
                                    Toast.makeText(this, "error, could not create user", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(err -> {
                    Toast.makeText(this, "failed to access Firebase", Toast.LENGTH_SHORT).show();
                });
    }
}
