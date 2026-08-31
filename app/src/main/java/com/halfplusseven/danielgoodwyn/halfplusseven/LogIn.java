package com.halfplusseven.danielgoodwyn.halfplusseven;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);
    }

    String emailString = "";
    String passwordString = "";

    public void logIn(View view) {
        EditText email = (EditText) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);
        emailString = email.getText().toString().toLowerCase();
        passwordString = password.getText().toString();

        if (!(emailString.equals(""))&&!(passwordString.equals(""))) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("name", emailString);
                    userData.put("DOB", new Date("11/18/1988"));
                    
                    db.collection("users").document(task.getResult().getUser().getUid()).set(userData);

                    Button button = (Button) findViewById(R.id.logIn);
                    button.setBackgroundResource(R.drawable.begin);
                    button.setTextColor(getResources().getColor(R.color.darkBlue));
                    button.setText("...");
                    Intent i = new Intent(LogIn.this, com.halfplusseven.danielgoodwyn.halfplusseven.User.class);
                    startActivity(i);
                    finish();
                } else {
                    auth.signInWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(this, signInTask -> {
                        if (signInTask.isSuccessful()) {
                            Button button = (Button) findViewById(R.id.logIn);
                            button.setBackgroundResource(R.drawable.begin);
                            button.setTextColor(getResources().getColor(R.color.darkBlue));
                            button.setText("...");
                            Intent i = new Intent(LogIn.this, Names.class);
                            startActivity(i);
                            finish();
                        } else {
                            Button button = (Button) findViewById(R.id.logIn);
                            button.setBackgroundResource(R.drawable.beginfail);
                            button.setTextColor(getResources().getColor(R.color.white));
                            button.setText("try again...");
                        }
                    });
                }
            });
        } else {
            Button button = (Button) findViewById(R.id.logIn);
            button.setBackgroundResource(R.drawable.failbegin);
            button.setTextColor(getResources().getColor(R.color.red));
            button.setText("type something...");
        }
        email.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                setButton();
                return false;
            }
        });
        password.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                setButton();
                return false;
            }
        });
    }

    public void setButton() {
        Button button = (Button) findViewById(R.id.logIn);
        button.setBackgroundResource(R.drawable.begin);
        button.setTextColor(getResources().getColor(R.color.white));
        button.setText("Begin");
    }
}
