package apps.ahqmrf.mock.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import apps.ahqmrf.mock.util.Const;
import apps.ahqmrf.mock.util.Utility;

public class LocationUpdateService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private   GoogleApiClient mGoogleApiClient;
    private   LocationRequest mLocationRequest;
    protected Location        mCurrentLocation;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();


    public LocationUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        mLocationRequest.setSmallestDisplacement(10); // float

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Utility.put(this, Const.Keys.SERVICE_STARTED, false);
                return;
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Utility.put(this, Const.Keys.SERVICE_STARTED, false);
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Utility.put(this, Const.Keys.SERVICE_STARTED, false);
    }

    @Override
    public void onLocationChanged(Location location) {
        String username = Utility.getString(getApplicationContext(), Const.Keys.USERNAME);
        if (!TextUtils.isEmpty(username)) {
            DatabaseReference refLocation = database.getReference(Const.Route.LOCATION_REF).child(username);
            DatabaseReference refLat = refLocation.child(Const.Keys.LATITUDE);
            DatabaseReference refLng = refLocation.child(Const.Keys.LONGITUDE);
            refLat.setValue(location.getLatitude());
            refLng.setValue(location.getLongitude());
        }
    }
}
