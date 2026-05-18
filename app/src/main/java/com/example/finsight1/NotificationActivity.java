package com.example.finsight1;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Calendar;

public class NotificationActivity extends AppCompatActivity {

    // רכיבי התצוגה
    private Switch switchDefaultNotification;
    private Button btnSetCustomTime;
    private EditText etCustomMessage;
    private Spinner spinnerDay;

    //משתני זמנים, ברירת מחדל 09:00
    private int selectedHour = 9;
    private int selectedMinute = 0;

    // המרת ימים למספרים של המערכת ולשמות באנגלית
    private final int[] calendarDays = {
            Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
            Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY
    };
    private final String[] dayNames = {
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };

    // משתני שמירת נתונים
    private static final String PREFS_NAME = "NotificationPrefs"; // שם הקובץ
    private boolean isProgrammaticChange = false; // דגל למניעת שגיאות בזמן טעינה
    private int selectedDayIndex = 0; // יום ראשון כברירת מחדל

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // תצוגה על כל המסך
        setContentView(R.layout.activity_notification); // טעינת קובץ העיצוב

        // התאמת חלון האפליקציה לשוליים של הטלפון
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קישור משתנים לרכיבים במסך
        switchDefaultNotification = findViewById(R.id.switchDefaultNotification);
        btnSetCustomTime = findViewById(R.id.btnSetCustomTime);
        etCustomMessage = findViewById(R.id.etCustomMessage);
        spinnerDay = findViewById(R.id.spinnerDay);

        // טעינת הגדרות שנשמרו בעבר
        loadPreferences();

        // בקשת הרשאת התראות (חובה מאנדרואיד 13)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // מאזין לשינוי יום בספינר
        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDayIndex = position; // שמירת היום שנבחר
                if (!isProgrammaticChange && switchDefaultNotification.isChecked()) {
                    updateNotification(); // עדכון ההתראה אם הסוויץ' דלוק
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // מאזין לכיבוי והדלקה של הסוויץ'
        switchDefaultNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isProgrammaticChange) return; // מתעלם משינוי שקרה בטעינה

            if (isChecked) {
                updateNotification(); // הפעלה
                Toast.makeText(this, "Reminder enabled for " + dayNames[selectedDayIndex], Toast.LENGTH_SHORT).show();
            }
            else {
                cancelNotification(); // ביטול
                savePreferences(); // שמירת מצב 'כבוי'
                Toast.makeText(this, "Reminder disabled", Toast.LENGTH_SHORT).show();
            }
        });

        // מאזין לכפתור בחירת שעה
        btnSetCustomTime.setOnClickListener(v -> {
            // יצירת והצגת חלון שעון
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                selectedHour = hourOfDay; // שמירת השעה
                selectedMinute = minute; // שמירת הדקה
                btnSetCustomTime.setText(String.format("Time: %02d:%02d", hourOfDay, minute)); // עדכון טקסט הכפתור

                if (switchDefaultNotification.isChecked()) {
                    updateNotification(); // עדכון השעון במערכת
                }
            }, selectedHour, selectedMinute, true); // true = תצוגת 24 שעות
            timePickerDialog.show();
        });

        // ניהול תפריט ניווט תחתון
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            }
            else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            else if (id == R.id.nav_insights) {
                startActivity(new Intent(this, InsightsActivity.class));
                return true;
            }
            else if (id == R.id.nav_timer) {
                startActivity(new Intent(this, TimerActivity.class));
                return true;
            }
            else if (id == R.id.nav_notification) {
                return true;
            }
            return false;
        });

        // כפתור לבדיקה מהירה להדגמה לבוחנת
        Button btnTestNotification = findViewById(R.id.btnTestNotification);
        btnTestNotification.setOnClickListener(v -> {
            Intent testIntent = new Intent(this, NotificationReceiver.class);
            testIntent.putExtra("title", "Test Finsight");
            testIntent.putExtra("message", "It works! This is an instant test.");
            sendBroadcast(testIntent); // משדר מיד למקלט
        });
    }

    // פונקציית עזר להכנת הנתונים וקריאה להתראה
    private void updateNotification() {
        String customMsg = etCustomMessage.getText().toString().trim(); // שולף טקסט
        String selectedDayName = dayNames[selectedDayIndex];

        // טקסט דינמי או ברירת מחדל
        String finalMessage = customMsg.isEmpty()
                ? selectedDayName + " is here! Did you meet your goals this week?"
                : customMsg;

        int targetCalendarDay = calendarDays[selectedDayIndex]; // היום הנבחר למערכת

        // תזמון ההתראה
        scheduleWeeklyNotification(targetCalendarDay, selectedHour, selectedMinute, finalMessage);

        // שמירת נתונים
        savePreferences();
    }

    // תזמון ההתראה מול מערכת ההפעלה
    @android.annotation.SuppressLint("ScheduleExactAlarm") // מעלים אזהרת אבטחה בסטודיו
    private void scheduleWeeklyNotification(int dayOfWeek, int hour, int minute, String message) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); // מנהל ההתראות

        Intent intent = new Intent(this, NotificationReceiver.class); // לאן לשלוח
        intent.putExtra("title", "Weekly Review!");
        intent.putExtra("message", message);

        // אינטנט תלוי (כדי שהמערכת תפעיל אותו בעתיד)
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // הגדרת הזמן המדויק
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0); // איפוס לדיוק מקסימלי

        // אם הזמן עבר השבוע, דוחה לשבוע הבא
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.WEEK_OF_YEAR, 1);
        }

        // קביעת ההתראה
        if (alarmManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // פקודה אגרסיבית שעוקפת חיסכון בסוללה (מדויקת)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
                else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
            }
            catch (SecurityException e) {
                // גיבוי במקרה של חסימת הרשאה
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }

    // ביטול ההתראה מהמערכת
    private void cancelNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        // חובה להשתמש באותו מזהה (1) בדיוק כדי לבטל
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,
                1, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent); // פקודת הביטול
        }
    }

    // כתיבת נתונים לזיכרון המכשיר
    private void savePreferences() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit(); // פתיחה לעריכה
        editor.putBoolean("isSwitchOn", switchDefaultNotification.isChecked());
        editor.putString("savedMessage", etCustomMessage.getText().toString());
        editor.putInt("savedDayIndex", selectedDayIndex);
        editor.putInt("savedHour", selectedHour);
        editor.putInt("savedMinute", selectedMinute);
        editor.apply(); // שמירה ברקע
    }

    // קריאת נתונים מזיכרון המכשיר ועדכון התצוגה
    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE); // פתיחה לקריאה
        isProgrammaticChange = true; // חסימת הפעלות שווא בזמן טעינה

        // שליפת הנתונים (או ברירת מחדל אם ריק)
        etCustomMessage.setText(prefs.getString("savedMessage", ""));
        selectedDayIndex = prefs.getInt("savedDayIndex", 0);
        spinnerDay.setSelection(selectedDayIndex);
        selectedHour = prefs.getInt("savedHour", 9);
        selectedMinute = prefs.getInt("savedMinute", 0);
        btnSetCustomTime.setText(String.format("Time: %02d:%02d", selectedHour, selectedMinute));
        switchDefaultNotification.setChecked(prefs.getBoolean("isSwitchOn", false));

        isProgrammaticChange = false; // שחרור החסימה
    }
}