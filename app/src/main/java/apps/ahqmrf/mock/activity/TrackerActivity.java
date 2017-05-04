package apps.ahqmrf.mock.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import apps.ahqmrf.mock.BaseActivity;
import apps.ahqmrf.mock.R;
import apps.ahqmrf.mock.User;
import apps.ahqmrf.mock.util.Const;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TrackerActivity extends BaseActivity implements OnMapReadyCallback {

    @BindView(R.id.layout_progress) View progressLayout;
    @BindView(R.id.container)       View layout;

    private GoogleMap mGoogleMap;
    private Marker    mCurrLocationMarker;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String thisUserName;
    private String thisUserFullName;
    private DatabaseReference location;
    private ValueEventListener eventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot != null) {
                double lat = (double)dataSnapshot.child(Const.Keys.LATITUDE).getValue();
                double lng = (double)dataSnapshot.child(Const.Keys.LONGITUDE).getValue();
                updateMarker(new LatLng(lat, lng));
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);
        ButterKnife.bind(this);
        User user = getIntent().getParcelableExtra(Const.Keys.USER);
        thisUserName = user.getUsername();
        thisUserFullName = user.getFullName();
        location = database.getReference(Const.Route.LOCATION_REF).child(thisUserName);
        setToolbarWithBackArrow();
    }

    @Override
    protected void setToolbarTitle() {
        setTitle(thisUserFullName);
    }

    @Override
    protected void onViewCreated() {
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        location.addValueEventListener(eventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        location.removeEventListener(eventListener);
    }

    private void updateMarker(LatLng latLng) {
        if (mCurrLocationMarker != null) {
            // Change marker position
            mCurrLocationMarker.setPosition(latLng);
            return;
        }

        //No marker added yet, add a marker and set current location to the marker

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(thisUserFullName);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);

        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (mGoogleMap == null) Log.e("", "");
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        progressLayout.setVisibility(View.GONE);
    }
}
