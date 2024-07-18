package com.mylocationapp;

import android.Manifest.permission;
import android.Manifest;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mylocationapp.GPSThread.GPSCallback;
import com.mylocationapp.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity implements GPSCallback, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private ActivityMainBinding binding;
    private GoogleMap googleMap;
    private Marker marker;
    private LatLng location;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GPSThread GPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);
        binding.mapView.onResume();

        this.GPS = new GPSThread(this,this);

        binding.startLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GPS.isRunning()){
                    GPS.startGPS();
                }
            }
        });

        binding.stopLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPS.stopGPS();
            }
        });

        binding.addRegion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void updateMarker() {
        if (this.marker != null) {
            this.marker.setPosition(this.location);
        } else {
            this.marker = this.googleMap.addMarker(new MarkerOptions().position(this.location).title("LOC"));
        }
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.location, 12));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.enableMyLocation();
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.googleMap.setMyLocationEnabled(true);
            return;
        }

        // 2. Otherwise, request location permissions from the user.
        PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, true);
    }

    @Override
    public void onLocationUpdate(double latitude, double longitude) {

        this.location = new LatLng(latitude, longitude);
        binding.latitude.setText(String.valueOf(latitude));
        binding.longitude.setText(String.valueOf(longitude));
        updateMarker();
    }

}