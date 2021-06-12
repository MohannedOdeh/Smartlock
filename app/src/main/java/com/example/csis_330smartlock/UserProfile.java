package com.example.csis_330smartlock;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * @author David Liang, Mohanned Odeh
 */
public class UserProfile extends AppCompatActivity {
    // Initialize Firebase variables
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String userid = currentUser.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
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

                        // Set the profile details into fields on the screen
                        firstName = document.getString("First Name");
                        lastName = document.getString("Last Name");
                        currentBalance = document.getDouble("Current balance");
                        email = document.getString("Email");
                        locker = document.getString("Reserved Locker");
                        userFirstName.setText(firstName);
                        userLastName.setText(lastName);
                        userEmail.setText(email);
                        if (!locker.equals("None")) {
                            String building = locker.substring(1, 2);
                            if (building.equals("1"))
                                building = "A";
                            else if (building.equals("2"))
                                building = "B";
                            String floor = locker.substring(3, 4);
                            String number = locker.substring(5, 7);
                            String display = "Building : " + building + "\nFloor : " + floor + "\nLocker : " + number;
                            reservedLocker.setText(display);
                        } else {
                            reservedLocker.setText(locker);
                        }
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