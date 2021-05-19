package com.example.csis_330smartlock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    EditText firstName;
    EditText lastName;
    EditText password;
    EditText email;
    Button btnSignUp;
    Button logout;
    TextView welcome;
    private FirebaseAuth mAuth;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);

        logout = findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();

//        if (mAuth.getCurrentUser() != null) {
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            welcome.setText("Welcome " + currentUser.getDisplayName());
//        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void toLockers (View view) {
        Intent intent = new Intent(this, LockerRegistration.class);
        startActivity(intent);
    }

    public void toPayment (View view){
        Intent intent = new Intent(this, Payment.class);
        startActivity(intent);
    }

    public void toUserProfile (View view){
        Intent intent = new Intent(this, userprofile.class);
        startActivity(intent);
    }
}