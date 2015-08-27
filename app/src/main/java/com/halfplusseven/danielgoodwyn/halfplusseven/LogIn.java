package com.halfplusseven.danielgoodwyn.halfplusseven;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Date;

public class LogIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);
    }

    String emailString = "";
    String passwordString = "";

    public void logIn(View view) throws ParseException {
        EditText email = (EditText) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);
        emailString = email.getText().toString().toLowerCase();
        passwordString = password.getText().toString();

        if (!(emailString.equals(""))&&!(passwordString.equals(""))) {
            ParseUser currentUser = new ParseUser();
            currentUser.setUsername(emailString);
            currentUser.setPassword(passwordString);
            currentUser.setEmail(emailString);
            currentUser.put("name", emailString);
            Date date = new Date("11/18/1988");
            currentUser.put("DOB", date);
            currentUser.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Button button = (Button) findViewById(R.id.logIn);
                        button.setBackgroundResource(R.drawable.begin);
                        button.setTextColor(getResources().getColor(R.color.darkBlue));
                        button.setText("...");
                        Intent i = new Intent(LogIn.this, com.halfplusseven.danielgoodwyn.halfplusseven.User.class);
                        startActivity(i);
                        finish();
                    } else {
                        ParseUser.logInInBackground(emailString, passwordString, new LogInCallback() {
                            public void done(ParseUser currentUser, ParseException e) {
                                if (currentUser != null) {
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
                            }
                        });
                    }
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
