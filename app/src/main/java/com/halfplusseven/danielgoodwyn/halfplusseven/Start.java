package com.halfplusseven.danielgoodwyn.halfplusseven;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.parse.Parse;
import com.parse.ParseUser;


public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "xxUw77Hqfy0HzwspWZMUprW3k6EK64WevWUGzpEn", "aqFfAb8ImWFCV48l7Pj1DOsXoRogxQBNqVadXACN");

        final ParseUser user = ParseUser.getCurrentUser();
        if (user != null) {
            Intent i = new Intent(this, Names.class);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(this, com.halfplusseven.danielgoodwyn.halfplusseven.LogIn.class);
            startActivity(i);
            finish();
        }
    }
}
