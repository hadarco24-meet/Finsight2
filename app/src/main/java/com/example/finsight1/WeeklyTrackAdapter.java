package com.example.finsight1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;


public class WeeklyTrackAdapter extends ArrayAdapter<WeeklyTrack> {

    private Context context;
    private List<WeeklyTrack> weeklyTracks;

    public WeeklyTrackAdapter(@NonNull Context context, List<WeeklyTrack> weeklyTracks) {
        super(context, R.layout.item_week, weeklyTracks);
        this.context = context;
        this.weeklyTracks = weeklyTracks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {{
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_week, parent, false);
        }

        WeeklyTrack currentTrack = getItem(position);

        TextView tvWeekX = convertView.findViewById(R.id.tvWeekX);
        TextView tvIncome = convertView.findViewById(R.id.tvWeeklyIncome);
        TextView tvExpenses = convertView.findViewById(R.id.tvWeeklyExpenses);

        if (currentTrack != null){
            tvWeekX.setText("Week " + (position + 1));
            tvIncome.setText("Income: " + currentTrack.getIncome());
            tvExpenses.setText("Expenses: " + currentTrack.getExpenses());
        }
        return convertView;
    }}
}
