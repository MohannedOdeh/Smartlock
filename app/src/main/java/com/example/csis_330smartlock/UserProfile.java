package com.example.csis_330smartlock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity {
    // Initialize Firebase variables
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String userid = currentUser.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

//    CollectionReference users = db.collection("users");
//    Map<String, Object> user = new HashMap<>();
    public static final String TAG = "UserProfile";

    String firstName;
    String lastName;
    String email;
    double currentBalance;
    TextView userFirstName;
    TextView userLastName;
    TextView userEmail;
    TextView userCurrentBalance;
    TextView reservedLocker;
    String locker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        reservedLocker = findViewById(R.id.reservedLocker);
        userFirstName = findViewById(R.id.userFirstName);
        userLastName = findViewById(R.id.userLastName);
        userEmail = findViewById(R.id.userEmail);
        userCurrentBalance = findViewById(R.id.userCurrentBalance);

        // Use the current user's id to retrieve their profile information from the database
        DocumentReference docRef = db.collection("users").document(userid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        firstName = document.getString("First Name");
                        lastName = document.getString("Last Name");
                        currentBalance = document.getDouble("Current balance");
                        email = document.getString("Email");
                        locker = document.getString("Reserved Locker");
                        userFirstName.setText(firstName);
                        userLastName.setText(lastName);
                        userEmail.setText(email);
                        reservedLocker.setText(locker);
                        String balance = Double.toString(currentBalance);
                        userCurrentBalance.setText(balance);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }
}