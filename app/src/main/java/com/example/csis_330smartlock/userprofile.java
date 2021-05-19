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

public class userprofile extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String userid = currentUser.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference users = db.collection("users");
    Map<String, Object> user = new HashMap<>();
    public static final String TAG = "YOUR-TAG-NAME";
    String firstName;
    String lastName;
    String email;
    double currentBalance;
    TextView userFirstName;
    TextView userLastName;
    TextView useremail;
    TextView usercurrentBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        userFirstName = findViewById(R.id.userFirstName);
        userLastName = findViewById(R.id.userLastname);
        useremail = findViewById(R.id.userEmail);
        usercurrentBalance = findViewById(R.id.userCurrentBalance);
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
                        userFirstName.setText(firstName);
                        userLastName.setText(lastName);
                        useremail.setText(email);
                        String balance = Double.toString(currentBalance);
                        usercurrentBalance.setText(balance);
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