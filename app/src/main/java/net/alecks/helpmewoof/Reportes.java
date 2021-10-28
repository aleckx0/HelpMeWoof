package net.alecks.helpmewoof;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import id.zelory.compressor.Compressor;


public class Reportes extends AppCompatActivity {
    final int código_galería = 10;
    final int código_cámara = 20;
    Button crearReporte;
    Button abrirCámara;
    Button abrirGaleria;
    Button quitarImagen;
    EditText editTextDescripción;
    CheckBox checkHerido;
    CheckBox checkSituaciónCalle;
    CheckBox checkPerdido;
    ImageView imagen;
    Uri imgUri = null;
    Bitmap imgBitmap = null;
    byte [] imgByte;
    String ruta;
    DatabaseReference databaseReference;
    DatabaseReference imagenReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);
        imagen = (ImageView) findViewById (R.id.imageView);
        crearReporte = findViewById(R.id.button);
        abrirCámara = findViewById(R.id.button2);
        abrirGaleria = findViewById(R.id.button3);
        quitarImagen = findViewById(R.id.button4);

        editTextDescripción = findViewById(R.id.editTextTextMultiLine);

        checkHerido = findViewById(R.id.checkBox);
        checkSituaciónCalle = findViewById(R.id.checkBox2);
        checkPerdido = findViewById(R.id.checkBox3);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        imagenReference = FirebaseDatabase.getInstance().getReference().child("imagenes_subidas");
        //storageReference = FirebaseStorage.getInstance().getReference().child("imagen_comprimida");

        if (ContextCompat.checkSelfPermission(Reportes.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Reportes.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Reportes.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1000);
        }

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
                imagen.setImageResource(R.drawable.image);
            }
        });
        crearReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descripción =  editTextDescripción.getText().toString();
                ArrayList<String> clasificación = new ArrayList<String>();
                String estado = "Activo";


                if  (checkHerido.isChecked()){
                    clasificación.add(checkHerido.getText().toString());
                }
                if  (checkSituaciónCalle.isChecked()){
                    clasificación.add(checkSituaciónCalle.getText().toString());
                }
                if  (checkPerdido.isChecked()){
                    clasificación.add(checkPerdido.getText().toString());
                }
                //List clasificaciónList = new ArrayList<String>(Arrays.asList(clasificación));

                Map<String, Object> datosReportes = new HashMap<>();

                datosReportes.put("descripción",descripción);
                datosReportes.put("clasificación",clasificación);
                if  (!imgBitmap.equals(null)){
                    StorageReference ref = storageReference.child(ruta);
                    UploadTask uploadTask = ref.putBytes(imgByte);
                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw Objects.requireNonNull(task.getException());
                            }
                            return ref.getDownloadUrl();

                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri downloaduri = task.getResult();
                            datosReportes.put("imagen", downloaduri);
                        }
                    });
                }
                datosReportes.put("estado",estado);
                databaseReference.child("Reportes").push().setValue(datosReportes);

            }
        });


    }

    private void camara(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null){
            File fotoArchivo = null;
            try {
                fotoArchivo = guardarImagen();
            }catch (IOException ex){
                Log.e("error", ex.toString());
            }
            if (fotoArchivo != null) {
                Uri uri = FileProvider.getUriForFile(this, "net.alecks.helpmewoof.fileprovider", fotoArchivo);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(intent, código_cámara);
            }
        }
    }

    private File guardarImagen() throws IOException{
        String nombreFoto = "foto_";
        File directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File foto = File.createTempFile(nombreFoto, ".jpg", directorio);
        ruta = foto.getAbsolutePath();
        return foto;
    }

    private void galeria () {
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
                    imgUri = data.getData();//
                    ruta = imgUri.getPath();//

                    File url = new File(ruta);
                    try {
                        imgBitmap = new Compressor(this)
                                .setMaxWidth(640)
                                .setMaxHeight(480)
                                .setQuality(90)
                                .compressToBitmap(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    imgBitmap.compress(Bitmap.CompressFormat.JPEG,90,byteArrayOutputStream);
                    imgByte = byteArrayOutputStream.toByteArray();
                    imagen.setImageURI(imgUri);
                    break;
                case código_cámara:
                    //Bundle extras = data.getExtras();
                    //Bitmap imgBitmap = (Bitmap) extras.get("data");

                    imgBitmap = BitmapFactory.decodeFile(ruta);
                    System.out.println("ruta: "+ruta);
                    int height = (imgBitmap.getHeight() * 512 / imgBitmap.getWidth());
                    Bitmap scale = Bitmap.createScaledBitmap(imgBitmap, 512, height, true);
                    try {
                        ExifInterface exif = new ExifInterface(ruta);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
                        Matrix matrix = new Matrix();
                        switch  (orientation){
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                matrix.postRotate(90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                matrix.postRotate(180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                matrix.postRotate(270);
                                break;
                        }
                        Bitmap rotateBitmap = Bitmap.createBitmap(scale,0,0,scale.getWidth(),scale.getHeight(), matrix, true);
                        imagen.setImageBitmap(rotateBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    break;
            }
       }
    }
}