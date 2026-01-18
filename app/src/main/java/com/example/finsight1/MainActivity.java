package com.example.finsight1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton btnAddGoal;
    private ListView listGoals;
    private GoalAdapter goalAdapter;
    private ArrayList<Goal> goalsList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        btnAddGoal = findViewById(R.id.btnAddGoal);
        listGoals = findViewById(R.id.listGoals);

        goalsList = new ArrayList<>();
        goalAdapter = new GoalAdapter(this, goalsList);
        listGoals.setAdapter(goalAdapter);

        listGoals.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, GoalViewActivity.class);
            intent.putExtra("goal_index", position);
            startActivity(intent);
        });

        btnAddGoal.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AddGoalActivity.class);
            startActivity(i);
        });

        loadGoalsFromFirebase();
    }

    private void loadGoalsFromFirebase() {
        if (User.currentUser == null) {
            Intent i = new Intent(MainActivity.this, SignOrLogActivity.class);
            startActivity(i);
            finish();
            return;
        }

        db.collection("users")
                .document(User.currentUser.getUsername())
                .get()
                .addOnSuccessListener(doc -> {
                    User updatedUser = doc.toObject(User.class);
                    if (updatedUser != null)
                    {
                        User.currentUser = updatedUser;

                        goalsList.clear();
                        if (updatedUser.getGoals() != null)
                        {
                            goalsList.addAll(updatedUser.getGoals());
                        }

                        goalAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(err ->
                        System.out.println("Failed loading goals")
                );
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGoalsFromFirebase();
    }
}