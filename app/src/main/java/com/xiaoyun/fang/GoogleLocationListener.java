package com.xiaoyun.fang;

import android.content.Context;
import android.location.Location;

import androidx.activity.result.ActivityResultLauncher;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

/**
 * 课题号（GORASA-XXXX)
 * 类或接口的描述信息
 *
 * @author xiaoyun.fangfenrir-inc.com.cn
 * @date 2022/1/18
 */
public class GoogleLocationListener {

    private LocationChangeInterface locationChangeInterface;
    private Context context;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    public GoogleLocationListener(Context context, LocationChangeInterface locationChangeInterface) {
        this.context = context;
        this.locationChangeInterface = locationChangeInterface;
        initObjects();
    }

    private void initObjects() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(15000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    locationChangeInterface.onNewLocationChanged(null);
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    locationChangeInterface.onNewLocationChanged(location);
                }
                //fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            }
        };
    }

    public FusedLocationProviderClient getFusedLocationProviderClient() {
        return fusedLocationProviderClient;
    }

    public LocationRequest getLocationRequest() {
        return locationRequest;
    }

    public LocationCallback getLocationCallback() {
        return locationCallback;
    }

    public interface LocationChangeInterface {
        void onNewLocationChanged(Location location);
    }
}
