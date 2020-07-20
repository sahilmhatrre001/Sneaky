package com.example.sneaky;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {

    private static final String TAG = "login";
    private FirebaseAuth mAuth;
    private DatabaseReference reff;
    private Button sign_in,sign_up;
    private TextView username,password;
    private ConstraintLayout container;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sign_in = (Button) findViewById(R.id.sign_in);
        sign_up = (Button) findViewById(R.id.sign_up);
        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);
        container = (ConstraintLayout) findViewById(R.id.container);
        reff = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Hide_keyboard();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                progressDialog.setTitle("Authenticating");
                progressDialog.setMessage("Verification mail in being sent ");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                String tUsername = username.getText().toString();
                String tPassword = password.getText().toString();

                if(tPassword.length() < 7)
                {
                    toastM("Password length must be greater than 6");
                    progressDialog.dismiss();
                }
                else if(tUsername.equals("") && tPassword.equals("") ) {
                    toastM("Please enter username and password");
                    progressDialog.dismiss();
                }
                else {
                    createAccount(tUsername,tPassword);
                }
            }
        });
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tUsername = username.getText().toString();
                String tPassword = password.getText().toString();

                if(!tUsername.equals("") && !tPassword.equals("") ) {
                   signin(tUsername,tPassword);
                }
                else {
                    toastM("Please enter correct Credentials");
                }
            }
        });
    }

    private void toastM(String m) {
        Toast.makeText(getApplicationContext(),m,Toast.LENGTH_LONG).show();

    }

    private void createAccount(final String username, String password)
    {
        mAuth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(getApplicationContext(), "User SignUp Successful.", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                final FirebaseUser user = mAuth.getCurrentUser();
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Email sent.");
                                                }
                                            }
                                        });
                            }else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Authentication failed ! Try again", Toast.LENGTH_LONG).show();
                                updateUI(null);
                                progressDialog.dismiss();
                            }
                        }
                    });
    }

    private void signin(String username,String password)
    {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                           toastM("Successfully signed in");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user.isEmailVerified() == true) {
                                updateUI(user);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Verify your email address",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }





    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null) {
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void Hide_keyboard()
    {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
