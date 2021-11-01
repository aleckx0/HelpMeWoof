package net.alecks.helpmewoof;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

public class formulariopublicar extends AppCompatActivity {

    //Referencias a nuestros componenetes
    ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

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
    private void cargarImagen(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/"); //le indicamos el tipo de archivo que se subira, seria tipo image
        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicacion"),10);
        //iniciamos el intent con startactivityforresult y si tenemos mas de una aplicacion para ver imagenes creamos un createchooser para
        //seleccionar la app de donde ver y elegir la imagen
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