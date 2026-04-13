package com.example.finsight1;

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

        goalsList = new ArrayList<>();
        goalsList.add("General Insight");
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

                timeHandler.postDelayed(this, 1000);
            }
        };
        timeHandler.post(timeRunnable);//לוקחת את הראנבל וזורקת להנדלר כדי שיתחיל לרוץ


        btnStart.setOnClickListener(R.id.btnStart);

    }
}