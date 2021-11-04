package net.alecks.helpmewoof;


import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Reportes extends AppCompatActivity {

    private TextView mTextView2;
    private TextView mTextViewEstado;
    private TextView mTextViewClasificacion;
    private ImageView mTextViewImagen;

    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);

        mTextView2 = (TextView) findViewById(R.id.textView2);
        mTextViewEstado = (TextView) findViewById(R.id.textViewEstado);
        mTextViewClasificacion = (TextView) findViewById(R.id.textViewClasificacion);
        mTextViewImagen = (ImageView) findViewById(R.id.textViewImagen);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Reportes").child("-MnHpDg_I3P6ln6cjK32").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    String descripcion = snapshot.child("descripción").getValue().toString();
                    String estado = snapshot.child("estado").getValue().toString();
                    String clasificacion = snapshot.child("clasificacion").getValue().toString();
                    int imagen = Integer.parseInt(snapshot.child("imagen").getValue().toString());

                    mTextView2.setText("La descripción es: " + descripcion);
                    mTextViewEstado.setText("El estado es: " + estado);
                    mTextViewClasificacion.setText("La clasificacion es: " + clasificacion);
                    mTextViewImagen.setImageAlpha(Integer.parseInt("La imagen es: " + imagen));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }










}