package com.xiaoyun.fang;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;
import androidx.wear.widget.SwipeDismissFrameLayout;

import com.google.android.gms.maps.model.Polyline;
import com.xiaoyun.fang.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    /// Location pins that will represents the start, aim and green center pins.
    private static Marker greenCenterPin, greenCenterPinFlag, teeboxPin;
    private static Marker aimPin;

    /// Distance pins that will represents the distances between that location markers.
    private Marker startCenterLabelPin, centerEndLabelPin;

    /// Lines that will represents the lines that connect the location markers.
    private Polyline startToCenterLine, centerToEndLine, shotTrackerLine;

    private ActivityResultLauncher<String[]> locationPermissionRequest;

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final SwipeDismissFrameLayout swipeDismissRootFrameLayout =
                binding.swipeDismissRootContainer;
        final FrameLayout mapFrameLayout = binding.mapContainer;

        swipeDismissRootFrameLayout.addCallback(new SwipeDismissFrameLayout.Callback() {
            @Override
            public void onDismissed(SwipeDismissFrameLayout layout) {
                layout.setVisibility(View.GONE);
                finish();
            }
        });

        swipeDismissRootFrameLayout.setOnApplyWindowInsetsListener(
                (view, insets) -> {
                    insets = swipeDismissRootFrameLayout.onApplyWindowInsets(insets);

                    FrameLayout.LayoutParams params =
                            (FrameLayout.LayoutParams) mapFrameLayout.getLayoutParams();

                    // Sets Wearable insets to FrameLayout container holding map as margins
                    params.setMargins(
                            insets.getSystemWindowInsetLeft(),
                            insets.getSystemWindowInsetTop(),
                            insets.getSystemWindowInsetRight(),
                            insets.getSystemWindowInsetBottom());
                    mapFrameLayout.setLayoutParams(params);

                    return insets;
                });

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                    Boolean backgroundLocationGranted = result.getOrDefault(
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else if (backgroundLocationGranted != null && backgroundLocationGranted){
                                // No location access granted.
                            } else {

                            }
                        }
                );


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Map is ready to be used.
        mMap = googleMap;

        // Inform user how to close app (Swipe-To-Close).
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(getApplicationContext(), R.string.intro_text, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        // Adds a marker in Sydney, Australia and moves the camera.
        LatLng sydney = new LatLng(35.8742, 139.7858);
        createGreenCenterPin();
        createTeeBox();
        createAimPin();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {


    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    private void createGreenCenterPin(){
        greenCenterPin = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(35.87434, 139.7858)));
        greenCenterPinFlag = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(35.87434, 139.7858)));
        greenCenterPin.setTitle("Green Center");
        greenCenterPinFlag.setTitle("Green Center");
        greenCenterPin.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.greencenter_small));
        Bitmap flag=BitmapFactory.decodeResource(getResources(),
                R.drawable.flaghole);
        Bitmap resize=Bitmap.createScaledBitmap(flag, 20, 48, false);
        BitmapDescriptor bitmapDescriptor=BitmapDescriptorFactory.fromBitmap(resize);
        greenCenterPinFlag.setIcon(bitmapDescriptor);
        greenCenterPin.setZIndex(3);
        greenCenterPinFlag.setZIndex(4);
        greenCenterPin.setFlat(true);
        greenCenterPin.setAnchor(0.5f, 0.5f);
        greenCenterPinFlag.setAnchor(0f, 1f);
    }

    private void createTeeBox(){
        teeboxPin = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(35.8740, 139.7858)));

        teeboxPin.setZIndex(2);
        teeboxPin.setFlat(true);
        teeboxPin.setAnchor(0.5f, 0.5f);
        teeboxPin.setInfoWindowAnchor(0.5f, 0.5f);
        teeboxPin.setTitle("Tee Box");
        Bitmap flag=BitmapFactory.decodeResource(getResources(), R.drawable.reddot_small);
        Bitmap resize=Bitmap.createScaledBitmap(flag, 24, 24, false);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resize);
        teeboxPin.setIcon(bitmapDescriptor);
    }

    private void createAimPin(){
        // init
        aimPin = mMap.addMarker(new MarkerOptions().position(new LatLng(35.8742, 139.7858)));

        // Setting up
        aimPin.setTitle("Aim Pin");
        Bitmap flag=BitmapFactory.decodeResource(getResources(), R.drawable.map_aim_point_icon);
        Bitmap resize=Bitmap.createScaledBitmap(flag, 30, 30, false);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(resize);
        aimPin.setIcon(bitmapDescriptor);
        aimPin.setZIndex(1);
        aimPin.setFlat(true);
        aimPin.setAnchor(0.5f, 0.5f);
        aimPin.setInfoWindowAnchor(0.5f, 0.5f);
        aimPin.setTag("aimPin");
        aimPin.setDraggable(true);
    }
}