package com.example.csis_330smartlock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    //:( :( :( :( :( :( :(
    EditText firstName;
    EditText lastName;

    EditText password;
    EditText email;
    Button btnSignUp;
    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);

    }
}