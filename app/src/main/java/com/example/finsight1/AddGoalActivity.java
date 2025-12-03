package com.example.finsight1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class AddGoalActivity extends AppCompatActivity {

    private EditText etGoalName, etGoalAmount, etCurrentAmount,
            etMonths, etWorkDays, etMonthlyExpenses;
    private Button btnSaveGoal;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        db = FirebaseFirestore.getInstance();

        etGoalName = findViewById(R.id.etGoalName);
        etGoalAmount = findViewById(R.id.etGoalAmount);
        etCurrentAmount = findViewById(R.id.etCurrentAmount);
        etMonths = findViewById(R.id.etMonths);
        etWorkDays = findViewById(R.id.etWorkDays);
        etMonthlyExpenses = findViewById(R.id.etMonthlyExpenses);

        btnSaveGoal = findViewById(R.id.btnSaveGoal);

        btnSaveGoal.setOnClickListener(v -> saveGoal());
    }

    private void saveGoal() {
        String name = etGoalName.getText().toString().trim();
        String amountStr = etGoalAmount.getText().toString().trim();
        String currStr = etCurrentAmount.getText().toString().trim();
        String monthsStr = etMonths.getText().toString().trim();
        String daysStr = etWorkDays.getText().toString().trim();
        String expStr = etMonthlyExpenses.getText().toString().trim();

        if (name.isEmpty() || amountStr.isEmpty() || currStr.isEmpty() ||
                monthsStr.isEmpty() || daysStr.isEmpty() || expStr.isEmpty()) {
            Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        int required = Integer.parseInt(amountStr);
        int current = Integer.parseInt(currStr);
        int months = Integer.parseInt(monthsStr);
        int workDays = Integer.parseInt(daysStr);
        int expenses = Integer.parseInt(expStr);

        Goal goal = new Goal(name, required, current, months, workDays, expenses);

        User.currentUser.getGoals().add(goal);

        db.collection("users")
                .document(User.currentUser.getUsername())
                .set(User.currentUser)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Goal saved!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
                );
    }
}
