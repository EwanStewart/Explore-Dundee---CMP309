package com.example.cmp309cwk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ActivitiesAdapter extends ArrayAdapter<String[]> {

    public ActivitiesAdapter(Context context, ArrayList<String[]> contacts){
        super(context, 0, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String[] activity = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_individual_list_element, parent, false);
        }

        String distanceMetric = getContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE).getString("distanceMetric", "notFound");

        TextView txtViewLandmarks = convertView.findViewById(R.id.txtViewLandmarks);
        TextView txtViewTotalDistance = convertView.findViewById(R.id.txtViewTotalDistance);
        TextView txtViewTotalTime = convertView.findViewById(R.id.txtViewTotalTime);
;
        txtViewLandmarks.setText(activity[1]);

        if (distanceMetric.equals("0")) { //display user's choice of km or miles
            txtViewTotalDistance.setText("Total Distanced Travelled: "  + String.format("%.2f", Double.parseDouble(activity[2])) + " km");
        } else {
            double miles = Double.parseDouble(activity[2]) * 0.621371;
            miles = Math.round(miles * 100.0) / 100.0;
            txtViewTotalDistance.setText("Total Distanced Travelled: " + String.valueOf(miles) + " miles");
        }

        txtViewTotalTime.setText("Total Time: " + activity[3]);


        return convertView;
    }
}