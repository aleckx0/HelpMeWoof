package net.alecks.helpmewoof.ui.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.alecks.helpmewoof.MapsPojo;
import net.alecks.helpmewoof.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsFragment extends Fragment {
    private GoogleMap mMap;
    //private ActivityPrincipalBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    DatabaseReference databaseReference;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
    private ArrayList<Marker> tmpRealtimeMarker = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    private void UploadData() {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), location -> {
                    // Got last known location. In some rare situations this can be null.
                    Log.e("Antes", "Se ecuentra antes");
                    if (location != null) {
                        // Logic to handle location object
                        Log.e("Latitud: ", +location.getLatitude()
                                + "Longitud: " + location.getLongitude());

                        Map<String, Object> latlong = new HashMap<>();
                        latlong.put("Latitud", location.getLatitude());
                        latlong.put("Longitud", location.getLongitude());
                        databaseReference.child("test").push().setValue(latlong);
                        Log.e("Después", "Se ecuentra después");
                        LatLng lng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lng, 17.0f));
                    }
                });
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                databaseReference.child("test")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (Marker marker : realTimeMarkers) {
                            marker.remove();
                        }

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            MapsPojo mp = dataSnapshot.getValue(MapsPojo.class);
                            Double Latitud = mp.getLatitud();
                            Double Longitud = mp.getLongitud();
                            MarkerOptions markerOptions = new MarkerOptions();

                            Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.location);
                            Bitmap smallimg = Bitmap.createScaledBitmap(img, 150, 150, false);
                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallimg);

                            markerOptions.position(new LatLng(Latitud, Longitud)).icon(bitmapDescriptor);

                            tmpRealtimeMarker.add(mMap.addMarker(markerOptions));
                        }
                        realTimeMarkers.clear();
                        realTimeMarkers.addAll(tmpRealtimeMarker);
                        //CountDownTimer();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }


                });

            }
    };



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            databaseReference = FirebaseDatabase.getInstance().getReference();
            UploadData();
        }

    }
}