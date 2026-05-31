package com.example.finsight1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddGoalActivity extends AppCompatActivity {

    //יצירת משתנים ואובייקטים חדשים
    private EditText etGoalName, etGoalAmount, etCurrentAmount,
            etMonthsTillDue, etWorkDaysPerWeek, etMonthlyExpenses;
    private Button btnSaveGoal;
    private BottomNavigationView bottomNavigationView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);

        db = FirebaseFirestore.getInstance(); //משתמשת בפונקציה הסטטית גט אינסטנס כדי לקבל מופע יחיד של ההתקשרות לפיירסטור

        etGoalName = findViewById(R.id.etGoalName);
        etGoalAmount = findViewById(R.id.etGoalAmount);
        etCurrentAmount = findViewById(R.id.etCurrentAmount);
        etMonthsTillDue = findViewById(R.id.etMonthsTillDue);
        etWorkDaysPerWeek = findViewById(R.id.etWorkDaysPerWeek);
        etMonthlyExpenses = findViewById(R.id.etMonthlyExpenses);

        btnSaveGoal = findViewById(R.id.btnSaveGoal);

        btnSaveGoal.setOnClickListener(v -> saveGoal());

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

    private void saveGoal() {
        String name = etGoalName.getText().toString().trim(); //שולף לי את המשתנים מהET בXML, שומר אותם כסטרינג ומוריד את הרווחים מההתחלה והסוף
        String amountStr = etGoalAmount.getText().toString().trim();
        String currStr = etCurrentAmount.getText().toString().trim();
        String etMonthsTillDueStr = etMonthsTillDue.getText().toString().trim();
        String daysStr = etWorkDaysPerWeek.getText().toString().trim();
        String expStr = etMonthlyExpenses.getText().toString().trim();

        if (name.isEmpty() || amountStr.isEmpty() || currStr.isEmpty() ||
                etMonthsTillDueStr.isEmpty() || daysStr.isEmpty() || expStr.isEmpty())
        {
            Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        } //בודק אם אחד מהשדות ריק, אם כן מקפיץ טוסט

        try
        {
            double required = Double.parseDouble(amountStr);
            double current = Double.parseDouble(currStr);
            int monthsTillDue = Integer.parseInt(etMonthsTillDueStr);
            int workDays = Integer.parseInt(daysStr);
            double expenses = Double.parseDouble(expStr);

            Goal goal = new Goal(name, required, current, monthsTillDue, workDays, expenses); //יוצרת מופע חדש של המללקה GOAL עם הנתנים שהמרתי
            User.currentUser.getGoals().add(goal); //מוסיפה את היעד החדש לרשימת יעדים של המשתמש

            db.collection("users") //ניגשים לאוסף משתמשים בענן
                    .document(User.currentUser.getUsername()) //מצביעים על המסמך הספציפי
                    .set(User.currentUser) //לוקחת את כל אובייקט המשתמש ודורסת את הגרסה הישנה בגרסה החדשה
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Goal saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    }) //ירוץ רק אם האינטרנט עבד והנתונים נשמרו בהצלחה
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show()
                    ); //ירוץ אם הייתה שגיאה
        }
        catch (NumberFormatException e) //אם המשתמש הזין תווים לא חוקיים  הקוד יקפוץ לכאן בTRY במקום קריסה
        {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }
}