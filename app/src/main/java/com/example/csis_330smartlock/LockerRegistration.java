package com.example.csis_330smartlock;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author David Liang
 */
public class LockerRegistration extends AppCompatActivity{
    private static final String TAG = "LockerRegistration";
    Spinner spinnerBuilding;
    Spinner spinnerFloor;
    Button btnViewFloor;
    ConstraintLayout lockerLayout;
    String selectedBuilding;
    String selectedFloor;
    ListenerRegistration update;
    int selectedLocker;
    Button btnConfirmLocker;

    // Initialize Firebase variables
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String userid = currentUser.getUid();
    CollectionReference users = db.collection("users");
    DocumentReference userDocRef = users.document(userid);
    CollectionReference lockers = db.collection("lockers");

    double minimum = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker_registration);

        btnViewFloor = findViewById(R.id.btnViewFloor);
        // Loop through the buttons on the screen and set them to reserved or available
        // depending on the information in the database
        lockerLayout = findViewById(R.id.lockerLayout);

        createBuildingSpinner();
    }

    private void createBuildingSpinner() {
        // Function to create a spinner that displays the available buildings

        spinnerBuilding = findViewById(R.id.spinnerBuilding);
        ArrayList<String> buildings = new ArrayList<>();
        // Add a '-' as the first item so nothing in the spinner is selected
        buildings.add("-");

        // Get locker information from the database and populate the spinner
        // with the buildings
        lockers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    // Loop through all entries in "lockers" collection
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());

//                        for(Map.Entry<String, Object> entry : map.entrySet()) {
//                            buildingNames.add(entry.getKey());
//                        }

                        // Create a Map object with the locker information
                        // and add the unique buildings to an ArrayList
                        Map<String, Object> map = document.getData();
                        String building = map.get("building").toString();
                        if(!buildings.contains(building)) {
                            buildings.add(building);
                        }
                    }

                    // Create a spinner using the Array List
                    Collections.sort(buildings);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(LockerRegistration.this, android.R.layout.simple_spinner_item, buildings);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBuilding.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        // Store the selected building in a variable
        // When a building is selected, another spinner for the floors is created
        spinnerBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    //if (parent.getId()==R.id.spinnerBuilding) {
                    selectedBuilding = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), selectedBuilding, Toast.LENGTH_SHORT).show();
                    createFloorSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void createFloorSpinner() {
        // Function to create a spinner that displays the available floors

        spinnerFloor = findViewById(R.id.spinnerFloor);
        ArrayList<String> floors = new ArrayList<>();

        // Get locker information from the database and populate the spinner
        // with the floors
        lockers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Loop through all entries in "lockers" collection
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        // Create a Map object with the locker information
                        // and add the unique floors to an ArrayList
                        Map<String, Object> map = document.getData();
                        if (map.get("building").equals(selectedBuilding)) {
                            String floor = map.get("floor").toString();
                            if (!floors.contains(floor))
                                floors.add(floor);
                        }
                    }
                    Collections.sort(floors);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(LockerRegistration.this, android.R.layout.simple_spinner_item, floors);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerFloor.setAdapter(adapter);

                    // Show the buttons to select the lockers
                    btnViewFloor.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        // Store the selected floor in a variable
        spinnerFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // I want this part to work if selectedFloor is an integer, but it doesn't and I don't know why
                //selectedFloor = Integer.parseInt(parent.getItemAtPosition(position).toString());
                selectedFloor = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), selectedFloor, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void displayFloor(View view) {
        lockers.whereEqualTo("building", selectedBuilding)
                .whereEqualTo("floor", Integer.parseInt(selectedFloor))
                .orderBy("number")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Show the layout of the lockers on-screen
                            lockerLayout.setVisibility(View.VISIBLE);

                            // Loop through the result and set the color of the images to
                            // the correct reservation status
                            // Available lockers are green and reserved lockers are red
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                //int lockerNumber = Integer.parseInt(document.getString("number"));

                                // Create a Map object with the locker's number and reservation status
                                Map<String, Object> map = document.getData();

                                setLockerColor(map);
//                                long lockerNumber = (long) map.get("number");
//                                boolean reserved = (boolean) map.get("reserved");
//
//                                // Search for the image with the same number and set its color
//                                String imgID = "img" + lockerNumber;
//                                int rImgID = getResources().getIdentifier(imgID, "id", getPackageName());
//                                ImageView img = findViewById(rImgID);
//                                if (reserved) {
//                                    img.setImageResource(R.drawable.ic_locked);
//                                    img.setClickable(false);
//                                } else {
//                                    img.setImageResource(R.drawable.ic_unlocked);
//                                    img.setClickable(true);
//                                }
                            }
                            checkForChanges();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void checkForChanges() {
        // If the database is changed, update the images on-screen with the
        // correct reservation status

        update = lockers.whereEqualTo("building", selectedBuilding)
                .whereEqualTo("floor", Integer.parseInt(selectedFloor))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case MODIFIED:
                                    Log.d(TAG, "Modified locker: " + dc.getDocument().getData());

                                    // Get the locker number and reservation status of the modified locker
                                    Map<String, Object> map = dc.getDocument().getData();

                                    setLockerColor(map);
                                    break;
                                case ADDED:
                                    Log.d(TAG, "Locker: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed locker: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }

    public void selectLocker(View view) {
        // Creates a border around the selected locker image and sets the
        // selectedLocker variable to this locker

        //Remove border from every image
        ConstraintLayout lockerLayout = findViewById(R.id.lockerLayout);
        for (int i = 0; i < lockerLayout.getChildCount(); i++) {
            View v = lockerLayout.getChildAt(i);
            if (v instanceof ImageView) {
                v.setBackgroundResource(0);
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }

        // Sets a border for the selected image
        view.setBackgroundResource(R.drawable.image_border);
        btnConfirmLocker = findViewById(R.id.btnConfirmLocker);
        btnConfirmLocker.setVisibility(View.VISIBLE);

        // Get the ID of the corresponding locker, then replace the variable with just the locker number
        String imgID = view.getResources().getResourceName(view.getId());
        imgID = imgID.replaceFirst(".*?(\\d+$)", "$1");
        selectedLocker = Integer.parseInt(imgID);
        Log.d(TAG, imgID);
    }

    public void setLockerColor(Map map) {
        long lockerNumber = (long) map.get("number");
        boolean reserved = (boolean) map.get("reserved");

        // Search for the image with the same number and set its color
        String imgID = "img" + lockerNumber;
        int rImgID = getResources().getIdentifier(imgID, "id", getPackageName());
        ImageView img = findViewById(rImgID);
        if (reserved) {
            img.setImageResource(R.drawable.ic_locked);
            img.setClickable(false);
            img.setBackgroundResource(0);
        } else {
            img.setImageResource(R.drawable.ic_unlocked);
            img.setClickable(true);
        }
    }

    public void confirmLocker(View view) {
        // Searches for the locker with the selected building, floor, and locker number then sets
        // the reservation status to false

        // Store the current balance of the user in a variable
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Double balance = document.getDouble("Current balance");

                        // Check if the user's balance is above the minimum
                        // If so, deduct the reservation price from their balance and update the database
                        if(Double.compare(balance,minimum)==0 || Double.compare(balance,minimum) > 0) {
                            CollectionReference users = db.collection("users");
                            double finalprice=0;
                            finalprice = balance-minimum;
                            Map<String, Object> user = new HashMap<>();
                            user.put("Current balance", finalprice);
                            users.document(userid).update(user);

                            // Update the database
                            updateDatabase();
                        }
                        else{
                            // Create an Alert Dialog to notify of insufficient funds
                            AlertDialog.Builder builder = new AlertDialog.Builder(LockerRegistration.this);
                            builder.setMessage("Insufficient funds in your account. Would you like to add more?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button
                                    Intent intent = new Intent(LockerRegistration.this, Payment.class);
                                    startActivity(intent);
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
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

    private void updateDatabase() {
        // Update the database information for the locker that is registered
        // and the user who registers it

        // Query to search for the locker so that it can be updated
        lockers.whereEqualTo("building", selectedBuilding)
                .whereEqualTo("floor", Integer.parseInt(selectedFloor))
                .whereEqualTo("number", selectedLocker)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty())
                                Log.d(TAG, "No results found");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                //Update the reservation status of the locker
                                lockers.document(document.getId())
                                        .update("reserved", true)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error updating document", e);
                                            }
                                        });

                                // Update the "Reserved Locker" field in the user's profile with
                                // the locker ID
                                userDocRef.update("Reserved Locker", document.getId())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error updating document", e);
                                            }
                                        });

                                Toast.makeText(LockerRegistration.this, "Registration successful", Toast.LENGTH_SHORT)
                                        .show();
                                finish();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
//
//    public void getBalance() {
//        // Get the balance of the user and store in a variable
//        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                        balance = document.getDouble("Current balance");
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });
//    }

    @Override
    protected void onStop() {
        super.onStop();
        // Quit the realtime updater if the activity ends
        if (update != null)
            update.remove();
    }
}