package com.halfplusseven.danielgoodwyn.halfplusseven;

import com.danielgoodwyn.halfplussevenapp.R;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class LogIn extends AppCompatActivity {

    private static final String TAG = "LogIn";
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    String emailString = "";
    String passwordString = "";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void logIn(View view) {
        EditText email = (EditText) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);
        emailString = email.getText().toString().toLowerCase().trim();
        passwordString = password.getText().toString();

        if (!(emailString.equals("")) && !(passwordString.equals(""))) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(emailString, passwordString).addOnCompleteListener(this, task -> {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("name", emailString);
                    userData.put("email", emailString);
                    userData.put("dob", new GregorianCalendar(1988, Calendar.NOVEMBER, 18).getTime());
                    
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

    public void signInWithGoogle(View view) {
        Button button = (Button) findViewById(R.id.googleSignInBtn);
        if (button != null) {
            button.setText("...");
        }
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null && account.getIdToken() != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                } else {
                    resetGoogleButton();
                    Toast.makeText(this, "Google Sign-In failed: No ID token", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                resetGoogleButton();
                Log.w(TAG, "Google sign in failed, code: " + e.getStatusCode(), e);
                Toast.makeText(this, "Google Sign-In failed (" + e.getStatusCode() + ")", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                        FirebaseUser user = task.getResult().getUser();
                        handleUserPostAuth(user);
                    } else {
                        resetGoogleButton();
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(LogIn.this, "Authentication failed: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void resetGoogleButton() {
        Button button = (Button) findViewById(R.id.googleSignInBtn);
        if (button != null) {
            button.setText(R.string.beginWithGoogle);
        }
    }

    private void handleUserPostAuth(FirebaseUser user) {
        if (user == null) {
            resetGoogleButton();
            return;
        }

        String uid = user.getUid();
        String email = user.getEmail() != null ? user.getEmail().toLowerCase() : "";
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(uid).get().addOnCompleteListener(docTask -> {
            if (docTask.isSuccessful() && docTask.getResult() != null && docTask.getResult().exists()) {
                DocumentSnapshot doc = docTask.getResult();
                if (doc.getString("email") == null && !email.isEmpty()) {
                    db.collection("users").document(uid).update("email", email);
                }
                navigateToNames();
            } else {
                if (!email.isEmpty()) {
                    db.collection("users").whereEqualTo("email", email).get().addOnCompleteListener(queryTask -> {
                        if (queryTask.isSuccessful() && queryTask.getResult() != null && !queryTask.getResult().isEmpty()) {
                            DocumentSnapshot existingDoc = queryTask.getResult().getDocuments().get(0);
                            migrateAndCreateUser(existingDoc, uid, email, user.getDisplayName());
                        } else {
                            db.collection("users").whereEqualTo("name", email).get().addOnCompleteListener(nameTask -> {
                                if (nameTask.isSuccessful() && nameTask.getResult() != null && !nameTask.getResult().isEmpty()) {
                                    DocumentSnapshot existingDoc = nameTask.getResult().getDocuments().get(0);
                                    migrateAndCreateUser(existingDoc, uid, email, user.getDisplayName());
                                } else {
                                    createNewUser(uid, email, user.getDisplayName());
                                }
                            });
                        }
                    });
                } else {
                    createNewUser(uid, email, user.getDisplayName());
                }
            }
        });
    }

    private void migrateAndCreateUser(DocumentSnapshot existingDoc, String newUid, String email, String displayName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String oldUid = existingDoc.getId();
        String name = existingDoc.getString("name");
        if (name == null || name.isEmpty()) {
            name = (displayName != null && !displayName.isEmpty()) ? displayName : email;
        }
        Date dob = existingDoc.getDate("dob");
        if (dob == null) {
            dob = new GregorianCalendar(1988, Calendar.NOVEMBER, 18).getTime();
        }
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("email", email);
        userData.put("dob", dob);

        db.collection("users").document(newUid).set(userData, SetOptions.merge()).addOnCompleteListener(t -> {
            migratePersons(oldUid, newUid);
            navigateToNames();
        });
    }

    private void migratePersons(String oldUid, String newUid) {
        if (oldUid == null || newUid == null || oldUid.equals(newUid)) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("persons").whereEqualTo("userId", oldUid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DocumentSnapshot doc : task.getResult()) {
                    doc.getReference().update("userId", newUid, "user", newUid);
                }
            }
        });
        db.collection("persons").whereEqualTo("user", oldUid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DocumentSnapshot doc : task.getResult()) {
                    doc.getReference().update("userId", newUid, "user", newUid);
                }
            }
        });
    }

    private void createNewUser(String uid, String email, String displayName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", (displayName != null && !displayName.isEmpty()) ? displayName : email);
        userData.put("email", email);
        userData.put("dob", new GregorianCalendar(1988, Calendar.NOVEMBER, 18).getTime());
        db.collection("users").document(uid).set(userData).addOnCompleteListener(t -> {
            Intent i = new Intent(LogIn.this, com.halfplusseven.danielgoodwyn.halfplusseven.User.class);
            startActivity(i);
            finish();
        });
    }

    private void navigateToNames() {
        Intent i = new Intent(LogIn.this, Names.class);
        startActivity(i);
        finish();
    }
}
