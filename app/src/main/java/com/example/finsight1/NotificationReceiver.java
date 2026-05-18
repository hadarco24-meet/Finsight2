package com.example.finsight1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;


public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "finsight_notifications"; //החל מאנדרואיד 8.0 חובה להגדיר מזהה יחודי בשביל הערוץ התראות

    @Override
    public void onReceive(Context context, Intent intent) {//שולפת נתונית מהמסך הקודם שנשלחו דרך האינטנט
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");

        //אם לא נשלח טקסט, נשים טקסט ברירת מחדל
        if (title == null)
            title = "Finsight";
        if (message == null)
            message = "It's time for your weekly review! Did you meet your goals?";


        // קבלת מנהל ההתראות של מערכת ההפעלה (שירות מערכת מובנה)
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // יצירת ערוץ התראות, בלעדיו מערכות הפעלה חדשות יחסמו את ההתראה ולא יציגו אותה
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,               // המזהה הדיגיטלי של הערוץ
                    "App Reminders",         // השם הויזואלי שהמשתמש יראה בהגדרות הטלפון שלו
                    NotificationManager.IMPORTANCE_HIGH // רמת דחיפות גבוהה: גורם להתראה להשמיע צליל ולצוף בראש המסך
            );

            //רישום ויצירת הערות בפועל בתוך מנהל ההתראות של המכשיר
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }

        //יוצר אינטנט רגיל שפותר את המסך הראשי
        Intent mainIntent = new Intent(context, MainActivity.class);

        // שמה את האינטנט בפנדינג אינטנט (אינטנט תלוי)
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE  //אומר שהאינטנט מאובטח ולא ניתן לשינוי לאחר יצירתו
        );

        // בניית אובייקט ההתראה
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // הגדרת האייקון הקטן שיופיע בשורת המצב למעלה
                .setContentTitle(title)                   // הגדרת כותרת ההתראה
                .setContentText(message)                  // הגדרת תוכן ההתראה
                .setPriority(NotificationCompat.PRIORITY_HIGH) //  קביעת עדיפות גבוהה למכשירים ישנים
                .setContentIntent(pendingIntent)          //  לחיצה על ההתראה תפעיל את הניווט למסך הראשי
                .setAutoCancel(true);                     // הגדרה שההתראה תימחק אוטומטית משורת ההתראות לאחר שלחצו עליה

        // פקודה סופית למערכת ההפעלה להציג את ההתראה בפועל
        if (notificationManager != null) {
            // שימוש בזמן הנוכחי במילישניות כמזהה ייחודי (ID), כדי שכל התראה חדשה
            // תתווסף בנפרד ולא תדרוס או תעלים התראות קודמות של האפליקציה.
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
