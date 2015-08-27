package com.halfplusseven.danielgoodwyn.halfplusseven;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class Results extends AppCompatActivity {

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
        setContentView(R.layout.activity_results);
        b = getIntent().getExtras();

        name = b.getString("name");
        time = b.getLong("time");
        DOB = new Date(time);
        age = ((time-now)*-1)/365.25/24/60/60/1000;
        double lowerRange = (age/2)+7;
        double upperRange = (age-7)*2;

        userName = b.getString("userName");
        userTime = b.getLong("userTime");
        userDOB = new Date(userTime);
        userAge = ((userTime-now)*-1)/365.25/24/60/60/1000;
        double userLowerRange = (userAge/2)+7;
        double userUpperRange = (userAge-7)*2;

        String text = capitalize(userName) + ", " + round(userAge, 2) + " years old";
        text = text + "\nrange: " + round(userLowerRange, 2) + " - " + round(userUpperRange, 2);
        text = text + "\n\n" + capitalize(name) + ", " + round(age, 2) + " years old";
        text = text + "\nrange: " + round(lowerRange, 2) + " - " + round(upperRange, 2);

        if (age < 14 || userAge < 14) {
            text = "Nobody under 14 please...";
        } else {
            text = text + "\n\n";
            if (userAge > (age - .1) && userAge < (age + .1)) {
                text = text + "You're the same age as " + name + ".";
            } else if (userAge > age) {
                double difference = userAge - age;
                double wait = (userLowerRange - age)*2;
                if (age < userLowerRange) {
                    text = text + "You're " + round(difference, 2) + " years older than " + name + ".\n\n...but if you wait " + round(wait, 2) + " years...";
                } else if (difference < userLowerRange / 6) {
                    text = text + "You're only " + round(difference, 2) + " years older than " + name + ".";
                } else {
                    text = text + "You're " + round(difference, 2) + " years older than " + name + ".";
                }
            } else if (userAge < age) {
                double difference = age - userAge;
                double wait = (lowerRange - userAge)*2;
                if (age > userUpperRange) {
                    text = text + name + " is " + round(difference, 2) + " years older than you.\n\n...but if you wait " + round(wait, 2) + " years...";
                } else if (difference < lowerRange / 6) {
                    text = text + name + " is only " + round(difference, 2) + " years older than you.";
                } else {
                    text = text + name + " is " + round(difference, 2) + " years older than you.";
                }
            }
        }

        TextView results = (TextView) findViewById(R.id.results);
        results.setText(text);
    }

    public void back(View view) {
        finish();
    }

    public void done(View view) {
        Intent i = new Intent(this, Names.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
