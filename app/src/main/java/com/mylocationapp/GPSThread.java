package com.mylocationapp;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class GPSThread extends Thread implements LocationListener{

    private Location mLocation;
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;

    private final Context context;
    private final GPSCallback callback;
    private final FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    //    private final LocationRequest locationRequest;
    public GPSThread(Context context, GPSCallback callback) {
        this.context = context;
        this.callback = callback;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
//        this.mLocationRequest = createLocationRequest();
        this.locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void run() {
        if (ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
//            this.googleMap.setMyLocationEnabled(true);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 30, this);
//        fusedLocationClient.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 30, this);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this.context,""+location.getLatitude()+", "+location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    public interface GPSCallback{

    }

}
