package com.example.finsight1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;

public class InsightsActivity extends AppCompatActivity {

    private TextView tvInsightsContent;
    private Button btnGenerate;
    private ProgressBar pbLoading;
    private BottomNavigationView bottomNavigationView;
    private GenerativeModelFutures model;
    private Spinner spinnerGoals;
    private ArrayList<String> goalsList;
    private Goal selectedGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insights);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        tvInsightsContent = findViewById(R.id.tvInsightsContent);
        btnGenerate = findViewById(R.id.btnGenerate);
        pbLoading = findViewById(R.id.pbLoading);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        spinnerGoals = findViewById(R.id.spinnerGoals);
        goalsList = new ArrayList<>();
        setupNavigation();

        goalsList.add("General insights");

        for( int i = 0; i < User.currentUser.getGoals().size(); i++)
        {
            goalsList.add(User.currentUser.getGoals().get(i).getGoalName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, goalsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoals.setAdapter(adapter);

        btnGenerate.setOnClickListener(v -> {

            int index = spinnerGoals.getSelectedItemPosition();
            selectedGoal = User.currentUser.getGoals().get(index);
        });


    }


    private String generatePrompt(Goal goal){
        StringBuilder sb = new StringBuilder();



    private void setupNavigation(){
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
                return true;
            }
            return false;
        });
    }
}