package br.com.gfsportclub.sportclub;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SportAdapter extends RecyclerView.Adapter<SportAdapter.ViewHolder>{

    private String[] sportsName;
    private Context context;
    private List sports;


    public SportAdapter(Context context){
        this.context = context;

        sportsName =  new String[] {"Futebol", "Basquete", "Tênis", "Volei", "Handebol", "Polo Aquático", "Polo"};
        sports = Arrays.asList(sportsName);



    }

    @NonNull
    @Override
    public SportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sports_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nomeEsporte.setText(sports.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return sports.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeEsporte;

        public ViewHolder(View itemView) {
            super(itemView);
            nomeEsporte = itemView.findViewById(R.id.row_s_nome_esporte);
        }

    }
}
