package com.example.finsight1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton btnAddGoal;
    private ListView listGoals;

    private ArrayList<String> goalsTextList;
    private ArrayAdapter<String> adapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAddGoal = findViewById(R.id.btnAddGoal);
        listGoals = findViewById(R.id.listGoals);

        db = FirebaseFirestore.getInstance();

        goalsTextList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                goalsTextList);

        listGoals.setAdapter(adapter);

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
                    User.currentUser = updatedUser;

                    goalsTextList.clear();
                    for (Goal g : updatedUser.getGoals()) {
                        goalsTextList.add(g.toString());
                    }

                    adapter.notifyDataSetChanged();
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
