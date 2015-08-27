package com.halfplusseven.danielgoodwyn.halfplusseven;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NamesAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] names;
    private final int selectedItem;

    public NamesAdapter(Context context, String[] names, int selectedItem) {
        super(context, R.layout.name_cell, names);
        this.context = context;
        this.names = names;
        this.selectedItem = selectedItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.name_cell, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.name_cell);
        textView.setText(names[position]);
        if ( (position & 1) == 0 ) {
            //even
            rowView.setBackgroundColor(getContext().getResources().getColor(R.color.darkBlue));
        } else {
            //odd
            rowView.setBackgroundColor(getContext().getResources().getColor(R.color.lightBlue));
        }
        return rowView;
    }
}
