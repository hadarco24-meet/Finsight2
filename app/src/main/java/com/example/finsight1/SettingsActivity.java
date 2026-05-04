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
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = FirebaseFirestore.getInstance();

        tvNameIcon = findViewById(R.id.tvNameIcon);
        tvUsername2 = findViewById(R.id.tvUsername2);
        rbGroup = findViewById(R.id.rbGroup);
        swDarkMode = findViewById(R.id.swDarkMode);
        btnClearData = findViewById(R.id.btnClearData);
        btnLogout = findViewById(R.id.btnLogout);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //לוקחת את האות הראשונה של שם המשתמש, הופכת אותה לאות גדולה, שמה אותה בתוך עיגול
        if (User.currentUser != null) {
            String name = User.currentUser.getUsername();
            tvUsername2.setText(name);
            if (name != null && !name.isEmpty()) {
                tvNameIcon.setText(name.substring(0, 1).toUpperCase());
            }
        }

        //קוראת איזה מטבע שמור כרגע באובייקט המשתמש, ומסמנת את כפתור הרדיו המתאים בקבוצה
        String currentCurrency = User.currentUser.getCurrency();
        if (currentCurrency.equals("₪")) rbGroup.check(R.id.rbShekel);
        else if (currentCurrency.equals("$")) rbGroup.check(R.id.rbDollar);
        else if (currentCurrency.equals("€")) rbGroup.check(R.id.rbEuro);

        //בודקת מול מערכת האנדרואיד האם האפליקציה כרגע מוגדרת למצב לילה
        swDarkMode.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        //ברגע שהמשתמש לוחץ על מתג מצב לילה, משנים את ערך הבוליאן באובייקט המשתמש
        // משתמשים בפקודה של אנדרואיד שמשנה את צבעי האפליקציה באותו רגע
        // קוראים לפונקציית שמירה
        swDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            User.currentUser.setDarkMode(isChecked);

            if (isChecked)
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            saveUserToFirebase();
        });

        //מאזין לקבוצת כפתורי רדיו- מזהה איזה כפתור נלחץ, מעדכן את המחרוזת של המטבע באובייקט, ושומר לפיירבייס
        rbGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbShekel) User.currentUser.setCurrency("₪");
            else if (checkedId == R.id.rbDollar) User.currentUser.setCurrency("$");
            else if (checkedId == R.id.rbEuro) User.currentUser.setCurrency("€");

            saveUserToFirebase();
        });

        btnClearData.setOnClickListener(v -> showDeleteConfirmation());
        btnLogout.setOnClickListener(v -> logout());

        setupNavigation();
    }

    private void setupNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_settings);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home)
            {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            }
            else if (id == R.id.nav_insights)
            {
                startActivity(new Intent(this, InsightsActivity.class));
                return true;
            }
            else if (id == R.id.nav_timer)
            {
                startActivity(new Intent(this, TimerActivity.class));
                return true;
            }
//            else if (id == R.id.nav_notification)
//            {
//                startActivity(new Intent(this, NotificationActivity.class));
//                return true;
//            }
            return id == R.id.nav_settings;
        });
    }

    //מאפסים את המשתמש הנוכחי
    //מנקים את כל ההיסטוריה של המסכים באפליקציה. כדי שהמשתמש לא יוכל ללחוץ על כפתור חזור בטלפון ולהיכנס חזרה לאפליקציה כאילו הוא מחובר
    //מוחקים את המסכים מהזיכרון
    private void logout() {
        User.currentUser = null;
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    //דורסת את המסמך הישן בענן עם המידע החדש
    private void saveUserToFirebase() {
        db.collection("users")
                .document(User.currentUser.getUsername())
                .set(User.currentUser)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show());
    }

    //פותח דיאלוג אזהרה
    // אם המשתמש לוחץ כן, ניגשת לרשימת היעדים שלו ומפעילה את הפקודה קליר שמרוקנת את הרשימה
    // הנתונים הריקים נשמרים בענן
    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete all data")
                .setMessage("Are you sure you want to delete all goals?")
                .setPositiveButton("Yes, delete", (dialog, which) -> {
                    User.currentUser.getGoals().clear();
                    Toast.makeText(this, "deleted all goals", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No, cancel", null)
                .show();
    }
}