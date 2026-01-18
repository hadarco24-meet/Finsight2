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
        TextView tvProgress = convertView.findViewById(R.id.tvItemGoalDetails);
        ProgressBar pb = convertView.findViewById(R.id.pbGoalProgress);

        tvName.setText(goal.getGoalName());
        tvProgress.setText("required: " + goal.getRequiredAmount() + " current: " + goal.getCurrentAmount());

        int progress = (int) ((goal.getCurrentAmount() / goal.getRequiredAmount()) * 100);
        pb.setProgress(progress);

        return convertView;
    }
}
