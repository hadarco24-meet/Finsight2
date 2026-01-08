package com.example.finsight1;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
public class GoalViewActivity extends AppCompatActivity {

    private Goal currentGoal;
    private WeeklyTrackAdapter adapter;
    private ListView lvWeeks;
    private TextView tvTitle;
    private TextView tvTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_view);

        int goalIndex = getIntent().getIntExtra("goal_index", -1);
        if (goalIndex != -1) {
            currentGoal = User.currentUser.getGoals().get(goalIndex);
        }

        tvTitle = findViewById(R.id.tvGoalTitleName);
        tvTarget = findViewById(R.id.tvGoalTargetAmount);
        lvWeeks = findViewById(R.id.lvWeeklyTracks);

        if (currentGoal != null) {
            tvTitle.setText(currentGoal.getGoalName());
            tvTarget.setText("Target: " + currentGoal.getRequiredAmount());
            adapter = new WeeklyTrackAdapter(this, currentGoal.getWeeks());
            lvWeeks.setAdapter(adapter);

            lvWeeks.setOnItemClickListener((parent, view, position, id) -> {
                WeeklyTrack selectedWeek = currentGoal.getWeeks().get(position);
                showUpdateDialog(selectedWeek);
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

                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}