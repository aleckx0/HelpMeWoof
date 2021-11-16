package net.alecks.helpmewoof.Adapters;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatCallback;
import androidx.recyclerview.widget.RecyclerView;

import net.alecks.helpmewoof.Activities.Reportes;
import net.alecks.helpmewoof.Modelos.Comentario;
import net.alecks.helpmewoof.R;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>  {

    private Context mContext;
    private List<Comentario> mData;
    int selected_position = 0;

    public CommentAdapter(Context mContext, List<Comentario> mdata) {
        this.mContext = mContext;
        this.mData = mdata;
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
        //holder.tv_date.setText(timestampToString((Long)mData.get(position).getTimestamp()));
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

    private String timestampToString (long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm",calendar).toString();
        return date;
    }
}
