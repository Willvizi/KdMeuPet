package com.williavizi.kdmeupetaplicacao.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.williavizi.kdmeupetaplicacao.R;
import com.williavizi.kdmeupetaplicacao.helper.ConfiguracaoFirebase;
import com.williavizi.kdmeupetaplicacao.helper.UsuarioFirebase;
import com.williavizi.kdmeupetaplicacao.model.Postagem;
import com.williavizi.kdmeupetaplicacao.model.Usuario;

import java.io.ByteArrayOutputStream;



public class FiltroActivity extends AppCompatActivity {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageFotoEscolhida;
    private Bitmap imagem;
    private TextInputEditText textDescricaoFiltro;
    private TextInputEditText textTelefoneFiltro;
    private String idUsuarioLogado;
    public String mandaEssaPorra;
    private AlertDialog dialog;
    private Usuario usuarioLogado;


    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference firebaseRef;
    private DataSnapshot seguidoresSnapshot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        usuariosRef = ConfiguracaoFirebase.getFirebase().child("usuarios");

        //Inicializar componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        textDescricaoFiltro = findViewById(R.id.textDescricaoFiltro);
        textTelefoneFiltro = findViewById(R.id.textTelefoneFiltro);

        //Recuperar dados para uma nova postagem
        recuperarDadosPostagem();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Filtro");
        setSupportActionBar( toolbar );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recupera a imagem escolhida pelo usuário
        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
            byte[] dadosImagem = bundle.getByteArray("fotoEscolhida");
            imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length );
            imageFotoEscolhida.setImageBitmap( imagem );
        }

    }

    private void abrirDialogCarregamento(String titulo){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle( titulo );
        alert.setCancelable(false);

        dialog = alert.create();
        dialog.show();

    }

    private void recuperarDadosPostagem(){

        abrirDialogCarregamento("Carregando dados, aguarde!");
        usuarioLogadoRef = usuariosRef.child( idUsuarioLogado );
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        //Recupera dados de usuário logado
                        usuarioLogado = dataSnapshot.getValue( Usuario.class );


                        /*
                         * Recuperar seguidores */
                        DatabaseReference seguidoresRef = firebaseRef
                                .child("seguidores")
                                .child( idUsuarioLogado );
                        seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                seguidoresSnapshot = dataSnapshot;
                                dialog.cancel();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void publicarPostagem(){

        final Postagem postagem = new Postagem();
        postagem.setIdUsuario( idUsuarioLogado );
        postagem.setDescricao( textDescricaoFiltro.getText().toString().toUpperCase() );
        postagem.setTelefone( textTelefoneFiltro.getText().toString().toUpperCase());

        //Recuperar dados da imagem para o firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] dadosImagem = baos.toByteArray();

        //Salvar imagem no firebase storage
        StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        StorageReference imagemRef = storageRef
                .child("imagens")
                .child("postagens")
                .child( postagem.getId() + ".jpeg");

        UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FiltroActivity.this,
                        "Erro ao salvar a imagem, tente novamente!",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                //Recuperar local da foto
                imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri url = task.getResult();

                        postagem.setCaminhoFoto( url.toString() );
                         mandaEssaPorra = url.toString();
                        postagem.setCaminhoFoto(mandaEssaPorra);


                }
                });


                //Salvar postagem
                if( postagem.salvar(seguidoresSnapshot) ){
                    postagem.setCaminhoFoto(mandaEssaPorra);
                    Toast.makeText(FiltroActivity.this,
                            "Sucesso ao salvar postagem!",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

        });

postagem.setCaminhoFoto("https://firebasestorage.googleapis.com/v0/b/kdmeupetapp-cb439.appspot.com/o/imagens%2Fpostagens%2F"+postagem.getId()+ ".jpeg?alt=media");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId() ){
            case R.id.ic_salvar_postagem :
                publicarPostagem();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
