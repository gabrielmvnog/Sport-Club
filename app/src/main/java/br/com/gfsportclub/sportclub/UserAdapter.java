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

/**
 * Created by gabriel on 03/05/18.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> friends;
    private Context context;

    public UserAdapter(Context c, List<User> f){
        this.friends = f;
        this.context = c;

    }

    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.with(context).load(friends.get(position).getImagem()).into(holder.imagem);
        holder.nome.setText(friends.get(position).getNome());
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nome, idade, categoria;
        ImageView imagem;

        public ViewHolder(final View itemView) {
            super(itemView);
            nome = (TextView) itemView.findViewById(R.id.friends_nome);
            imagem = (ImageView) itemView.findViewById(R.id.friends_imagem);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("KEY", friends.get(getAdapterPosition()).getKey());
                    itemView.getContext().startActivity(intent);
                }
            });

        }

    }
}
