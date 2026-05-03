package com.example.finsight1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TimerActivity extends AppCompatActivity {

    private Spinner spinnerGoals;
    private EditText etHourlyWage;
    private TextView tvTimer;
    private Button btnStart;
    private Button btnPause;
    private Button btnEnd;
    private BottomNavigationView bottom_navigation;
    private List<String> goalsList;
    private ArrayList<String> goalNames;
    private int secondsPassed;
    private boolean isRunning;
    private Handler timeHandler;
    private Runnable timeRunnable;
    private int hours;
    private int mins;
    private int secs;
    private String timeString;
    private FirebaseFirestore db;
    private TextView tvHourlyWageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_timer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        spinnerGoals = findViewById(R.id.spinnerGoals);
        etHourlyWage = findViewById(R.id.etHourlyWage);
        tvTimer = findViewById(R.id.tvTimer);
        btnStart = findViewById(R.id.btnStart);
        btnPause = findViewById(R.id.btnPause);
        btnEnd = findViewById(R.id.btnEnd);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        secondsPassed = 0;
        isRunning = false;
        db = FirebaseFirestore.getInstance();

        tvHourlyWageTitle = findViewById(R.id.tvHourlyWageTitle);


        // לוקחת את סימן המטבע השמור ושותלת אותו בתוך הטקסט של הכותרת
        if (User.currentUser != null) {
            String wageText = "Hourly wage (" + User.currentUser.getCurrency() + "):";
            tvHourlyWageTitle.setText(wageText);
        }

        //רצה בלולאה על כל האובייקטים מסוג גול של המשתמש ושולפת מתוכם רק את השם לתוך רשימה חדשה
        //יוצרת אריי אדפטר שמעביר את רשימת השמות לתוך ספינר
        goalsList = new ArrayList<>();
        if (User.currentUser != null && User.currentUser.getGoals() != null) {
            for( int i = 0; i < User.currentUser.getGoals().size(); i++) {
                goalsList.add(User.currentUser.getGoals().get(i).getGoalName());
            }
        }
        else {
            Toast.makeText(this, "User not connected/ no goals available", Toast.LENGTH_SHORT).show();
        }
        goalNames = new ArrayList<>(goalsList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, goalsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoals.setAdapter(adapter);

        //מגדירה את המשימה שתרוץ כל שנייה
        timeHandler = new Handler();
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                hours = secondsPassed/3600;
                mins = (secondsPassed % 3600) / 60;
                secs = secondsPassed % 60;

                if (isRunning)
                    secondsPassed++;

                timeString = String.format("%02d:%02d:%02d", hours, mins, secs);
                tvTimer.setText(timeString);

                timeHandler.postDelayed(this, 1000);//גורמת לראנבל לקרוא לעצמו שוב בעוד שניה
            }
        };



        timeHandler.post(timeRunnable);//לוקחת את הראנבל וזורקת להנדלר כדי שיתחיל לרוץ






        btnStart.setOnClickListener(v -> {
            isRunning = true;
        });

        btnPause.setOnClickListener(v -> {
            isRunning = false;
        });

        btnEnd.setOnClickListener(v -> {
            isRunning = false;
            String wageStr = etHourlyWage.getText().toString().trim();
            if (!wageStr.isEmpty()){
                if (User.currentUser.getGoals() == null || User.currentUser.getGoals().isEmpty()) {
                    Toast.makeText(this, "No goals found. Please add a goal first.", Toast.LENGTH_SHORT).show();
                    return;
                }

                double hourlyWage = Double.parseDouble(wageStr);
                double exactHours = secondsPassed / 3600.0;
                double earnedMoney = hourlyWage * exactHours;

                //שולפים איזה יעד נבחר בספינר
                //מחשבים את הפרש הזמן בין הרגע הנוכחי לבין תאריך היצירה של היעד
                // מחלקים את ההפרש במספר המילישניות בשבוע שלם, התוצאה- האינדקס המדויק של השבוע הנוכחי במערך השבועות
                int selectedPosition = spinnerGoals.getSelectedItemPosition();

                Goal selectedGoal = User.currentUser.getGoals().get(selectedPosition);
                long timeDiff = System.currentTimeMillis()- selectedGoal.getStartDate();
                int currentWeekIndex = (int) (timeDiff / (1000L * 60 * 60 * 24 * 7));

                //מושכים את השבוע הנכון, מעדכנים לו את ההכנסה, ומעדכנים את הסכום הכללי של היעד
                //דורסים את המידע הישן בענן עם המידע המעודכן, מאפסים את הטיימר, סוגרים את המסך
                if (currentWeekIndex < selectedGoal.getWeeklyTrack().size()){
                    WeeklyTrack currentWeek = selectedGoal.getWeeklyTrack().get(currentWeekIndex);
                    currentWeek.setIncome(currentWeek.getIncome()+earnedMoney);
                    selectedGoal.setCurrentAmount(selectedGoal.getCurrentAmount() + earnedMoney);

                    db.collection("users")
                            .document(User.currentUser.getUsername())
                            .set(User.currentUser)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Goal saved!", Toast.LENGTH_SHORT).show();
                                secondsPassed = 0;
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
                            });
                }
            }
            else
                Toast.makeText(TimerActivity.this, "enter hourly wage please", Toast.LENGTH_SHORT).show();
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
            else if (id == R.id.nav_timer)
            {
                return true;
            }
            return false;
        });

    }
}