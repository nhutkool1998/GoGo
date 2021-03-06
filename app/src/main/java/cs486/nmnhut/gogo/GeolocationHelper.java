package cs486.nmnhut.gogo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeolocationHelper {

    private static LocationRequest locationRequest;
    private static LocationCallback mLocationCallback;
    private static String Username = "";
    public static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1000;
    private FusedLocationProviderClient mFusedLocationClient;
    static LatLng currentPosition = new LatLng(0, 0);
    Activity mActivity;
    GeolocationHelper(Activity activity, String username) {
        this.mActivity = activity;
        Username = username;
        locationRequest = new LocationRequest();
        locationRequest.setInterval(600);
        locationRequest.setFastestInterval(300);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(((Context) activity));

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {

                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    String s = "position/" + GeolocationHelper.Username;
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference ref = db.getReference(s);
                    ref.child("lat").setValue(location.getLatitude());
                    ref.child("lng").setValue(location.getLongitude());
                }
            }


        };


        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermission(activity);

        }



    }

    private void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_REQUEST_FINE_LOCATION);
    }



    public static LatLng getCurrentPosition() {
        return currentPosition;
    }

    @SuppressLint("MissingPermission")
    public void requestLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(mActivity);
        } else
            mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);

    }

    public String getUsername() {
        return Username;
    }

    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    public void stopLocationUpdate() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

}
