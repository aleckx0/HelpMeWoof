package net.alecks.helpmewoof.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.alecks.helpmewoof.Activities.Reportes;
import net.alecks.helpmewoof.Modelos.Comentario;
import net.alecks.helpmewoof.R;
import net.alecks.helpmewoof.ui.maps.MapsFragment;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>  {

    private Context mContext;
    private List<Comentario> mData;
    private String idReporte;
    int selected_position = 0;

    public CommentAdapter(Context mContext, List<Comentario> mdata, String idReporte) {
        this.mContext = mContext;
        this.mData = mdata;
        this.idReporte = idReporte;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment,parent,false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.tv_nivelu.setText(mData.get(position).getNivelU());
        holder.tv_content.setText(mData.get(position).getComentario());
        holder.tv_date.setText(timestampToString((Long)mData.get(position).getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView tv_nivelu, tv_content, tv_date;

        public CommentViewHolder(View itemView){
            super (itemView);
            tv_nivelu = itemView.findViewById(R.id.textView6);
            tv_content = itemView.findViewById(R.id.textView);
            tv_date = itemView.findViewById(R.id.comment_date);
            //Comprobamos si el usuario es administrador para permitir la eliminación de comentarios
            Reportes reportes = new Reportes();

            if (reportes.nivelUsuario().equals("Administrador")){
                DatabaseReference databaseReference;
                databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Reportes").child(idReporte).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String estado = snapshot.child("estado").getValue().toString();
                        if (estado.equals("Activo")){
                            //Muestra menu alert dialog para eliminar el comentario
                            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    final CharSequence [] opciones = {"Eliminar comentario"};
                                    final AlertDialog.Builder alertOpciones = new AlertDialog.Builder(mContext);
                                    alertOpciones.setTitle("Seleccione una opción");
                                    alertOpciones.setItems(opciones, new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (opciones[which].equals("Eliminar comentario")){
                                                DatabaseReference databaseReference;
                                                databaseReference = FirebaseDatabase.getInstance().getReference();
                                                databaseReference.child("Reportes").child(idReporte).child("comentarios").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                            String comentarioDb = snapshot.child(dataSnapshot.getKey()).child("comentario").getValue().toString();
                                                            String niveluserDb = snapshot.child(dataSnapshot.getKey()).child("nivelU").getValue().toString();
                                                            String comentarioTextview = tv_content.getText().toString();
                                                            String nivelUTextview = tv_nivelu.getText().toString();
                                                            if (comentarioDb.equals(comentarioTextview) & niveluserDb.equals(nivelUTextview)){
                                                                databaseReference.child("Reportes").child(idReporte).child("comentarios").child(dataSnapshot.getKey()).removeValue();
                                                            }
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                                Toast.makeText(mContext, "Comentario eliminado correctamente", Toast.LENGTH_SHORT).show();
                                            }else{
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                    alertOpciones.show();
                                    return false;
                                }
                            });
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private String timestampToString (long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm",calendar).toString();
        return date;
    }
}
