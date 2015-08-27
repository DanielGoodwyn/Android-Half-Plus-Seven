package com.halfplusseven.danielgoodwyn.halfplusseven;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class NamesOnLongClick implements View.OnLongClickListener {
    private Context context;
    private int selectedItem = 0;
    private final String[] ids;

    public NamesOnLongClick(Context context, String[] ids, int selectedItem) {
        this.context = context;
        this.selectedItem = selectedItem;
        this.ids = ids;
    }

    @Override
    public boolean onLongClick(View v) {
        String id = ids[selectedItem];
        Toast.makeText(context, "Long Clicked " + id, Toast.LENGTH_SHORT).show();
        return true;
    }
}
