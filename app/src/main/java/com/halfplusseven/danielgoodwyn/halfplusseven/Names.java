package com.halfplusseven.danielgoodwyn.halfplusseven;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class Names extends AppCompatActivity {

    List<String> ids = new ArrayList<String>();
    List<String> names = new ArrayList<String>();
    List<Long> DOBs = new ArrayList<Long>();

    String id;
    String name;
    Long DOB;

    NamesAdapter namesAdapter;

    int selectedItem;

    int screenSize;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        if (width<height) {
            screenSize = width;
        } else {
            screenSize = height;
        }

        final ParseUser user = ParseUser.getCurrentUser();
        final Button userButton = (Button) findViewById(R.id.user);

        if (user != null) {
            userButton.setText(capitalize(user.getString("name")));

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
            query.whereEqualTo("user", user);
            query.orderByAscending("name");
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> nameList, ParseException e) {
                    if (e == null) {
                        for (int i = 0; nameList.size() > i; i++) {
                            ids.add(i, nameList.get(i).getObjectId());
                            names.add(i, nameList.get(i).getString("name"));
                            DOBs.add(i, nameList.get(i).getDate("DOB").getTime());
                        }
                        populateNamesListView();
                    }
                }
            });
        } else {
            userButton.setText("sign in");
        }
    }

    public void populateNamesListView() {
        String[] namesArray = names.toArray(new String[names.size()]);
        if (namesArray.length < 1) {
            Intent i = new Intent(this, com.halfplusseven.danielgoodwyn.halfplusseven.Add.class);
            startActivity(i);
        }
        namesAdapter = new NamesAdapter(this, namesArray, selectedItem);
        final ListView LVNames = (ListView) findViewById(R.id.names);
        LVNames.setAdapter(namesAdapter);
        LVNames.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View v, final int i, long id) {
                v.setBackgroundColor(getResources().getColor(R.color.red));
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Person");
                query.getInBackground(ids.get(i), new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            object.deleteInBackground(new DeleteCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        names.remove(i);
                                        ids.remove(i);
                                        DOBs.remove(i);
                                        populateNamesListView();
                                        namesAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                });

                return true;
            }
        });
        LVNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView nameCell = (TextView) view.findViewById(R.id.name_cell);
                nameCell.setSelected(true);
                selectedItem = position;
                name = names.get(position);
                DOB = DOBs.get(position);
                namesAdapter.notifyDataSetChanged();
                populateNamesListView();
                ListView LVNames = (ListView) findViewById(R.id.names);
                LVNames.setSelection(position);
                LVNames.setSelectionFromTop(position, (screenSize / 2) - 70);
                Intent i = new Intent(Names.this, com.halfplusseven.danielgoodwyn.halfplusseven.Answer.class);
                i.putExtra("name", name);
                i.putExtra("DOB", DOB);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (namesAdapter != null) {
            namesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (namesAdapter != null) {
            namesAdapter.notifyDataSetChanged();
        }
    }

    public void user(View v) {
        Intent i = new Intent(this, com.halfplusseven.danielgoodwyn.halfplusseven.User.class);
        startActivity(i);
    }

    public void add(View v) {
        Intent i = new Intent(this, com.halfplusseven.danielgoodwyn.halfplusseven.Add.class);
        startActivity(i);
    }

    public String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public int rand(int a, int b) {
        return((int)((b-a+1)*Math.random() + a));
    }
}
