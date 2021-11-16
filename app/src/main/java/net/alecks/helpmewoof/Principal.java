package net.alecks.helpmewoof;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.alecks.helpmewoof.databinding.ActivityPrincipalBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Principal extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private ActivityPrincipalBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    DatabaseReference databaseReference;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
    private Button btn_reporte;
    private ArrayList<Marker> tmpRealtimeMarker = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        UploadData();
        btn_reporte = findViewById(R.id.button_report);
        btn_reporte.setOnClickListener(this);
        countDownTimer();
    }

    private void countDownTimer(){
        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
                Log.e("Segundos restantes: ", "" + l / 1000);
            }

            @Override
            public void onFinish() {
                Toast.makeText(Principal.this, "Marcadores Actualizados", Toast.LENGTH_SHORT).show();
                onMapReady(mMap);
            }
        }
        .start();
    }

    private void UploadData() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Principal.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        Log.e("Latitud: ", +location.getLatitude()
                                + "Longitud: " + location.getLongitude());

                        Map<String, Object> latlong = new HashMap<>();
                        latlong.put("Latitud", location.getLatitude());
                        latlong.put("Longitud", location.getLongitude());
                        databaseReference.child("reportes").push().setValue(latlong);

                        LatLng lng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lng, 13.0f));
                    }
                });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        databaseReference.child("reportes").addListenerForSingleValueEvent(new ValueEventListener() {
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
                    markerOptions.position(new LatLng(Latitud, Longitud));

                    tmpRealtimeMarker.add(mMap.addMarker(markerOptions));
                }
                realTimeMarkers.clear();
                realTimeMarkers.addAll(tmpRealtimeMarker);
                countDownTimer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_report:
                Intent intent = new Intent(Principal.this, MenuLateral.class);
                startActivity(intent);
                break;
        }
    }
}


