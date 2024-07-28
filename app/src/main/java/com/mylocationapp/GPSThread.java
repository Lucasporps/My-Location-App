package com.mylocationapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.Priority;

public class GPSThread extends Thread {

    private final Context context;
    private final GPSCallback callback;
    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationRequest mLocationRequest;
    private boolean state = false;

    public GPSThread(Context context, GPSCallback callback) {
        this.context = context;
        this.callback = callback;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setIntervalMillis(500)                         // Intervalo dos updates
//                .setMinUpdateIntervalMillis(250)                // Sets the fastest allowed interval of location updates.
//                .setWaitForAccurateLocation(true)               // Want Accurate location updates make it true or you get approximate updates
//                .setMaxUpdateDelayMillis(100)                   // Sets the longest a location update may be delayed.
                .build();
    }

    @Override
    public void run() {
        // Check de permissão
        if (ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Log.i("GPSThread", "Passou da verificação"); // Log para acompanhar se a thread iniciou

        // Callback para usar no fused location client, vai notificar o callback da main activity atualizando as informações
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()){
                    if (state){
                        callback.onLocationUpdate(location.getLatitude(), location.getLongitude());
                    }
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper());
    }

    // Interface de callback com a main activity
    public interface GPSCallback{

        void onLocationUpdate(double latitude, double longitude);
    }

    // Iniciar localização em tempo real
    public void startGPS (){
        this.state = true;
        if(this.getState() == State.NEW) {
            this.start();
        }
    }

    // Parar localização em tempo real
    public void stopGPS (){
        this.state = false;
        this.interrupt();
    }

    // Verificação de estado da thread
    public boolean isRunning(){
        return this.state;
    }

}
