package com.williavizi.kdmeupetaplicacao.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.williavizi.kdmeupetaplicacao.R;
import com.williavizi.kdmeupetaplicacao.activity.ComentariosActivity;
import com.williavizi.kdmeupetaplicacao.model.Feed;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Intent.ACTION_VIEW;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder> {

    private List<Feed> listaFeed;
    private Context context;

    public AdapterFeed(List<Feed> listaFeed, Context context) {
        this.listaFeed = listaFeed;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_feed, parent, false);
        return new AdapterFeed.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Feed feed = listaFeed.get(position);

        //carrega dados do Feed
        Uri uriFotoUsuario = Uri.parse( feed.getFotoUsuario());
        Uri uriFotoPostagem = Uri.parse( feed.getFotoPostagem());

        Glide.with( context ).load(uriFotoUsuario).into(holder.fotoPerfil);
        Glide.with( context ).load(uriFotoPostagem).into(holder.fotoPostagem);

        holder.descricao.setText( feed.getDescricao() );
        holder.telefone.setText( feed.getTelefone() );
        holder.nome.setText( feed.getNomeUsuario() );

        holder.visualizarComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ComentariosActivity.class);
                i.putExtra("idPostagem", feed.getId() );
                context.startActivity( i );
            }
        });

        holder.imagemLocalização.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent i =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir//-23.3695123,-51.8959821/@-23.3695441,-51.9313078,13z"));
                i.putExtra("idPostagem", feed.getId() );
                context.startActivity( i );
            }
        });

    }



    @Override
    public int getItemCount() {
        return listaFeed.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView fotoPerfil;
        TextView nome, descricao, qtdCurtidas, telefone;
        ImageView fotoPostagem, visualizarComentario, imagemLocalização;
        LikeButton likeButton;

        public MyViewHolder(View itemView) {
            super(itemView);

            fotoPerfil   = itemView.findViewById(R.id.imagePerfilPostagem);
            fotoPostagem = itemView.findViewById(R.id.imagePostagemSelecionada);
            nome         = itemView.findViewById(R.id.textPerfilPostagem);
            telefone     = itemView.findViewById(R.id.textTelefonePostagem);
            qtdCurtidas  = itemView.findViewById(R.id.textQtdCurtidasPostagem);
            descricao    = itemView.findViewById(R.id.textDescricaoPostagem);
            visualizarComentario    = itemView.findViewById(R.id.imageComentarioFeed);
            imagemLocalização = itemView.findViewById(R.id.imageLocalização);
            likeButton = itemView.findViewById(R.id.likeButtonFeed);


        }
    }
}
