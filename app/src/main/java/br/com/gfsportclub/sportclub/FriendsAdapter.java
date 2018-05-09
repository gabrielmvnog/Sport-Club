package br.com.gfsportclub.sportclub;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by gabriel on 03/05/18.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private List<User> friends;
    private Context context;

    public FriendsAdapter(Context c, List<User> f){
        this.friends = f;
        this.context = c;


    }

    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.with(context).load(friends.get(position).getImagem()).into(holder.imagem);
        holder.nome.setText(friends.get(position).getNome());
        holder.idade.setText(friends.get(position).getNome());
        holder.categoria.setText(friends.get(position).getNome());
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nome, idade, categoria;
        ImageView imagem;

        public ViewHolder(View itemView) {
            super(itemView);
            nome = (TextView) itemView.findViewById(R.id.friends_nome);
            idade = (TextView) itemView.findViewById(R.id.friends_idade);
            categoria = (TextView) itemView.findViewById(R.id.friends_categoria);
            imagem = (ImageView) itemView.findViewById(R.id.friends_imagem);

        }

    }
}
