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

import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.type.GenerativeBackend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InsightsActivity extends AppCompatActivity {


    private TextView tvInsightsContent;
    private Button btnGenerate;
    private ProgressBar pbLoading;
    private BottomNavigationView bottomNavigationView;
    private Spinner spinnerGoals;
    private List<String> goalsList;
    private ArrayList<String> goalNames;


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

        pbLoading.setVisibility(View.GONE);

        goalsList = new ArrayList<>();
        //יוצרים רשימת טקסטים, רצים על כל היעדים של המשתמש ומוסיפים את שם היעד לרשימה. את הרשימה מעבירים לאדפטר של הספינר כדי שיציג למשתמש
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

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //כשהמשתמש לוחץ על הכפתור, בודקים איזה פריט הוא בחר בספינר
                String selectedItem = spinnerGoals.getSelectedItem().toString();

                //משנים את הטקסט, מציגים את בר הטעינה, ומכבים את הכפתור
                tvInsightsContent.setText("Generating insights, please wait..");
                pbLoading.setVisibility(View.VISIBLE);
                btnGenerate.setEnabled(false);

                if (selectedItem.equals("General Insight")) {
                    askAI("You are a financial assistant. Give me a general motivational quote about saving money, tracking expenses, and being financially responsible. Please format it nicely.");
                }
                else {
                    Goal targetGoal = null;

                    for (int i = 0; i < User.currentUser.getGoals().size(); i++) {
                        if (User.currentUser.getGoals().get(i).getGoalName().equals(selectedItem)) {
                            targetGoal = User.currentUser.getGoals().get(i);
                            break;
                        }
                    }
                    if (targetGoal != null) { //שולחים את היעד לפונקציה שבונה את הפרומפט, ואת התוצאה שולחים לAI.
                        String finalPrompt = generatePrompt(targetGoal);
                        askAI(finalPrompt);
                    }
                    else {
                        Toast.makeText(InsightsActivity.this, "failed to access goals", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
        setupNavigation();
        bottomNavigationView.setSelectedItemId(R.id.nav_insights);
    }

    private String generatePrompt(Goal selectedGoal) {

        StringBuilder sb = new StringBuilder();
        //מוסיפים לטקסט את כל נתוני היעד הכלליים
        sb.append("You are a financial assistant. Analyze my progress for my saving goal:  " + selectedGoal.getGoalName() + "\n");
        sb.append("this is the required amount of money i am aiming for: " + selectedGoal.getRequiredAmount() + "\n");
        sb.append("this is the amount of money i have right now: " + selectedGoal.getCurrentAmount() + "\n");
        sb.append("this is the currency i am using: " + User.currentUser.getCurrency() + "\n");


        //הלולאה עוברת על כל השבועות,הIF שולח לAI רק שבועות שיש בהם פעילות
        for (int i = 0; i < selectedGoal.getWeeklyTrack().size(); i++){
            WeeklyTrack week = selectedGoal.getWeeklyTrack().get(i);
            if (week.getExpenses() != 0.0 || week.getIncome() != 0.0){
                sb.append("Week " + (i+1) + ": Income: " + week.getIncome() + ", Expenses: " + week.getExpenses() + "\n");            }
        }

        //מוסיפים את ההוראות הספציפיות לAI- איך לנתח את המידע ואיך לעצב את התשובה
        sb.append("\n\n" + "Based on this weekly data, please tell me: was there a period of time (a couple of weeks or more) which was more successful then others (higher incomes), which week was the best, which was the worst, and give me a motivational quote that suits my process. Please answer in english, and in an organized format of a sub title (bigger size text), under it the relevant insight, and so on. and without any special characters like # or * please so the ui will be clean and nice. " + "\n");
        return sb.toString();
    }

private void askAI(String promptText) {

        //הגדרת המודל דרך ספריית Firebase. עוטפים אותו ב-Futures כדי לעבוד איתו בצורה אסינכרונית ב-Java.
    GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
            .generativeModel("gemini-2.5-flash-lite");
    GenerativeModelFutures model = GenerativeModelFutures.from(ai);


    //מייצר Thread נפרד כדי שהבקשה ל-AI לא תתבצע על הMain Thread שמצייר את המסך (אחרת המסך יקפא עד שהAI יענה)
    Executor executor = Executors.newSingleThreadExecutor();


    //שמים את הטקסט באובייקט קונטנט ושולחים לג'נרייט קונטנט. התשובה מגיעה כפיוצ'ר- תשובה עתידית
    Content prompt = new Content.Builder()
            .addText(promptText)
            .build();

    ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);
    Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
        @Override
        public void onSuccess(GenerateContentResponse result) {
            String resultText = result.getText();//כשהתשובה חוזרת בהצלחה מחלצים את הטקסט
            // הפונקציה מחזירה את השליטה לת'רד הראשי כדי לעדכן את המסך, להעלים את הטעינה, ולהדליק מחדש את הכפתור:
            runOnUiThread(() -> {
                tvInsightsContent.setText(resultText);
                pbLoading.setVisibility(View.GONE);
                btnGenerate.setEnabled(true);
            });
        }

        @Override
        public void onFailure(Throwable t) {
            runOnUiThread(() -> {
                Toast.makeText(InsightsActivity.this, "Failed accessing insights. Try again please", Toast.LENGTH_SHORT).show();
                pbLoading.setVisibility(View.GONE);
                btnGenerate.setEnabled(true);
            });
            Log.d("InsightsAI", Log.getStackTraceString(t));
        }
    }, executor);}

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
            else if (id == R.id.nav_timer)
            {
                startActivity(new Intent(this, TimerActivity.class));
                return true;
            }
//            else if (id == R.id.nav_notification)
//            {
//                startActivity(new Intent(this, NotificationActivity.class));
//                return true;
//            }
            return false;
        });
    }
}