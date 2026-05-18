package com.example.finsight1;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class NotificationActivity extends AppCompatActivity {

    private Switch switchDefaultNotification;
    private Button btnSetCustomTime;
    private EditText etCustomMessage;
    private Spinner spinnerDay;
    private int selectedHour = 9; //שעת ברירת המחדל היא 09:00
    private int selectedMinute = 0;

    // מערך הממפה את המיקום בספינר
    private final int[] calendarDays = {
            Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
            Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY
    };
    private final String[] dayNames = {
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };

    private int selectedDayIndex = 0; // ברירת מחדל ראשון

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        switchDefaultNotification = findViewById(R.id.switchDefaultNotification);
        btnSetCustomTime = findViewById(R.id.btnSetCustomTime);
        etCustomMessage = findViewById(R.id.etCustomMessage);
        spinnerDay = findViewById(R.id.spinnerDay);

        // טיפול בהרשאות בזמן ריצה, חובה החל מאנדרואיד 13 (API 33)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // בדיקה האם למשתמש אין עדיין הרשאה להציג התראות
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // הקפצת חלונית מערכת המבקשת מהמשתמש לאשר קבלת התראות מהאפליקציה
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDayIndex = position; // שומר את היום החדש שנבחר
                if (switchDefaultNotification.isChecked()) {
                    updateNotification(); // מעדכן את ההתראה בזמן אמת
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        // האזנה לשינוי מצב הסוויצ
        switchDefaultNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                updateNotification();
                Toast.makeText(this, "Reminder enabled for " + dayNames[selectedDayIndex], Toast.LENGTH_SHORT).show();
            }
            else {
                cancelNotification();
                Toast.makeText(this, "Reminder disabled", Toast.LENGTH_SHORT).show();
            }
        });

        btnSetCustomTime.setOnClickListener(v -> {
            // יצירת רכיב שעון קופץ
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {

                selectedHour = hourOfDay;
                selectedMinute = minute;

                btnSetCustomTime.setText(String.format("Time: %02d:%02d", hourOfDay, minute));

                // במידה הסוויצ כבר דלוק, נעדכן מיידית את השעון במערכת לזמן החדש
                if (switchDefaultNotification.isChecked()) {
                    String customMsg = etCustomMessage.getText().toString().trim();
                    String finalMessage = customMsg.isEmpty() ? "Sunday is here! Did you meet your goals this week?" : customMsg;
                    scheduleWeeklyNotification(Calendar.SUNDAY, selectedHour, selectedMinute, finalMessage);
                }
            }, selectedHour, selectedMinute, true); //24 שעות במקום AM/PM

            timePickerDialog.show(); //  המציגה את דיאלוג השעון על המסך
        });


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
                return true;
            }
            return false;
        });
    }


    //  בונה את ההודעה ומזמנת את האלארם (כדי לא לשכפל קוד)
    private void updateNotification() {
        String customMsg = etCustomMessage.getText().toString().trim();
        String selectedDayName = dayNames[selectedDayIndex];

        // אם המשתמש לא הקליד כלום נשתמש ביום שהוא בחר מתוך המערך
        String finalMessage = customMsg.isEmpty()
                ? selectedDayName + " is here! Did you meet your goals this week?"
                : customMsg;

        int targetCalendarDay = calendarDays[selectedDayIndex];

        scheduleWeeklyNotification(targetCalendarDay, selectedHour, selectedMinute, finalMessage);


    }

    // מבצעת את הרישום הפיזי של ההתראה החוזרת באלארם מנגר של אנדרואיד
    private void scheduleWeeklyNotification(int dayOfWeek, int hour, int minute, String message) {
        // גישה לשירות המערכת הגלובלי של אנדרואיד שאחראי על שעונים מעוררים ותזמונים
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // יצירת אינטנט שמגדיר מי הרכיב שצריך להתעורר כשהזמן יגיע
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("title", "Weekly Review!");
        intent.putExtra("message", message);

        // יצירת אינטנט תלוי עם מזהה קבוע 1 (מאפשר לנו לגשת, לעדכן או לבטל את אותה התראה)
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                1, // איידי של האינטנט הספציפי הזה
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // שימוש במחלקת קלנדר כדי לחשב את ראשון הקרוב בשעה ובדקה המבוקשות במילישניות
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek); // קביעת היום בשבוע
        calendar.set(Calendar.HOUR_OF_DAY, hour);       // קביעת השעה
        calendar.set(Calendar.MINUTE, minute);         // קביעת הדקה
        calendar.set(Calendar.SECOND, 0);              // איפוס השניות ל0

        //  אם הזמן שחושב כבר עבר השבוע
        if (calendar.before(Calendar.getInstance())) {
            // נוסיף שבוע קדימה, שההתראה לא תקפוץ ברגע הרישום
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }

        // ביצוע הרישום בפועל באלארם מנגר של הטלפון
        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7,
                    pendingIntent
            );
        }
    }

    // מבטלת ומוחקת אל האלארם מהמערכת
    private void cancelNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);

        // כדי לבטל ניצור אינטנט תלוי זהה לזה שיצרנו בעת הרישום
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // ביטול האלארם מהמנהל של המכשיר
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
