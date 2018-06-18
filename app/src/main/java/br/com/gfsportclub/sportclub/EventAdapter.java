package br.com.gfsportclub.sportclub;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
    public List<Event> eventos;
    public Context context;

    public EventAdapter(List<Event> eventos, Context context){
        this.eventos = eventos;
        this.context = context;
    }

    @NonNull
    @Override
    public EventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_row, parent, false);
        return new EventAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.ViewHolder holder, int position) {
        holder.titulo.setText(eventos.get(position).getTitulo());
        holder.data.setText(eventos.get(position).getData() + " - " + eventos.get(position).getHora());
        holder.esporte.setText(eventos.get(position).getEsporte());
        holder.local.setText(eventos.get(position).getNomeLocal());
    }

    @Override
    public int getItemCount() {
        return eventos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, data, local, esporte;

        public ViewHolder(final View itemView) {
            super(itemView);

           titulo = (TextView) itemView.findViewById(R.id.card_event_title);
           data = (TextView) itemView.findViewById(R.id.card_event_data);
           local = (TextView) itemView.findViewById(R.id.card_event_location);
           esporte = (TextView) itemView.findViewById(R.id.card_event_sport);

           itemView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Intent intent = new Intent(v.getContext(), EventInfoActivity.class);
                   intent.putExtra("EVENT_KEY", eventos.get(getAdapterPosition()).getKey());
                   v.getContext().startActivity(intent);
               }
           });
        }
    }

}
