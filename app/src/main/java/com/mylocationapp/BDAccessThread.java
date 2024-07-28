package com.mylocationapp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BDAccessThread extends Thread{

    private FirebaseFirestore dateBase = FirebaseFirestore.getInstance();
    private ArrayList<Region> list;
    private boolean state = false;

    public BDAccessThread (ArrayList<Region> list) {
        this.list = list;
    }

    @Override
    public void run() {

        if (this.state) {
            Map<String, Object> location = new HashMap<>();

            for(Region region : list) {
                location.put("nome", region.getName());
                location.put("lat", region.getLatitude());
                location.put("long", region.getLongitude());

                dateBase.collection("Locations")
                        .add(location)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("BDThread", "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("BDThread", "Error adding document", e);
                            }
                        });

            }

            stopBD();
        }
    }

    public void startBD(){
        this.state = true;
        if(this.getState() == State.NEW) {
            this.start();
        } else {
            this.run();
        }
    }

    public void stopBD(){
        this.state = false;
    }
}
