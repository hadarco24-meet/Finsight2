package com.example.finsight1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class GoalViewActivity extends AppCompatActivity {

    private Goal currentGoal;
    private WeeklyTrackAdapter adapter;
    private TextView tvTitle;
    private TextView tvTarget;
    private ListView lvWeeklyTrack;
    private EditText etRequiredAmount;
    private EditText etCurrentAmount;
    private EditText etMonthsTillDue;
    private EditText etWorkDaysPerWeek;
    private EditText etMonthlyExpenses;
    private Button btnSaveInfo;

    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_view);

        db = FirebaseFirestore.getInstance();

        int goalIndex = getIntent().getIntExtra("goal_index", -1);
        if (goalIndex != -1) {
            currentGoal = User.currentUser.getGoals().get(goalIndex);
        }

        tvTitle = findViewById(R.id.tvGoalTitleName);
        tvTarget = findViewById(R.id.tvGoalTargetAmount);
        lvWeeklyTrack = findViewById(R.id.lvWeeklyTrack);

        etRequiredAmount = findViewById(R.id.etRequiredAmount);
        etCurrentAmount = findViewById(R.id.etCurrentAmount);
        etMonthsTillDue = findViewById(R.id.etMonthsTillDue);
        etWorkDaysPerWeek = findViewById(R.id.etWorkDaysPerWeek);
        etMonthlyExpenses = findViewById(R.id.etMonthlyExpenses);

        btnSaveInfo = findViewById(R.id.btnSaveInfo);
        btnSaveInfo.setOnClickListener(v -> SaveInfo());

        if (currentGoal != null) {
            tvTitle.setText(currentGoal.getGoalName());
            tvTarget.setText("Target: " + currentGoal.getRequiredAmount());
            adapter = new WeeklyTrackAdapter(this, currentGoal.getWeeklyTrack());
            lvWeeklyTrack.setAdapter(adapter);

            lvWeeklyTrack.setOnItemClickListener((parent, view, position, id) -> {
                WeeklyTrack selectedWeek = currentGoal.getWeeklyTrack().get(position);
                showUpdateDialog(selectedWeek);
            });

            etRequiredAmount.setText(Double.toString(currentGoal.getRequiredAmount()));
            etCurrentAmount.setText(Double.toString(currentGoal.getCurrentAmount()));
            etMonthsTillDue.setText(Integer.toString(currentGoal.getMonthsTillDue()));
            etWorkDaysPerWeek.setText(Integer.toString(currentGoal.getWorkDaysPerWeek()));
            etMonthlyExpenses.setText(Double.toString(currentGoal.getMonthlyExpenses()));

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    return true;
                }
                else if (id == R.id.nav_profile) {
                    // startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return true;
                }
                else if (id == R.id.nav_settings) {
                    // startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

    private void showUpdateDialog(WeeklyTrack week) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_week, null);
        EditText etIncome = dialogView.findViewById(R.id.etDialogIncome);
        EditText etExpenses = dialogView.findViewById(R.id.etDialogExpenses);

        etIncome.setText(String.valueOf(week.getIncome()));
        etExpenses.setText(String.valueOf(week.getExpenses()));

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String incomeStr = etIncome.getText().toString();
                    String expensesStr = etExpenses.getText().toString();

                    if (!incomeStr.isEmpty() && !expensesStr.isEmpty()) {
                        week.setIncome(Double.parseDouble(incomeStr));
                        week.setExpenses(Double.parseDouble(expensesStr));
                        FirebaseFirestore db =  FirebaseFirestore.getInstance();
                        db.collection("users")
                                .document(User.currentUser.getUsername())
                                .set(User.currentUser)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Week updated!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
                                );
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void SaveInfo() {
        currentGoal.setRequiredAmount(Double.parseDouble(etRequiredAmount.getText().toString()));
        currentGoal.setCurrentAmount(Double.parseDouble(etCurrentAmount.getText().toString()));
        currentGoal.setMonthsTillDue(Integer.parseInt(etMonthsTillDue.getText().toString()));
        currentGoal.setWorkDaysPerWeek(Integer.parseInt(etWorkDaysPerWeek.getText().toString()));
        currentGoal.setMonthlyExpenses(Double.parseDouble(etMonthlyExpenses.getText().toString()));

        db.collection("users")
                .document(User.currentUser.getUsername())
                .set(User.currentUser)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Goal updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                });
    }
}