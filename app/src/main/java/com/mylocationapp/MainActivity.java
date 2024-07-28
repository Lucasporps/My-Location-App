package com.mylocationapp;

import android.Manifest.permission;
import android.Manifest;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.mylocationapp.GPSThread.GPSCallback;
import com.mylocationapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements GPSCallback, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private ActivityMainBinding binding;
    private GoogleMap googleMap;
    private Marker marker;
    private LatLng location;
    private GPSThread GPS;
    private final ArrayList<Region> regions = new ArrayList<>();
    private BDAccessThread bdThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);
        binding.mapView.onResume();

        this.GPS = new GPSThread(this,this);
        this.bdThread = new BDAccessThread(this.regions);

        binding.startLoc.setOnClickListener(view -> {
            if (!GPS.isRunning()){
                GPS.startGPS();
            }
        });

        binding.stopLoc.setOnClickListener(view -> GPS.stopGPS());

        binding.addRegion.setOnClickListener(view -> saveLocation());

        binding.addBD.setOnClickListener(view -> bdThread.startBD());

    }

    private void updateMarker() {
        if (this.marker != null) {
            this.marker.setPosition(this.location);
        } else {
            this.marker = this.googleMap.addMarker(new MarkerOptions().position(this.location).title("LOC"));
        }
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(this.location, 15));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.enableMyLocation();
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        // Check de permissão para habilitar localização
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            this.googleMap.setMyLocationEnabled(true);
            return;
        }

        // Requisitar permissões
        PermissionUtils.requestLocationPermissions(this, LOCATION_PERMISSION_REQUEST_CODE, true);
    }

    @Override
    public void onLocationUpdate(double latitude, double longitude) {

        this.location = new LatLng(latitude, longitude);
        binding.latitude.setText(String.valueOf(latitude));
        binding.longitude.setText(String.valueOf(longitude));
        updateMarker();
    }

    public void saveLocation(){
        try {

            Geocoder geo = new Geocoder(MainActivity.this.getApplicationContext());
            List<Address> addresses = geo.getFromLocation(Double.parseDouble(binding.latitude.getText().toString()), Double.parseDouble(binding.longitude.getText().toString()), 1);
            if (addresses.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Localização não encontrada", Toast.LENGTH_SHORT).show();
            }
            else {
                if (addresses.size() > 0) {
//                    Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_SHORT).show();
                    this.regions.add(new Region(addresses.get(0).getFeatureName(),
                            Double.parseDouble(binding.latitude.getText().toString()),
                            Double.parseDouble(binding.longitude.getText().toString())));
                    Log.i("ListaGeo", "" + this.regions.size());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}