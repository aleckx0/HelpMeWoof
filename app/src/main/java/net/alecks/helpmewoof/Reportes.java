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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import java.io.IOException;

public class Reportes extends AppCompatActivity {
    final int código_galería = 10;
    final int código_cámara = 20;
    Button abrirCámara;
    Button abrirGaleria;
    Button quitarImagen;
    ImageView imagen;
    String ruta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);
        imagen = (ImageView) findViewById (R.id.imageView);
        abrirCámara = findViewById(R.id.button2);
        abrirGaleria = findViewById(R.id.button3);
        quitarImagen = findViewById(R.id.button4);

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
                    Uri path1 = data.getData();
                    imagen.setImageURI(path1);
                    break;
                case código_cámara:
                    //Bundle extras = data.getExtras();
                    //Bitmap imgBitmap = (Bitmap) extras.get("data");

                    Bitmap imgBitmap = BitmapFactory.decodeFile(ruta);
                    System.out.println("ruta: "+ruta);
                    int height = (imgBitmap.getHeight() * 512 / imgBitmap.getWidth());
                    Bitmap scale = Bitmap.createScaledBitmap(imgBitmap, 512, height, true);
                    int rotate = 0;
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