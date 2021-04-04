package com.example.csis_330smartlock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
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
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker_registration);

        db = FirebaseFirestore.getInstance();
        lockers = db.collection("lockers");
        textView = findViewById(R.id.textView);

        spinnerBuilding = findViewById(R.id.spinnerBuilding);
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
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Map<String, Object> map = document.getData();
//                        for(Map.Entry<String, Object> entry : map.entrySet()) {
//                            buildingNames.add(entry.getKey());
//                        }
                        String building = map.get("building").toString();
                        if(!buildings.contains(building)) {
                            buildings.add(building);
                        }
                        Collections.sort(buildings);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LockerRegistration.this, android.R.layout.simple_spinner_item, buildings);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerBuilding.setAdapter(adapter);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        spinnerBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    textView.setText("Hellooo");
                    //if (parent.getId()==R.id.spinnerBuilding) {
                    String building = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), building, Toast.LENGTH_SHORT).show();

                    spinnerFloor = findViewById(R.id.spinnerFloor);

                    ArrayList<String> floors = new ArrayList<>();
                    lockers.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Map<String, Object> map = document.getData();
//                        for(Map.Entry<String, Object> entry : map.entrySet()) {
//                            buildingNames.add(entry.getKey());
//                        }
                                    if (map.get("building").equals(building)) {
                                        String floor = map.get("floor").toString();
                                        if (!floors.contains(floor))
                                            floors.add(floor);
                                    }
                                }
                                Collections.sort(floors);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(LockerRegistration.this, android.R.layout.simple_spinner_item, floors);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerFloor.setAdapter(adapter);
                                spinnerFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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