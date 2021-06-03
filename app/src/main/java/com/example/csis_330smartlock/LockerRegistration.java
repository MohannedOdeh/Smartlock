package com.example.csis_330smartlock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Map;

public class LockerRegistration extends AppCompatActivity{
    private static final String TAG = "LockerRegistration";
    //FirebaseAuth mAuth;
    FirebaseFirestore db;
    CollectionReference lockers;
    CollectionReference users;
//    FirebaseUser currentUser;
//    String userid;
    Spinner spinnerBuilding;
    Spinner spinnerFloor;
    Button btnViewFloor;
    ConstraintLayout lockerLayout;
    String selectedBuilding;
    String selectedFloor;
    ListenerRegistration update;
    int selectedLocker;
    Button btnConfirmLocker;
    double add;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String userid = currentUser.getUid();
    double minimum = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker_registration);

        db = FirebaseFirestore.getInstance();
        lockers = db.collection("lockers");
        users = db.collection("users");
//        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser();
//        userid = currentUser.getUid();

        btnViewFloor = findViewById(R.id.btnViewFloor);
        lockerLayout = findViewById(R.id.lockerLayout);
        DocumentReference docRef = db.collection("users").document(userid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        testingMethod(document.getDouble("Current balance"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        createBuildingSpinner();
    }

    public void testingMethod(double a)
    {
        add = a;
    }

    private void createBuildingSpinner() {
        spinnerBuilding = findViewById(R.id.spinnerBuilding);
        ArrayList<String> buildings = new ArrayList<>();
        buildings.add("-");
        lockers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    // Loop through all entries in "lockers" collection
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Map<String, Object> map = document.getData();
//                        for(Map.Entry<String, Object> entry : map.entrySet()) {
//                            buildingNames.add(entry.getKey());
//                        }
                        String building = map.get("building").toString();
                        // Add the building name to ArrayList only if it doesn't already exist in it
                        if(!buildings.contains(building)) {
                            buildings.add(building);
                        }
                        Collections.sort(buildings);
                    }

                    // Create a spinner using the Array List
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(LockerRegistration.this, android.R.layout.simple_spinner_item, buildings);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerBuilding.setAdapter(adapter);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

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
        spinnerFloor = findViewById(R.id.spinnerFloor);

        ArrayList<String> floors = new ArrayList<>();
        lockers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
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

                    btnViewFloor.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
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
                            lockerLayout.setVisibility(View.VISIBLE);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                //int lockerNumber = Integer.parseInt(document.getString("number"));
                                Map<String, Object> map = document.getData();
                                long lockerNumber = (long) map.get("number");
                                boolean reserved = (boolean) map.get("reserved");
                                String imgID = "img" + lockerNumber;
                                int rImgID = getResources().getIdentifier(imgID, "id", getPackageName());
                                ImageView img = findViewById(rImgID);
                                if (reserved) {
                                    img.setImageResource(R.drawable.ic_locked);
                                    img.setClickable(false);
                                } else {
                                    img.setImageResource(R.drawable.ic_unlocked);
                                    img.setClickable(true);
                                }
                            }
                            checkForChanges();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void checkForChanges() {
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

                                    Map<String, Object> map = dc.getDocument().getData();
                                    long lockerNumber = (long) map.get("number");
                                    boolean reserved = (boolean) map.get("reserved");
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

    public void confirmLocker(View view) {
        // Searches for the locker with the selected building, floor, and locker number then sets
        // the reservation status to false

        //Query to search for the locker


        if(Double.compare(add,minimum)==0 || Double.compare(add,minimum) > 0) {
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

                                    //Update the reservation status
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

                                    //Update the user's profile
//                                users.document(document.getId())
//                                        .update("reserved", true)
//                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                            @Override
//                                            public void onSuccess(Void aVoid) {
//                                                Log.d(TAG, "DocumentSnapshot successfully updated!");
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Log.w(TAG, "Error updating document", e);
//                                            }
//                                        });


                                    DocumentReference userDocRef = db.collection("users").document(userid);
                                    String lockerID = "B" + selectedBuilding + "F" + selectedFloor + "N" + selectedLocker;
                                    userDocRef.update("Reserved Locker", lockerID)
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
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
        else{
            CharSequence text = "Not enough funds. Please add some more";
            Toast toast = Toast.makeText (this, text, Toast.LENGTH_LONG);
            toast.show ();
            Intent intent = new Intent(this, Payment.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Quit the realtime updater if the activity ends
        if (update != null)
            update.remove();
    }

    //    private void createSpinner(Spinner spinner, String fieldName) {
//        ArrayList<String> arrayList = new ArrayList<>();
//        lockers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        Log.d(TAG, document.getId() + " => " + document.getData());
//                        Map<String, Object> map = document.getData();
////                        for(Map.Entry<String, Object> entry : map.entrySet()) {
////                            buildingNames.add(entry.getKey());
////                        }
//                        String fieldValue = map.get(fieldName).toString();
//                        if(!arrayList.contains(fieldValue))
//                            arrayList.add(fieldValue);
//                        Collections.sort(arrayList);
//                    }
//                } else {
//                    Log.d(TAG, "Error getting documents: ", task.getException());
//                }
//            }
//        });
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);
//    }

//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        textView.setText("Hellooo");
//        //if (parent.getId()==R.id.spinnerBuilding) {
//            String building = parent.getItemAtPosition(position).toString();
//            Toast.makeText(parent.getContext(), building, Toast.LENGTH_SHORT).show();
//
//            spinnerFloor = findViewById(R.id.spinnerFloor);
//            ArrayList<String> floors = new ArrayList<>();
//            lockers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            Log.d(TAG, document.getId() + " => " + document.getData());
//                            Map<String, Object> map = document.getData();
////                        for(Map.Entry<String, Object> entry : map.entrySet()) {
////                            buildingNames.add(entry.getKey());
////                        }
//                            if (map.get("building").equals(building)) {
//                                String floor = map.get("floor").toString();
//                                if (!floors.contains(floor))
//                                    floors.add(floor);
//                            }
//                        }
//                        Collections.sort(floors);
//                    } else {
//                        Log.d(TAG, "Error getting documents: ", task.getException());
//                    }
//                }
//            });
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, floors);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinnerFloor.setAdapter(adapter);
//            spinnerFloor.setOnItemSelectedListener(this);
//        //}
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
}