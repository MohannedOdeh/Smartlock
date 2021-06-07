package com.example.csis_330smartlock;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * @author David Liang, Mohanned Odeh
 */
public class MainActivity extends AppCompatActivity {
    String lockerID;
    ProgressBar progressBar;
    Button btnRegisterLocker;
    Button btnDeregisterLocker;

    // Initialize Firebase variables
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String userid = currentUser.getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference usersDocRef = db.collection("users").document(userid);
    CollectionReference lockers = db.collection("lockers");
    DocumentReference locker;
    ListenerRegistration update;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        btnRegisterLocker = findViewById(R.id.btnRegisterLocker);
        btnDeregisterLocker = findViewById(R.id.btnDeregisterLocker);

        checkForLocker();
        checkForChanges();
//        if (mAuth.getCurrentUser() != null) {
//            FirebaseUser currentUser = mAuth.getCurrentUser();
//            welcome.setText("Welcome " + currentUser.getDisplayName());
//        }
    }

    private void checkForLocker() {
        // Use the current user's id to retrieve their profile information from the database
        // If the user has a locker registered, a button to deregister will be shown

        // Show progress bar in place of buttons
        progressBar.setVisibility(View.VISIBLE);
        btnRegisterLocker.setVisibility(View.GONE);
        btnDeregisterLocker.setVisibility(View.GONE);

        usersDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        lockerID = document.getString("Reserved Locker");
                        // If there is no locker registered to the user, show the registration button
                        // Otherwise, show the deregistration button
                        // Remove the progress bar before showing a button
                        if (lockerID.equals("None")) {
                            progressBar.setVisibility(View.GONE);
                            btnRegisterLocker.setVisibility(View.VISIBLE);
                            btnDeregisterLocker.setVisibility(View.GONE);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            btnRegisterLocker.setVisibility(View.GONE);
                            btnDeregisterLocker.setVisibility(View.VISIBLE);
                            locker = lockers.document(lockerID);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    // Go to the page that displays the profile information
    public void toUserProfile (View view){
        Intent intent = new Intent(this, UserProfile.class);
        startActivity(intent);
    }

    // Go to the page to add fund
    public void toPayment (View view){
        Intent intent = new Intent(this, Payment.class);
        startActivity(intent);
    }

    // Go to the locker registration page
    public void toLockers (View view) {
        Intent intent = new Intent(this, LockerRegistration.class);
        startActivity(intent);
    }

    // Deregisters the locker from the user's account
    public void deregisterLocker (View view){
        // Empty the Reserved Locker field of the user's account
        usersDocRef
                .update("Reserved Locker", "None")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User profile successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        //Update the reservation status of the locker
        locker
            .update("reserved", false)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Locker successfully updated!");
                    Toast.makeText(MainActivity.this, "Deregistration successful", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG, "Error updating document", e);
                }
            });

        // Remove the button to unregister a locker
        btnDeregisterLocker.setVisibility(View.GONE);
    }

    private void checkForChanges() {
        // If the database is changed, update the images on-screen with the
        // correct reservation status
        update = lockers.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }
                        checkForLocker();
                    }
                });
    }

    // Sign out from the current account
    public void signOut (View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }
}