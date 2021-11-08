package net.alecks.helpmewoof.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import net.alecks.helpmewoof.Adapters.CommentAdapter;
import net.alecks.helpmewoof.Modelos.Comentario;
import net.alecks.helpmewoof.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  Reportes extends AppCompatActivity {

    TextView textViewEstado;
    TextView textViewClasificacion1;
    TextView textViewClasificacion2;
    TextView textViewClasificacion3;
    TextView textViewDescripción;
    ImageView imageView;
    Button eliminar;
    Switch switchEstado;
    EditText editTextComentario;
    Button publicarComentario;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    RecyclerView recyclerViewComentario;
    CommentAdapter commentAdapter;
    String idReporte = "-MnHpDg_I3P6ln6cjK32" ;
    List<Comentario> listaComentario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);

        textViewEstado = (TextView) findViewById(R.id.textView1);
        textViewClasificacion1 = (TextView) findViewById(R.id.textView2);
        textViewClasificacion2 = (TextView) findViewById(R.id.textView3);
        textViewClasificacion3 = (TextView) findViewById(R.id.textView4);
        textViewDescripción = (TextView) findViewById(R.id.textView5);
        imageView = (ImageView) findViewById(R.id.imageView);
        publicarComentario = findViewById(R.id.button2);
        editTextComentario = findViewById(R.id.editTexComentario);
        recyclerViewComentario = findViewById(R.id.rv_comment);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();
        Map<String, Object> datosComentarios = new HashMap<>();//
        //Reporte reporte = new Reporte();
        //String idReporte = reporte.getIdReporte();


        databaseReference.child("Reportes").child(idReporte).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //Se obtienen los datos de firebase
                    ArrayList clasificación = new ArrayList();
                    long clasificaciónTamaño = snapshot.child("clasificación").getChildrenCount();
                    for (int i = 0; i < clasificaciónTamaño; i++ ){
                        String j = String.valueOf(i);
                        clasificación.add(snapshot.child("clasificación").child(j).getValue().toString());
                    }
                    String descripcion = snapshot.child("descripción").getValue().toString();
                    String estado = snapshot.child("estado").getValue().toString();
                    String imagen = "";
                    if  (snapshot.hasChild("imagen")){
                        imagen = snapshot.child("imagen").getValue().toString();
                    }
                    //Se muestran los datos obtenidos
                    textViewEstado.setText("Reporte " + estado);
                    for (int i = 0; i < clasificación.size(); i++){
                        switch  (i){
                            case 0:
                                textViewClasificacion1.setText("#"+clasificación.get(i).toString());
                            break;
                            case 1:
                                textViewClasificacion2.setText("#"+clasificación.get(i).toString());
                            break;
                            case 2:
                                textViewClasificacion3.setText("#"+clasificación.get(i).toString());
                            break;
                        }
                    }
                    textViewDescripción.setText(descripcion);
                    if(imagen != ""){
                        Picasso.get()
                                .load(imagen)
                                .placeholder(R.drawable.fondogyp)
                                .error(R.drawable.image)
                                .into(imageView)
                        ;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //Iniciar recycler view
        iniciarRecyclerViewComentario();


        //Menotodos click lisener
        publicarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Se comprueba que el comentario no este vacio
                boolean editTextVacio = editTextComentario.getText().toString().equals("");
                if (!editTextVacio){
                    //Obtenemos la información de descripción
                    String comentario =  editTextComentario.getText().toString();
                    datosComentarios.put("comentario",comentario);
                    //databaseReference.child("comentarios").push().setValue(datosComentarios);
                    databaseReference.child("Reportes").child(idReporte).child("comentarios").push().setValue(datosComentarios);
                    Toast.makeText(Reportes.this, "Comentario creado correctamente", Toast.LENGTH_SHORT).show();
                    editTextComentario.setText("");
                    datosComentarios.clear();

                }else{
                    Toast.makeText(Reportes.this, "Es necesario escribir un comentario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void iniciarRecyclerViewComentario() {
        recyclerViewComentario.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference comentarioReferencia = firebaseDatabase.getReference("Reportes").child(idReporte).child("comentarios");
        comentarioReferencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaComentario = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()){
                    Comentario comentario = snap.getValue(Comentario.class);
                    listaComentario.add(comentario);

                }
                commentAdapter = new CommentAdapter(getApplicationContext(),listaComentario);
                recyclerViewComentario.setAdapter(commentAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    //Método para mostrar y ocultar el menú
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.overflow, menu);
        return true;
    }
    //Método para asignar las funciones correspondientes a las opciones
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.item1){
            Toast.makeText(this, "Reporte marcado como resuelto correctamente!", Toast.LENGTH_SHORT).show();
            //Desactivar creación de comentarios
            editTextComentario.setEnabled(false);
            editTextComentario.setHint("Discusión terminada");
            //editTextComentario.setVisibility();
            publicarComentario.setEnabled(false);

        }else if (id == R.id.item2){
            Toast.makeText(this, "Reporte eliminado", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}