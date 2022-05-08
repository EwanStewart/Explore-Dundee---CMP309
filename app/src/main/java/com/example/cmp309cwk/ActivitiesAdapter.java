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
        /* Get the contacts data for this position. */
        String[] contact = getItem(position);
        /* Check if an existing view is being reused, otherwise inflate the view. */
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activities_layout, parent, false);
        }
        /* Lookup views. */
        TextView display_name = (TextView) convertView.findViewById(R.id.display_name);
        TextView display_email = (TextView) convertView.findViewById(R.id.display_email);
        TextView display_phone = (TextView) convertView.findViewById(R.id.display_phone);
;

        display_name.setText("Landmarks Visited: " + contact[0]);
        display_email.setText("Total Distance Travelled: " + String.format("%.2f", Double.parseDouble(contact[1])) + "km");
        display_phone.setText("Total Time: " + contact[2]);
        return convertView;
    }
}