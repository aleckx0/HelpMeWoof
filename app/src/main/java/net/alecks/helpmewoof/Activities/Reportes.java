package net.alecks.helpmewoof.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import net.alecks.helpmewoof.Adapters.CommentAdapter;
import net.alecks.helpmewoof.MainActivity;
import net.alecks.helpmewoof.Modelos.Comentario;
import net.alecks.helpmewoof.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  Reportes extends AppCompatActivity {
    //Componentes del activity
    TextView textViewEstado;
    TextView textViewClasificacion1;
    TextView textViewClasificacion2;
    TextView textViewClasificacion3;
    TextView textViewDescripción;
    ImageView imageView;
    Switch switchEstado;
    EditText editTextComentario;
    Button publicarComentario;
    RecyclerView recyclerViewComentario;
    //Adaptador de los comentarios
    CommentAdapter commentAdapter;
    //Base de datos firebase
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    //Variables
    String idReporte;
    List<Comentario> listaComentario;
    Map<String, Object> datosComentarios = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);

        //Componentes del activity
        textViewEstado = (TextView) findViewById(R.id.textView1);
        textViewClasificacion1 = (TextView) findViewById(R.id.textView2);
        textViewClasificacion2 = (TextView) findViewById(R.id.textView3);
        textViewClasificacion3 = (TextView) findViewById(R.id.textView4);
        textViewDescripción = (TextView) findViewById(R.id.textView5);
        imageView = (ImageView) findViewById(R.id.imageView);
        publicarComentario = findViewById(R.id.button2);
        editTextComentario = findViewById(R.id.editTexComentario);
        recyclerViewComentario = findViewById(R.id.rv_comment);

        //Base de datos firebase
        idReporte = getIntent().getStringExtra("idReporte");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //Iniciar recycler view para cargar los comentarios
        iniciarRecyclerViewComentario();

        //Muestra el reporte
        cargarReporte();

        //Comprobamos el nivel del usuario para mostrar menu toolbar
        if  (nivelUsuario().equals("Administrador")){
            //Si es administrador muestra opción "Reporte resuelto" y "Eliminar reporte" y "Eliminar comentario"
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        }
        if  (nivelUsuario() != "Anonimo" & nivelUsuario() != "Administrador"){
            //Si el usuario está registrado No puede eliminar reporte o comentario
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);
        }

        //Método click lisener del botón comentar
        publicarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.carolinabr.tk/app.php";
                StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String value = jsonObject.getString("value");
                            if (value.equals("false")){
                                agregarComentario();
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(Reportes.this);
                                builder.setTitle("Comentario ofensivo").setMessage("Su comentario podría incluir palabras ofensivas o vulgares").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                })
                {

                    protected Map<String, String> getParams(){
                        Map<String, String> params = new HashMap<>();
                        boolean editTextVacio = editTextComentario.getText().toString().equals("");
                        if (!editTextVacio){
                            String comentario =  editTextComentario.getText().toString();
                            params.put("comentario", comentario);
                            return params;

                        }else{
                            //Toast.makeText(Reportes.this, "Es necesario escribir un comentario", Toast.LENGTH_SHORT).show();
                            return null;
                        }
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(Reportes.this);
                requestQueue.add(postRequest);
            }
        });
    }//final del método oncreate

    //Método que retorna el nivel del usuario (Sí es Administrador, Anonimo o Registrado)
    public String nivelUsuario (){
        ArrayList administradores = new ArrayList<>();
        String nivelUser = "";
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user.isAnonymous()){
            nivelUser = "Anonimo";
            return nivelUser;

        }else if (!user.isAnonymous()){
            //Si no es anonimo comprobamos si es administrador
            administradores.add("carolinabermudez99@gmail.com");
            administradores.add("email08@gmail.com");
            for (int i=0;i<administradores.size(); i++){
                if (user.getEmail().equals(administradores.get(i).toString())){
                    nivelUser = "Administrador";
                    return nivelUser;
                }
            }
        }
        if (!user.isAnonymous() & nivelUser != "Administrador"){
            //Si el nivel de usuario sigue estando vacio quiere decir que es solo un usuario registrado
            String email = user.getEmail();
            String[] parts = email.split("@");
            String nombreUsuario = parts[0];
            String dominioEmail = parts[1];
            nivelUser = nombreUsuario;
            return nivelUser;
        }
        return nivelUser;
    }

    //Método que muestra los detalles del reporte desde firebase
    private void cargarReporte(){
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
                    //Si el reporte esta resuelto se inhabilitan los comentarios
                    if  (estado.equals("Resuelto")){
                        editTextComentario.setEnabled(false);
                        editTextComentario.setHint("Discusión terminada");
                        publicarComentario.setEnabled(false);
                    }
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
    }
    //Método que muestra los comentarios de la base de datos
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
                commentAdapter = new CommentAdapter(Reportes.this,listaComentario, idReporte);
                recyclerViewComentario.setAdapter(commentAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    //Método para agregar un comentario
    private void agregarComentario(){
        //Se comprueba que el comentario no este vacio
        boolean editTextVacio = editTextComentario.getText().toString().equals("");
        if (!editTextVacio){
            //Obtenemos la información de comentario
            String comentario =  editTextComentario.getText().toString();
            String nivelUsuario = nivelUsuario();
            Comentario comentarioModelo = new Comentario(comentario,nivelUsuario);
            databaseReference.child("Reportes").child(idReporte).child("comentarios").push().setValue(comentarioModelo);
            Toast.makeText(Reportes.this, "Comentario creado correctamente", Toast.LENGTH_SHORT).show();
            editTextComentario.setText("");
            datosComentarios.clear();
        }else{
            Toast.makeText(Reportes.this, "Es necesario escribir un comentario", Toast.LENGTH_SHORT).show();
        }
    }
    //Método para mostrar y ocultar el menú overflow
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.overflow, menu);
        return true;
    }
    //Método donde se definen las acciones al dar click en un item del menú overflow
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == R.id.item1){
            //El reporte se marca como resuelto
            Map<String, Object> estadoActualizado = new HashMap<>();
            estadoActualizado.put("estado","Resuelto");
            databaseReference.child("Reportes").child(idReporte).updateChildren(estadoActualizado).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(Reportes.this, "Reporte marcado como resuelto correctamente!", Toast.LENGTH_SHORT).show();
                    //1,000 = 1s; 500,000 = 5min; 12,000,000 = 120min
                    CountDownTimer countDownTimer = new CountDownTimer(12000000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }
                        public void onFinish() {
                            databaseReference.child("Reportes").child(idReporte).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        if  (snapshot.child("imagen").exists()){
                                            String linkImagen = snapshot.child("imagen").getValue().toString();
                                            FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
                                            StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(linkImagen);
                                            photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {

                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            databaseReference.child("Reportes").child(idReporte).removeValue();
                            Intent i = new Intent (Reportes.this, MainActivity.class);
                            startActivity(i);
                        }
                    };
                    countDownTimer.start();
                }
            });

            //Desactivar creación de comentarios
            editTextComentario.setEnabled(false);
            editTextComentario.setHint("Discusión terminada");
            publicarComentario.setEnabled(false);
        }else if (id == R.id.item2){
            //El reporte se elimina
            AlertDialog.Builder builder = new AlertDialog.Builder(Reportes.this);
            builder.setTitle("Eliminar Reporte").setMessage("¿Seguro que quiere eliminar el reporte?").setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    databaseReference.child("Reportes").child(idReporte).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.child("imagen").exists()){
                                    String linkImagen = snapshot.child("imagen").getValue().toString();
                                    FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
                                    StorageReference photoRef = mFirebaseStorage.getReferenceFromUrl(linkImagen);
                                    photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                        }
                                    });
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    databaseReference.child("Reportes").child(idReporte).removeValue();
                    Toast.makeText(Reportes.this, "Reporte eliminado", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent (Reportes.this, MainActivity.class);
                    startActivity(i);
                }
            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {

        //Si el usuario esta registrado
        if  (nivelUsuario() != "Anonimo" & nivelUsuario() != "Administrador"){
            FirebaseAuth mAuth;
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            databaseReference.child("Reportes").child(idReporte).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String idUserCreador = snapshot.child("idUsuario").getValue().toString();
                        String opcionEliminar = snapshot.child("eliminar").getValue().toString();
                        if  (idUserCreador.equals(user.getUid()) & opcionEliminar.equals("true")){
                            //Si el usuario es el dueño del reporte se muestra la opcion de eliminar despues de 5 minutos de ser publicado
                            menu.getItem(1).setVisible(true);
                        }else{
                            //Si el usuario está registrado No puede eliminar reporte
                            menu.getItem(1).setVisible(false);//0 marcar resuelto - 1 Eliminar reporte
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        //Se comprueba si el reporte esta resuelto para no mostrar la opcion 0
        databaseReference.child("Reportes").child(idReporte).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String estado = snapshot.child("estado").getValue().toString();
                    if  (estado.equals("Resuelto")){
                        menu.getItem(0).setVisible(false);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return true;
    }
}