package com.example.finsight1;

import android.content.Intent;
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

        db = FirebaseFirestore.getInstance(); // יצירת החיבור למסד הנתונים בענן

        int goalIndex = getIntent().getIntExtra("goal_index", -1);
        if (goalIndex != -1) {
            currentGoal = User.currentUser.getGoals().get(goalIndex);
        }//הMAIN שולח לדף הזה את מיקום היעד ברשימה דרך האינטנט, אם האינדקס תקין, שולפים את היעד מתוך המשתמש הנוכחי ושומרים

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

        if (currentGoal != null) { //אנחנו בודקים שהיעד נטען בהצלחה. אם כן, כותבים שם מטרה וסכום- כולל מטבע, בתיבות הטקסט
            tvTitle.setText(currentGoal.getGoalName());
            tvTarget.setText("Target: " + String.format("%.2f", currentGoal.getRequiredAmount()) + User.currentUser.getCurrency());
            adapter = new WeeklyTrackAdapter(this, currentGoal.getWeeklyTrack()); //מחברים את הLV לנתונים
            lvWeeklyTrack.setAdapter(adapter);

            lvWeeklyTrack.setOnItemClickListener((parent, view, position, id) -> {
                WeeklyTrack selectedWeek = currentGoal.getWeeklyTrack().get(position);
                showUpdateDialog(selectedWeek);
            });//המשתמש לוחץ על שורה ברשימה, שולפים את השבוע הספציפי לפי מיקום, ושולחים אותו לפונקציה שתפתח דיאלוג לעריכה

            etRequiredAmount.setText(String.format("%.2f", currentGoal.getRequiredAmount()));
            etCurrentAmount.setText(String.format("%.2f", currentGoal.getCurrentAmount()));
            etMonthsTillDue.setText(Integer.toString(currentGoal.getMonthsTillDue()));
            etWorkDaysPerWeek.setText(Integer.toString(currentGoal.getWorkDaysPerWeek()));
            etMonthlyExpenses.setText(Double.toString(currentGoal.getMonthlyExpenses()));

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
                    startActivity(new Intent(this, InsightsActivity.class));
                    return true;
                }
                else if (id == R.id.nav_timer)
                {
                    startActivity(new Intent(this, TimerActivity.class));
                    return true;
                }
                else if (id == R.id.nav_notification)
                {
                    startActivity(new Intent(this, NotificationActivity.class));
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
                    String incomeStr = etIncome.getText().toString().trim();
                    String expensesStr = etExpenses.getText().toString().trim();

                    if (!incomeStr.isEmpty() && !expensesStr.isEmpty()) {
                        try{
                            //מלאים את תיבות הטקסט בנתונים הנוכחיים של השבוע:
                            week.setIncome(Double.parseDouble(incomeStr));
                            week.setExpenses(Double.parseDouble(expensesStr));
                            FirebaseFirestore db =  FirebaseFirestore.getInstance();
                            db.collection("users")
                                    .document(User.currentUser.getUsername())
                                    .set(User.currentUser)
                                    .addOnSuccessListener(unused -> {
                                        Toast.makeText(GoalViewActivity.this, "Week updated!", Toast.LENGTH_SHORT).show();
                                        adapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(GoalViewActivity.this, "Failed to save", Toast.LENGTH_SHORT).show()                                    );
                            adapter.notifyDataSetChanged(); // מודיעה לאדפטר של הרשימה שהנתונים השתנו, ושצריך לשנות את המסך כדי שיראו את העדכון
                        }
                        catch (NumberFormatException e){
                            Toast.makeText(GoalViewActivity.this, "Failed to save", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void SaveInfo() { //הכל בטריי קאץ' כדי שאם המשתמש הזין טקסט במקום מספר התוכנה לא תקרוס, תציג הודעת שגיאה
        try
        {
            String requiredStr = etRequiredAmount.getText().toString().trim();
            String currentStr = etCurrentAmount.getText().toString().trim();
            String monthsStr = etMonthsTillDue.getText().toString().trim();
            String daysStr = etWorkDaysPerWeek.getText().toString().trim();
            String expensesStr = etMonthlyExpenses.getText().toString().trim();

            if (requiredStr.isEmpty() || currentStr.isEmpty() || monthsStr.isEmpty() ||
                    daysStr.isEmpty() || expensesStr.isEmpty())
            {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            currentGoal.setRequiredAmount(Double.parseDouble(requiredStr));
            currentGoal.setCurrentAmount(Double.parseDouble(currentStr));
            currentGoal.setMonthsTillDue(Integer.parseInt(monthsStr));
            currentGoal.setWorkDaysPerWeek(Integer.parseInt(daysStr));
            currentGoal.setMonthlyExpenses(Double.parseDouble(expensesStr));

            db.collection("users") //ניגשים לאוסף משתמשים בענן
                    .document(User.currentUser.getUsername())//מצביעים על המסמך הספציפי
                    .set(User.currentUser)//לוקחת את כל אובייקט המשתמש ודורסת את הגרסה הישנה בגרסה החדשה
                    .addOnSuccessListener(unused ->
                    {
                        Toast.makeText(this, "Information updated successfully", Toast.LENGTH_SHORT).show();
                        tvTarget.setText("Target: " + String.format("%.2f", currentGoal.getRequiredAmount()) + User.currentUser.getCurrency());
                    })//ירוץ רק אם האינטרנט עבד והנתונים נשמרו בהצלחה
                    .addOnFailureListener(e ->
                    {
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                    });//ירוץ אם הייתה שגיאה

        }
        catch (Exception e)//אם המשתמש הזין תווים לא חוקיים  הקוד יקפוץ לכאן בTRY במקום קריסה
        {
            Toast.makeText(this, "Please enter numbers only", Toast.LENGTH_SHORT).show();
        }
    }
}