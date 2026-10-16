package com.halfplusseven.danielgoodwyn.halfplusseven;

import com.danielgoodwyn.halfplussevenapp.R;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class Answer extends AppCompatActivity {

    long now = System.currentTimeMillis();
    String name;
    Date DOB;
    long time;
    double age;

    String userName;
    Date userDOB;
    long userTime;
    double userAge;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_answer);
        b = getIntent().getExtras();
        name = b.getString("name");
        time = b.getLong("DOB");
        DOB = new Date(time);
        age = ((time-now)*-1)/365.25/24/60/60/1000;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(user.getUid()).addSnapshotListener((snapshot, e) -> {
                if (e != null || snapshot == null) return;
                if (snapshot.exists()) {
                    userName = snapshot.getString("name");
                    userDOB = snapshot.getDate("dob");
                    if (userDOB != null) {
                        userTime = userDOB.getTime();
                        userAge = ((userTime-now)*-1)/365.25/24/60/60/1000;

                        double userLowerRange = (userAge/2)+7;
                        double userUpperRange = (userAge-7)*2;

                        TextView answer = (TextView) findViewById(R.id.answer);
                        TextView nameView = (TextView) findViewById(R.id.name);
                        TextView emoji = (TextView) findViewById(R.id.emoji);

                        if (userAge < 14 || age < 14) {
                            answer.setText("⛔ ");
                            nameView.setText("Nobody under 14 please...");
                            emoji.setText("👶👎");
                        } else if (age > userLowerRange && age < userUpperRange) {
                            answer.setText("YES");
                            nameView.setText("to " + name);
                            emoji.setText("😍👍");
                        } else if (age < userLowerRange) {
                            answer.setText("NO");
                            nameView.setText(name + " is too young for you.");
                            emoji.setText("😖👎");
                        } else if (age > userUpperRange) {
                            answer.setText("NO");
                            nameView.setText(name + " is too old for you.");
                            emoji.setText("😖👎");
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(this, Names.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void done(View view) {
        Intent i = new Intent(this, Names.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void next(View view) {
        Intent i = new Intent(this, com.halfplusseven.danielgoodwyn.halfplusseven.Results.class);
        i.putExtra("name", name);
        i.putExtra("time", time);
        i.putExtra("userName", userName);
        i.putExtra("userTime", userTime);
        startActivity(i);
    }
}
