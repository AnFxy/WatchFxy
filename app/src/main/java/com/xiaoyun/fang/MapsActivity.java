package com.xiaoyun.fang;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;
import androidx.wear.widget.SwipeDismissFrameLayout;

import com.xiaoyun.fang.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleLocationListener.LocationChangeInterface {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

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
        LatLng sydney = new LatLng(0, 0);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        GoogleLocationListener listener = new GoogleLocationListener(MapsActivity.this, this);
        if (checkCoarseAndFineLocationPermission(MapsActivity.this)) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            // ユーザーの最後のアドレスを取得します。
            listener.getFusedLocationProviderClient().getLastLocation().addOnSuccessListener(location -> {
                this.onNewLocationChanged(location);
            });
            // ユーザがバックグラウンドアドレス権限をオンにすると、ユーザの所在地を更新し続けることができる。
            listener.getFusedLocationProviderClient().requestLocationUpdates(listener.getLocationRequest(), listener.getLocationCallback(), Looper.getMainLooper());
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                locationPermissionRequest.launch(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                });
            } else {
                locationPermissionRequest.launch(new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });
            }
        }
    }

    @Override
    public void onNewLocationChanged(Location location) {
        if (mMap != null && location != null) {
            LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.addMarker(new MarkerOptions().position(sydney).title("你所在的地方"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
    }

    public static boolean checkCoarseAndFineLocationPermission(Context context) {
        if (!isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION) &&
                !isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isPermissionGranted(Context context,String permission) {
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result = context.checkSelfPermission(permission);
        } else {
            result = PermissionChecker.checkSelfPermission(context, permission);
        }
        return result == PackageManager.PERMISSION_GRANTED;
    }

}