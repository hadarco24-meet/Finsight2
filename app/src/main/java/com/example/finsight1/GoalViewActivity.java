package com.example.finsight1;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;


public class GoalViewActivity extends AppCompatActivity {


    private ListView listGoalDetails;
    private ExtendedFloatingActionButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_view);

        listGoalDetails = findViewById(R.id.listGoalDetails);
        btnBack = findViewById(R.id.btnBack);
        Goal selectedGoal = (Goal) getIntent().getSerializableExtra("goal");

        ArrayList<String> details = new ArrayList<>();
        if (selectedGoal != null) {
            details.add("Goal Name: " + selectedGoal.getGoalName());
            details.add("Required Amount: " + selectedGoal.getRequiredAmount());
            details.add("Current Amount: " + selectedGoal.getCurrentAmount());
            details.add("Time Till Due (months): " + selectedGoal.getTimeTillDue());
            details.add("Work Days Per Week: " + selectedGoal.getWorkDaysPerWeek());
            details.add("Monthly Expenses: " + selectedGoal.getMonthlyExpenses());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                details);
        listGoalDetails.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

//שינוי מידע
        listGoalDetails.setOnItemClickListener((parent, view, position, id) -> {
            String currentValue = details.get(position);

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Edit Field");

            final android.widget.EditText input = new android.widget.EditText(this);
            input.setText(currentValue.split(": ")[1]); //לוקח לי את הערך
            builder.setView(input);

            builder.setPositiveButton("OK", (dialog, which) -> {
                String newValue = input.getText().toString();
                details.set(position, currentValue.split(": ")[0] + ": " + newValue);
                adapter.notifyDataSetChanged();


                switch (position) {  //מעדכן לי את המידע
                    case 0: selectedGoal.setGoalName(newValue); break;
                    case 1: selectedGoal.setRequiredAmount(Double.parseDouble(newValue)); break;
                    case 2: selectedGoal.setCurrentAmount(Double.parseDouble(newValue)); break;
                    case 3: selectedGoal.setTimeTillDue(Integer.parseInt(newValue)); break;
                    case 4: selectedGoal.setWorkDaysPerWeek(Integer.parseInt(newValue)); break;
                    case 5: selectedGoal.setMonthlyExpenses(Double.parseDouble(newValue)); break;
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });

    }


}
