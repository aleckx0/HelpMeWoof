package net.alecks.helpmewoof.ui.maps;

import android.Manifest;
import android.content.Intent;
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

import net.alecks.helpmewoof.Activities.Reportes;
import net.alecks.helpmewoof.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsFragment extends Fragment{
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    DatabaseReference databaseReference;
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
    private ArrayList<Marker> tmpRealtimeMarker = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();

    private void UploadData() {

    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            //Pide permisos de ubicación
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                return;
            }
            //Enfoca el mapa en la ubicación actual
            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                if (location != null) {
                    LatLng lng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lng, 17.0f));
                }
            });
            //Carga los marcadores
            mMap = googleMap;
            databaseReference.child("Reportes").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        for (Marker marker : realTimeMarkers) {
                            marker.remove();
                        }
                        String latitud = snapshot.child(dataSnapshot.getKey()).child("coordenadas").child("Latitud").getValue().toString();
                        Double Latitud = Double.valueOf(latitud).doubleValue();
                        String longitud = snapshot.child(dataSnapshot.getKey()).child("coordenadas").child("Longitud").getValue().toString();
                        Double Longitud = Double.valueOf(longitud).doubleValue();

                        MarkerOptions markerOptions = new MarkerOptions();
                        Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.location);
                        Bitmap smallimg = Bitmap.createScaledBitmap(img, 150, 150, false);
                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(smallimg);

                        markerOptions.position(new LatLng(Latitud, Longitud)).icon(bitmapDescriptor).title(dataSnapshot.getKey());
                        tmpRealtimeMarker.add(mMap.addMarker(markerOptions));
                    }
                    realTimeMarkers.clear();
                    realTimeMarkers.addAll(tmpRealtimeMarker);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            //Al dar click al marcador de la ubicacion se abre la activity reportes y envia junto con el idReporte
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    String idReporte = marker.getTitle();
                    Intent i = new Intent (getActivity(),Reportes.class);
                    i.putExtra("idReporte",idReporte);
                    startActivity(i);
                    return false;
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