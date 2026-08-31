package com.halfplusseven.danielgoodwyn.halfplusseven;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        FirebaseApp.initializeApp(this);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
