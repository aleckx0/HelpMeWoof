package net.alecks.helpmewoof.Adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import net.alecks.helpmewoof.Modelos.Comentario;
import net.alecks.helpmewoof.R;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comentario> mData;

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
        holder.tv_user.setText(mData.get(position).getNombreUsuario());
        holder.tv_content.setText(mData.get(position).getComentario());
        //holder.tv_date.setText(timestampToString((Long)mData.get(position).getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView tv_user, tv_content, tv_date;

        public CommentViewHolder(View itemView){
            super (itemView);
            tv_user = itemView.findViewById(R.id.textView6);
            tv_content = itemView.findViewById(R.id.textView);
            tv_date = itemView.findViewById(R.id.comment_date);

        }
    }

    private String timestampToString (long time){

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm",calendar).toString();
        return date;
    }
}
