package br.com.gfsportclub.sportclub;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.List;

public class SelectSportAdapter extends RecyclerView.Adapter<SelectSportAdapter.ViewHolder>{

    private String[] sportsName;
    private Context context;
    private List sports;
    private DatabaseReference mDatabase;



    public SelectSportAdapter(Context context, DatabaseReference databaseReference){
        this.context = context;
        this.mDatabase = databaseReference;

        sportsName =  new String[] {"Futebol", "Basquete", "Tênis", "Volei", "Handebol", "Polo Aquático", "Polo"};
        sports = Arrays.asList(sportsName);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sports_row, parent, false);
        return new SelectSportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final String sportName = sports.get(position).toString();
        holder.nomeEsporte.setText(sportName);
    }

    @Override
    public int getItemCount() {
        return sports.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nomeEsporte;
        boolean click = true;

        public ViewHolder(final View itemView) {
            super(itemView);
            nomeEsporte = itemView.findViewById(R.id.row_s_nome_esporte);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(click){
                        itemView.setBackgroundColor(Color.GRAY);
                        mDatabase.child(sports.get(getAdapterPosition()).toString().toLowerCase()).setValue("true");
                        click = false;
                    } else {
                        itemView.setBackgroundColor(Color.WHITE);
                        mDatabase.child(sports.get(getAdapterPosition()).toString().toLowerCase()).setValue(null);
                        click = true;
                    }

                }
            });
        }


    }

}
