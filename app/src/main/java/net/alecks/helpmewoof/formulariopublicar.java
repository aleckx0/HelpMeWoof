package net.alecks.helpmewoof;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class formulariopublicar extends AppCompatActivity {

    //Referencias a nuestros componenetes
    ImageView imagen;
    Button btnGPS;
    TextView txt_gps_prueba;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        //Se instancias el boton y el text view esto solo es ejemplo se supone debe instanciarse en el formulario para mostrar
        txt_gps_prueba = (TextView)findViewById(R.id.txt_gps_prueba);
        btnGPS = (Button)findViewById(R.id.btn4p);
        //metodo onclicklistener para el boton esta vez desde la clase no en el boton
        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //se obtienen las referencias del sistema de localizacion manager
                LocationManager locationManager = (LocationManager) formulariopublicar.this.getSystemService(Context.LOCATION_SERVICE);
                //Define a listener that responds to location updates
                LocationListener locationListener = new LocationListener() {
                    public void onLocationChanged(Location location) {
                        //called when a new location is found by the network location provider
                        //crea un objeto locationmanager con un locationlistener que cuando cambie la ubicacion, estatus y cuando el provedor
                        //este habilidato o desabilitado el provedor de ubicacion
                        txt_gps_prueba.setText(""+location.getLatitude()+" "+location.getLongitude());
                    }

                    public void onStatusChanged(String provider, int status, Bundle extras) {}

                    public void onProviderEnabled(String provider) {}

                    public void onProviderDisabled(String provider) {}
                };
                //Register the listener with the Location Manager to receive location updates
                int permissionCheck = ContextCompat.checkSelfPermission(formulariopublicar.this, Manifest.permission.ACCESS_FINE_LOCATION);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        });
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        imagen= (ImageView) findViewById(R.id.image_carga);
    }
    public void cargarimag(View view) {
        cargarImagen(); //ya teniendo el intent hacemos que al dar click en este boton se cargue un metodo cargar imagen que esta mas abajo
    }
    public void abrircam(View view) {
        abrirCamara();
    }
    private void cargarImagen(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/"); //le indicamos el tipo de archivo que se subira, seria tipo image
        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicacion"),10);
        //iniciamos el intent con startactivityforresult y si tenemos mas de una aplicacion para ver imagenes creamos un createchooser para
        //seleccionar la app de donde ver y elegir la imagen
    }
    private void abrirCamara(){
        //pendiente
    }
    //tenemos que sobreescribir el metodo con onAct
    //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            Uri path=data.getData();
            imagen.setImageURI(path);
        }
    }
}