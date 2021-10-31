package net.alecks.helpmewoof;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;

import android.os.Bundle;

import android.view.Menu;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

public class AuthActivity extends AppCompatActivity {
    String permisos[] = {Manifest.permission.CAMERA,
                         Manifest.permission.ACCESS_COARSE_LOCATION,
                         Manifest.permission.READ_EXTERNAL_STORAGE,
                         Manifest.permission.INTERNET};

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        requestPermissions(permisos, 1);

        elementosBarra();

/*        NavigationView nav= findViewById(R.id.nav_menu);
        nav.getMenu().clear();
        nav.inflateMenu(R.menu.activity_main_drawer);*/
    }
    //crea los elementos de la barra (nombre y boton)
    public void elementosBarra(){

        setSupportActionBar(findViewById(R.id.barrin));

        drawerLayout = findViewById(R.id.main);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, findViewById(R.id.barrin), R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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



}