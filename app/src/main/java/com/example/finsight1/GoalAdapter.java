package com.example.finsight1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class GoalAdapter extends ArrayAdapter<Goal> {
    public GoalAdapter(Context context, List<Goal> goals) {
        super(context, 0, goals);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_goal, parent, false);
        }

        Goal goal = getItem(position);

        TextView tvName = convertView.findViewById(R.id.tvItemGoalName);
        TextView tvRequired = convertView.findViewById(R.id.tvRequired);
        TextView tvCurrent = convertView.findViewById(R.id.tvCurrent);
        TextView tvGoalPercentage = convertView.findViewById(R.id.tvGoalPercentage);

        ProgressBar pb = convertView.findViewById(R.id.pbGoalProgress);
        //עד לפה נכתב בעזרת AI (מתחילת האוברייד)

        tvName.setText(goal.getGoalName());
        tvRequired.setText("required: " + goal.getRequiredAmount() + User.currentUser.getCurrency());
        tvCurrent.setText("current: " + goal.getCurrentAmount() + User.currentUser.getCurrency());

        int progress = (int) ((goal.getCurrentAmount() / goal.getRequiredAmount()) * 100.0);//מחלקים סכום רצוי בנוכחי, מכפילים ב100 וממירים לINT
        pb.setProgress(progress); //אומרים לבר להתמלא עד אותו אחוז
        tvGoalPercentage.setText(progress + "%"); //מציגים סימן אחוזים

        return convertView;
    }
}
