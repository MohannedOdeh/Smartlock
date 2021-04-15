package com.example.csis_330smartlock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
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
    FirebaseFirestore db;
    CollectionReference lockers;
    Spinner spinnerBuilding;
    Spinner spinnerFloor;
    Button btnViewFloor;
    ConstraintLayout lockerLayout;
    String selectedBuilding;
    String selectedFloor;
    ListenerRegistration update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker_registration);

        db = FirebaseFirestore.getInstance();
        lockers = db.collection("lockers");
        btnViewFloor = findViewById(R.id.btnViewFloor);
        lockerLayout = findViewById(R.id.lockerLayout);
        createBuildingSpinner();
        //createSpinner(spinnerBuilding, "building");

//        spinnerFloor = findViewById(R.id.spinnerFloor);
//        createSpinner(spinnerFloor, "floor");

        //        ArrayAdapter.createFromResource(this, buildingNames, android.R.layout.simple_spinner_item);

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
                    //textView.setText("Hellooo");
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
                                } else {
                                    img.setImageResource(R.drawable.ic_unlocked);
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
                                case ADDED:
                                    Log.d(TAG, "New locker: " + dc.getDocument().getData());
                                    break;
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
                                    } else {
                                        img.setImageResource(R.drawable.ic_unlocked);
                                    }
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed locker: " + dc.getDocument().getData());
                                    break;
                            }
                        }

                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
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