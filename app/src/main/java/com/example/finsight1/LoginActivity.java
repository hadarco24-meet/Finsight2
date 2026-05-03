package com.example.finsight1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsernameLogin, etPasswordLogin;
    private Button btnLogin, btnBack;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        etUsernameLogin = findViewById(R.id.etUsernameLogin);
        etPasswordLogin = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
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

    private void loginUser() {
        String username = etUsernameLogin.getText().toString().trim();
        String password = etPasswordLogin.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(username)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User loggedUser = documentSnapshot.toObject(User.class);//לוקחת את המידע הזה, ובאופן אוטומטי בונה ממנו אובייקט מהמחלקה יוזר שיצרתי

                        //בודקים מקומית אם הסיסמה ששמורה בענן תואמת לסיסמה שהמשתמש הקליד
                        if (loggedUser != null && loggedUser.getPassword().equals(password)) {
                            User.currentUser = loggedUser;// שומרת את האובייקט שחזר מהשרת לתוך משתנה סטטי

                            Toast.makeText(this, "successfully logged in", Toast.LENGTH_SHORT).show();

                            //אם הכל תקין, יוצרים אינטנט שמעביר אותנו לדף הראשי
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(this, "username or password incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "user does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(err -> {
                    Toast.makeText(this, "failed to access Firebase", Toast.LENGTH_SHORT).show();
                });
    }
}