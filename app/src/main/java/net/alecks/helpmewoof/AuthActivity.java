package net.alecks.helpmewoof;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class AuthActivity extends AppCompatActivity {
    String permisos[] = {Manifest.permission.CAMERA,
                         Manifest.permission.ACCESS_COARSE_LOCATION,
                         Manifest.permission.READ_EXTERNAL_STORAGE,
                         Manifest.permission.INTERNET};

    DrawerLayout drawerLayout;

    NotificationCompat.Builder notificacion;

    private final int idn=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        requestPermissions(permisos, 1);

        elementosBarra();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    //crea los elementos de la barra (nombre y boton)
    public void elementosBarra(){

        setSupportActionBar(findViewById(R.id.toolbar));

        drawerLayout = findViewById(R.id.main);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.toolbar), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /*
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            if (requestCode == 1){
                for (int i = 0; i < permisos.length; i++) {
                    if(grantResults[i]== PackageManager.PERMISSION_GRANTED){

                    }
                }
            }
        }
    */
    public void iriniciar(View view){
        Intent i = new Intent(this, IniciarSesionActivity.class);
        startActivity(i);
    }

    public void registrar(View view){
        Intent i = new Intent(this, RegistrarseActivity.class);
        startActivity(i);

    }

    @Override
    public void onBackPressed() {
        drawerLayout = findViewById(R.id.main);

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void notificacion(View view){
        Toast.makeText(this, "gg se pauso", Toast.LENGTH_SHORT).show();
        NotificationChannel canal= new NotificationChannel("canal", "nuevo", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.createNotificationChannel(canal);

        notificacion = new NotificationCompat.Builder(this, "canal");
        notificacion.setSmallIcon(R.drawable.notificacion);
        notificacion.setContentTitle("Notificacion de Prueba");
        notificacion.setContentText("La aplicacion sigue corriendo de fondo");

        //notificacion.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent i= new Intent(this, AuthActivity.class);
        TaskStackBuilder sb= TaskStackBuilder.create(this);
        sb.addParentStack(AuthActivity.class);
        sb.addNextIntent(i);

        PendingIntent pi= sb.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);

        notificacion.setContentIntent(pi);

        NotificationManagerCompat mc= NotificationManagerCompat.from(getApplicationContext());
        mc.notify(1, notificacion.build());
    }

}