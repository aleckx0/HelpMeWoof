package net.alecks.helpmewoof;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.Manifest;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class formulariopublicar extends AppCompatActivity {
    //Variables de los elementos del activity
    EditText editTextDescripción;
    TextView txt_gps_prueba;
    CheckBox checkHerido;
    CheckBox checkSituaciónCalle;
    CheckBox checkPerdido;
    ImageView imagen;
    Button abrirCámara;
    Button abrirGaleria;
    Button quitarImagen;
    Button crearReporte;
    //Variables para cargar a firebase
    final int código_galería = 10;
    final int código_cámara = 20;
    DatabaseReference databaseReference;
    Uri imgUri;
    String linkImagen;
    String nombreImagenCamara;
    boolean conImagen;
    ArrayList ubicacion = new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);
        //Variables de los elementos del activity
        editTextDescripción = findViewById(R.id.txt_descrip);
        txt_gps_prueba = (TextView)findViewById(R.id.txt_gps_prueba);
        checkHerido = findViewById(R.id.rbtn3);
        checkSituaciónCalle = findViewById(R.id.rbtn2);
        checkPerdido = findViewById(R.id.rbtn1);
        imagen = (ImageView) findViewById (R.id.image_carga);
        abrirCámara = findViewById(R.id.btn1);
        abrirGaleria = findViewById(R.id.btn2);
        quitarImagen = findViewById(R.id.btn3);
        crearReporte = findViewById(R.id.btn4p);
        //Variables para cargar a firebase
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> datosReportes = new HashMap<>();
        //Solicita permisos para el gps
        int permissionCheck = ContextCompat.checkSelfPermission(formulariopublicar.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(formulariopublicar.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(formulariopublicar.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        //Solicita los permisos para las imagenes
        if (ContextCompat.checkSelfPermission(formulariopublicar.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(formulariopublicar.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(formulariopublicar.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);
        }
        // Generamos la ubicación
        ubicación();
        abrirCámara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camara();

            }
        });
        abrirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galeria();
            }
        });
        quitarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conImagen = false;
                imagen.setImageResource(R.drawable.fondogyp);
            }
        });
        crearReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean editTextVacio = editTextDescripción.getText().toString().equals("");
                if  (!editTextVacio) {
                    if (checkHerido.isChecked() || checkSituaciónCalle.isChecked() || checkPerdido.isChecked()){
                        //Se asigna el estado del reporte a Activo
                        String estado = "Activo";
                        //Obtenemos la información de descripción
                        String descripción =  editTextDescripción.getText().toString();
                        //Creamos un arraylist y guardamos las clasificaciónes seleccionadas
                        ArrayList clasificación = new ArrayList();
                        if  (checkHerido.isChecked()){
                            clasificación.add(checkHerido.getText().toString());
                        }
                        if  (checkSituaciónCalle.isChecked()){
                            clasificación.add(checkSituaciónCalle.getText().toString());
                        }
                        if  (checkPerdido.isChecked()){
                            clasificación.add(checkPerdido.getText().toString());
                        }
                        //Enviar datos de un Hashmap a firebase
                        datosReportes.put("clasificación",clasificación);
                        datosReportes.put("descripción",descripción);
                        datosReportes.put("estado",estado);
                        datosReportes.put("ubicación",ubicacion);
                        //Comprobamos si hay imagen para agregarla al Hashmap
                        if (conImagen) {
                            datosReportes.put("imagen", linkImagen);
                        }
                        databaseReference.child("Reportes").push().setValue(datosReportes);
                        Toast.makeText(formulariopublicar.this, "Reporte creado correctamente", Toast.LENGTH_SHORT).show();
                        datosReportes.clear();
                    }else{
                        Toast.makeText(formulariopublicar.this, "Es necesario marcar al menos una casilla", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(formulariopublicar.this, "Es necesario agregar una descripción", Toast.LENGTH_SHORT).show();
                }
                //Aqui termina el proceso, debe regresar a pantalla principal y limpiar el formulario

                editTextDescripción.setText("");
                if(checkHerido.isChecked()){
                    checkHerido.toggle();
                }
                if(checkSituaciónCalle.isChecked()){
                    checkSituaciónCalle.toggle();
                }
                if(checkPerdido.isChecked()){
                    checkPerdido.toggle();
                }
                imagen.setImageResource(R.drawable.fondogyp);
                txt_gps_prueba.setText("");
                conImagen = false;

            }
        });
    }
    private void ubicación (){
        //Codigo de GPS
        //ArrayList ubicacion = new ArrayList();
        //se obtienen las referencias del sistema de localizacion manager
        LocationManager locationManager = (LocationManager) formulariopublicar.this.getSystemService(Context.LOCATION_SERVICE);
        //Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //crea un objeto locationmanager con un locationlistener que cuando cambie la ubicacion, estatus y cuando el provedor
                //este habilidato o desabilitado el provedor de ubicacion
                txt_gps_prueba.setText(""+location.getLatitude()+" "+location.getLongitude());
                Double latitud = 0.0;
                Double longitud = 0.0;
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                ubicacion.add(latitud);
                ubicacion.add(longitud);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
        int permissionCheck = ContextCompat.checkSelfPermission(formulariopublicar.this, Manifest.permission.ACCESS_FINE_LOCATION);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }
    private void camara(){
        //Abre la camara
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Crear nombre de la imagen con informacion del tiempo
        nombreImagenCamara = (System.currentTimeMillis()/1000+".jpg");
        //Guardar archivo temporal
        File fotoArchivo = new File(getExternalFilesDir(null),"imagen_"+nombreImagenCamara);
        imgUri = FileProvider.getUriForFile(this,"net.alecks.helpmewoof.fileprovider", fotoArchivo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
        startActivityForResult(intent,código_cámara);
    }
    private void galeria () {
        //Abre la galería y llama al activity result
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),código_galería);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            switch (requestCode) {
                case código_galería:
                    conImagen = true;
                    //Se obtiene la imagen seleccionada
                    imgUri = data.getData();
                    //Indicamos que las imagenes se guadaran en la carpeta ImagenesReportes
                    StorageReference nombreFolder = FirebaseStorage.getInstance().getReference().child("ImagenesReportes");
                    //Se establece el nombre de la imagen
                    StorageReference nombreImagen = nombreFolder.child("imagen"+imgUri.getLastPathSegment() + System.currentTimeMillis()/1000);
                    //obtiene el link de la imagen
                    nombreImagen.putFile(imgUri).addOnSuccessListener(taskSnapshot -> nombreImagen.getDownloadUrl().addOnSuccessListener(imgUri -> {
                        linkImagen = String.valueOf(imgUri);
                    }));
                    //Se muestra la imagen en el imageview
                    imagen.setImageURI(imgUri);
                    break;
                case código_cámara:
                    conImagen = true;
                    Bitmap imgBitmap = BitmapFactory.decodeFile(getExternalFilesDir(null) + "/imagen_"+nombreImagenCamara);
                    nombreFolder = FirebaseStorage.getInstance().getReference().child("ImagenesReportes");
                    nombreImagen = nombreFolder.child(imgUri.getLastPathSegment());
                    nombreImagen.putFile(imgUri).addOnSuccessListener(taskSnapshot -> nombreImagen.getDownloadUrl().addOnSuccessListener(imgUri -> {
                        linkImagen = String.valueOf(imgUri);
                    }));
                    imagen.setImageBitmap(imgBitmap);
                    break;
            }
        }
    }
}