package com.halfplusseven.danielgoodwyn.halfplusseven;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Add extends AppCompatActivity {

    Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR)-21;
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);
    long now = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_add);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -21);
        Date time = cal.getTime();
        updateLabel(time);
        final Button dateButton = (Button) findViewById(R.id.dateButton);
        final EditText ageEditText = (EditText) findViewById(R.id.ageEditText);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(Add.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mYear = year;
                                mMonth = monthOfYear;
                                mDay = dayOfMonth;
                                dateButton.setText((monthOfYear + 1) + " / " + dayOfMonth + " / " + year);
                                Date dob = new Date(year - 1900, monthOfYear, dayOfMonth);
                                setAge(dob);
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();
            }
        });
        ageEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                setDOB();
                return false;
            }
        });
        EditText name = (EditText) findViewById(R.id.name);
        name.setFocusableInTouchMode(true);
        name.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(name, InputMethodManager.SHOW_FORCED);
    }

    private void updateLabel(Date date) {
        final Button dateButton = (Button) findViewById(R.id.dateButton);
        String myFormat = "MM / dd / yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateButton.setText(sdf.format(date));
        setAge(date);
    }

    public void setAge(Date date) {
        EditText ageText = (EditText) findViewById(R.id.ageEditText);
        double time = date.getTime();
        double age = ((time-now)*-1)/365.25/24/60/60/1000;
        ageText.setText(round(age, 2) + "");
    }

    public void setDOB() {
        Button dateButton = (Button) findViewById(R.id.dateButton);
        EditText ageText = (EditText) findViewById(R.id.ageEditText);
        long now = System.currentTimeMillis();
        if (!ageText.getText().toString().equals("")) {
            String ageString = ageText.getText().toString();
            Double age = (Double.parseDouble(ageString))*(365.25*24*60*60*1000);
            Date date = new Date(now);
            if (age > (14*365.25*24*60*60*1000)-1) {
                date = new Date((long) (now - age));
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                mDay = cal.get(Calendar.DAY_OF_MONTH);
                mMonth = cal.get(Calendar.MONTH);
                mYear = cal.get(Calendar.YEAR);
            }
            String myFormat = "MM / dd / yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            dateButton.setText(sdf.format(date));
        }
    }

    public void cancel(View view) {
        finish();
    }

    public void add(View view) {
        ParseUser user = ParseUser.getCurrentUser();
        ParseObject person = new ParseObject("Person");
        EditText name = (EditText) findViewById(R.id.name);
        person.put("name", capitalizeWords(name.getText().toString()));
        Date dob = new Date(mYear-1900, mMonth, mDay);
        person.put("DOB", dob);
        person.put("user", user);
        try {
            person.save();
        } catch (ParseException e) {
            e.printStackTrace();
            finish();
        }
        Intent i = new Intent(Add.this, com.halfplusseven.danielgoodwyn.halfplusseven.Answer.class);
        i.putExtra("name", capitalizeWords(name.getText().toString()));
        i.putExtra("DOB", dob.getTime());
        startActivity(i);
        finish();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String capitalizeWords(String string) {
        if (string != null && !(string.equals(""))) {
            String[] arr = string.split(" ");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < arr.length; i++) {
                sb.append(Character.toUpperCase(arr[i].charAt(0))).append(arr[i].substring(1)).append(" ");
            }
            return sb.toString().trim();
        } else {
            return "👤";
        }
    }
}
