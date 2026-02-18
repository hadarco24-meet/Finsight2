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
import com.google.android.gms.tasks.OnSuccessListener;
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
    private String finalPrompt;
    private ListenableFuture<GenerateContentResponse> respons;


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

        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyD7SAC64pvvhr8l7diWnO1R87gIzQjqeqM");
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
            pbLoading.setVisibility(View.VISIBLE);
            btnGenerate.setEnabled(false);

            if(index == 0 ){
                StringBuilder sb = new StringBuilder("Analyze all my financial goals: ");
                for (int i = 0; i < User.currentUser.getGoals().size(); i++) {
                    Goal currentGoal = User.currentUser.getGoals().get(i);
                    sb.append("Goal: ").append(currentGoal.getGoalName())
                            .append(" (").append(currentGoal.getCurrentAmount())
                            .append("/").append(currentGoal.getRequiredAmount()).append("), ");
                }
                sb.append("Tell me which goal process is best and give tips and a motivational sentence.");
                finalPrompt = sb.toString();
            }
            else{
                selectedGoal = User.currentUser.getGoals().get(index - 1);
                finalPrompt = generatePrompt(selectedGoal);
            }

            Content content = new Content.Builder().addText(finalPrompt).build();
            respons = model.generateContent(content);

            Futures.addCallback(respons, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
//                    Log.d("GEMINI_DEBUG", "Success! Response: " + result.getText());
//                    tvInsightsContent.setText(result.getText());
//                    pbLoading.setVisibility(View.GONE);
//                    btnGenerate.setEnabled(true);
                    runOnUiThread(() -> {
                        try {
                            String output = result.getText();
                            tvInsightsContent.setText(output != null ? output : "No response from AI");
                        } catch (Exception e) {
                            tvInsightsContent.setText("Error parsing response");
                        }
                        pbLoading.setVisibility(View.GONE);
                        btnGenerate.setEnabled(true);
                    });
                }

                @Override
                public void onFailure(Throwable t) {
//                    Log.e("GEMINI_DEBUG", "Failed!", t);
//                    tvInsightsContent.setText("Error detail: " + t.getMessage());
//                    pbLoading.setVisibility(View.GONE);
//                    btnGenerate.setEnabled(true);
                    runOnUiThread(() -> {
                        Log.e("GEMINI_ERROR", "Failed", t);
                        tvInsightsContent.setText("Error: " + t.getMessage());
                        pbLoading.setVisibility(View.GONE);
                        btnGenerate.setEnabled(true);
                        Toast.makeText(InsightsActivity.this, "Check connection", Toast.LENGTH_SHORT).show();
                    });
                }
            }, ContextCompat.getMainExecutor(this));

           });


    }


    private String generatePrompt(Goal goal) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a financial assistant. Analyze my progress for my saving goal:  " + goal.getGoalName() + "\n");
        sb.append("this is the reqiued amount of money i am aiming for: " + goal.getRequiredAmount() + "\n");
        sb.append("this is the amount of money i have right now: " + goal.getCurrentAmount() + "\n");
        sb.append("this is the currency i am using: " + User.currentUser.getCurrency() + "\n");

        for (int i = 0; i < goal.getWeeklyTrack().size(); i++){
            WeeklyTrack week = goal.getWeeklyTrack().get(i);
            if (week.getExpenses() != 0.0 || week.getIncome() != 0.0){
                sb.append("Week " + i + ": Income: " + week.getIncome() + ", Expenses: " + week.getExpenses() + "\n");            }
        }
        sb.append("/n/n" + "Based on this weekly data, please tell me: was there a period of time (a couple of weeks or more) which was more successful then others (higher incomes), which week was the best, which was the worst, and give me a motivational quote that suits my process. Please answer in english, and in an organized format of a sub title (bigger size text), under it the relevant insight, and so on. " + "\n");
        return finalPrompt = sb.toString();
    }


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