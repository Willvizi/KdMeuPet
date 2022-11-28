package com.williavizi.kdmeupetaplicacao.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.williavizi.kdmeupetaplicacao.R;
import com.williavizi.kdmeupetaplicacao.model.Postagem;
import com.williavizi.kdmeupetaplicacao.model.Usuario;

import de.hdodenhof.circleimageview.CircleImageView;


public class VisualizarPostagemActivity extends AppCompatActivity {

    private ImageView imageLocalização;
    private TextView textPerfilPostagem,textQtdCurtidasPostagem,
            textDescricaoPostagem,textVisualizarComentariosPostagem, textTelefonePostagem;
    private ImageView imagePostagemSelecionada;
    private CircleImageView imagePerfilPostagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_postagem);

        imageLocalização = findViewById(R.id.imageLocalização);
        imageLocalização.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir//-23.3695123,-51.8959821/@-23.3695441,-51.9313078,13z")));
            }
        });

        //inicializar Componentes
        inicializarComponentes();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Visualizar Postagem");
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recupera dados da activity
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            Postagem postagem = (Postagem) bundle.getSerializable("postagem");
            Usuario usuario = (Usuario) bundle.getSerializable("usuario");

            //dados
            Uri uri = Uri.parse( usuario.getCaminhoFoto() );
            Glide.with(VisualizarPostagemActivity.this)
                    .load( uri )
                    .into( imagePerfilPostagem);
            textPerfilPostagem.setText( usuario.getNome());

            //exibe dados postagem
            Uri uriPostagem = Uri.parse(postagem.getCaminhoFoto());
            Glide.with(VisualizarPostagemActivity.this)
                    .load( uriPostagem)
                    .into(imagePostagemSelecionada);
            textDescricaoPostagem.setText(postagem.getDescricao());
            textTelefonePostagem.setText(postagem.getTelefone());

        }
    }

    private void inicializarComponentes(){
        textPerfilPostagem = findViewById(R.id.textPerfilPostagem);
        textQtdCurtidasPostagem = findViewById(R.id.textQtdCurtidasPostagem);
        textDescricaoPostagem = findViewById(R.id.textDescricaoPostagem);
        textTelefonePostagem = findViewById(R.id.textTelefonePostagem);
        imagePostagemSelecionada = findViewById(R.id.imagePostagemSelecionada);
        imagePerfilPostagem = findViewById(R.id.imagePerfilPostagem);

    }

    public boolean onSupportNavigateUp(){
        finish();
        return false;
    }

}
