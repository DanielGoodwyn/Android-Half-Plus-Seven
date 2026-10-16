package com.halfplusseven.danielgoodwyn.halfplusseven;

import com.danielgoodwyn.halfplussevenapp.R;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;

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

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final Button userButton = (Button) findViewById(R.id.user);

        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(user.getUid()).addSnapshotListener((snapshot, e) -> {
                if (e != null || snapshot == null) return;
                if (snapshot.exists()) {
                    String usersName = snapshot.getString("name");
                    if (usersName != null) {
                        userButton.setText(capitalize(usersName));
                    }
                }
            });

            Map<String, DocumentSnapshot> allPersons = new HashMap<>();

            EventListener<QuerySnapshot> listener = (value, error) -> {
                if (error != null || value == null) return;
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.REMOVED) {
                        allPersons.remove(dc.getDocument().getId());
                    } else {
                        allPersons.put(dc.getDocument().getId(), dc.getDocument());
                    }
                }

                List<DocumentSnapshot> sortedDocs = new ArrayList<>(allPersons.values());
                Collections.sort(sortedDocs, (d1, d2) -> {
                    String n1 = d1.getString("name");
                    String n2 = d2.getString("name");
                    if (n1 == null) n1 = "";
                    if (n2 == null) n2 = "";
                    return n1.compareToIgnoreCase(n2);
                });

                ids.clear(); names.clear(); DOBs.clear();
                for (int i = 0; i < sortedDocs.size(); i++) {
                    DocumentSnapshot doc = sortedDocs.get(i);
                    ids.add(i, doc.getId());
                    names.add(i, doc.getString("name"));
                    if (doc.getDate("dob") != null) {
                        DOBs.add(i, doc.getDate("dob").getTime());
                    } else {
                        DOBs.add(i, 0L);
                    }
                }
                populateNamesListView();
            };

            db.collection("persons").whereEqualTo("user", user.getUid()).addSnapshotListener(listener);
            db.collection("persons").whereEqualTo("userId", user.getUid()).addSnapshotListener(listener);
        } else {
            userButton.setText("sign in");
        }
    }

    public void populateNamesListView() {
        String[] namesArray = names.toArray(new String[names.size()]);
        namesAdapter = new NamesAdapter(this, namesArray, selectedItem);
        final ListView LVNames = (ListView) findViewById(R.id.names);
        LVNames.setAdapter(namesAdapter);
        LVNames.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View v, final int i, long id) {
                v.setBackgroundColor(getResources().getColor(R.color.red));
                FirebaseFirestore.getInstance().collection("persons").document(ids.get(i)).delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        names.remove(i);
                        ids.remove(i);
                        DOBs.remove(i);
                        populateNamesListView();
                        namesAdapter.notifyDataSetChanged();
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
