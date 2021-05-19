package com.example.csis_330smartlock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vishnusivadas.advanced_httpurlconnection.PutData;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    EditText firstName;
    EditText lastName;
    EditText password;
    EditText email;
    MaterialButton btnSignUp;
    TextView txtLogin;
    ProgressBar progressBar;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        btnSignUp = findViewById(R.id.btnSignUp);
        txtLogin = findViewById(R.id.txtLogin);
        progressBar = findViewById(R.id.progress);

        mAuth = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String userEmail = email.getText().toString();
//                String userPassword = password.getText().toString();
//                String fn = firstName.getText().toString();
//                String ln = lastName.getText().toString();
//                createAccount(userEmail, userPassword);
//                user.put("First Name", fn);
//                user.put("Last Name", ln);
//                users.document(userEmail).set(user);

                if (checkDataEntered()) {
                    String userEmail = email.getText().toString();
                    String userPassword = password.getText().toString();
                    createAccount(userEmail, userPassword);
                } else {
                    progressBar.setVisibility(View.GONE);
                    btnSignUp.setVisibility(View.VISIBLE);
                }
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            reload();
        }
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser fbUser = mAuth.getCurrentUser();
                            updateDB();
                            updateUI(fbUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            btnSignUp.setVisibility(View.VISIBLE);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void reload() {
        // Go to main menu
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateDB() {
        // Initialize Firestore variables
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection("users");
        Map<String, Object> user = new HashMap<>();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userid = currentUser.getUid();
        // Write first and last name into Firestore database
        String fn = firstName.getText().toString();
        String ln = lastName.getText().toString();
        String userEmail = email.getText().toString();
        double balance = 0;
        String reservedLocker = "None";
        user.put("First Name", fn);
        user.put("Last Name", ln);
        user.put("Email", userEmail);
        user.put("Current balance", balance);
        user.put("Reserved Locker", reservedLocker);
        users.document(userid).set(user);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Set Display Name to the entered first name
            UserProfileChangeRequest setDisplayName = new UserProfileChangeRequest.Builder().setDisplayName(firstName.getText().toString()).build();
            user.updateProfile(setDisplayName);

            //Re-authentication to update Display Name
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Toast.makeText(SignUp.this, "Successful Authentication", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "signInWithEmail:success");
                                //FirebaseUser user = mAuth.getCurrentUser();

                                //Go to main menu
                                reload();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignUp.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    boolean isEmail(EditText text) {
        CharSequence email = text.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    boolean isEmpty(EditText text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    boolean checkDataEntered() {
        // Check if form is filled correctly
        progressBar.setVisibility(View.VISIBLE);
        btnSignUp.setVisibility(View.INVISIBLE);
        if (isEmpty(firstName)) {
            Toast.makeText(this, "You must enter first name to register!", Toast.LENGTH_SHORT).show();
            firstName.setError("First name is required!");
            return false;
        }

        if (isEmpty(lastName)) {
            Toast.makeText(this, "Enter a last name to register", Toast.LENGTH_SHORT).show();
            lastName.setError("Last name is required!");
            return false;
        }

        if (!isEmail(email)) {
            Toast.makeText(this, "Enter an e-mail address to register", Toast.LENGTH_SHORT).show();
            email.setError("Enter valid email!");
            return false;
        }

        if (isEmpty(password))
        {
            Toast.makeText(this, "Enter a password to register", Toast.LENGTH_SHORT).show();
            password.setError("Enter a password!");
            return false;
        }

        if (password.getText().toString().length() < 6)
        {
            Toast.makeText(this, "Password must have at least 6 characters", Toast.LENGTH_SHORT).show();
            password.setError("Password must have at least 6 characters");
            return false;
        }
        return true;
    }
}