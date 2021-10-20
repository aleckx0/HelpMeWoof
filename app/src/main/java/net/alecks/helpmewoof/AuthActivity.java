package net.alecks.helpmewoof;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

    }


    public void iriniciar(View view){
        Intent i = new Intent(this, IniciarSesionActivity.class);
        startActivity(i);
    }

    public void registrar(View view){
        Intent i = new Intent(this, RegistrarseActivity.class);
        startActivity(i);

    }

}