package robertorodrigues.curso.academicos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;
import robertorodrigues.curso.academicos.model.Feed;
import robertorodrigues.curso.academicos.model.Postagem;
import robertorodrigues.curso.academicos.model.PostagemCurtida;
import robertorodrigues.curso.academicos.model.Usuario;

public class VisualizarPostagemActivity extends AppCompatActivity {

    private CircleImageView imagePerfilVisualizarPostagem;
    private TextView textNomeVisualizarPostagem, descricao, textQtdCurtidaAmigo, textQtdComentarioAmigo;
    private ImageView imageVisualizarFotoPostada,  botaoComentarios;


    private Usuario usuarioLogado;
    private LikeButton botaoCurtida;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_postagem);


        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Postagem");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // configura botao voltar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_fechar_24); // conficurar um botao personalizado


        inicializarComponentes();

        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        // recuperar dados da activityperfil amigo
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Postagem postagem = (Postagem) bundle.getSerializable("postagemSelecionadaAmigo");
            Usuario usuario = (Usuario) bundle.getSerializable("usuarioSelecionadoAmigo");

            // exibir dados do usuario
            // recuperar foto
            Uri uri = Uri.parse(usuario.getFotoUsuario());
            // foto usuario
            if (uri != null) {
                Picasso.get()
                        .load(uri)
                        .into(imagePerfilVisualizarPostagem);
            } else {
                imagePerfilVisualizarPostagem.setImageResource(R.drawable.perfil);
            }
            // nome usuario
            textNomeVisualizarPostagem.setText(usuario.getNomeUsuario());



            // exibir dados da postagem
            Uri uriPostagem = Uri.parse(postagem.getFotoPostagem());

            if (uriPostagem != null) {
                Picasso.get()
                        .load(uriPostagem)
                        .into(imageVisualizarFotoPostada);
            } else {
               // imagePerfilVisualizarPostagem.setImageResource(R.drawable.perfil);
                exibirMensagem("Erro ao recuperar foto Postagem!");
            }

            descricao.setText(postagem.getDescricao());

            // falta recuperar curtidas e comentarios

            // recuperar dados da postagem curtida
            DatabaseReference curtidasRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("postagens_curtidas")
                    .child(postagem.getIdPostagem());
            curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int qtdCurtidas = 0;
                    if(snapshot.hasChild("qtdCurtidas")){ // verifica se existe essa variavel no firebase
                        PostagemCurtida postagemCurtida = snapshot.getValue(PostagemCurtida.class);
                        qtdCurtidas = postagemCurtida.getQtdCurtidas();

                        // recupera quantidade de curtidas
                        textQtdCurtidaAmigo.setText(qtdCurtidas + "");

                        // verificar se o botão curtida já foi clicado
                        if(snapshot.hasChild(usuarioLogado.getIdUsuario())){
                            botaoCurtida.setLiked(true);
                        }else{
                            botaoCurtida.setLiked(false);
                        }


                    }

                    // monta objeto postagem curtida
                    PostagemCurtida curtida = new PostagemCurtida();

                    Feed feed = new Feed();
                    feed.setIdPostagem(postagem.getIdPostagem());
                    feed.setFotoPostagem(postagem.getFotoPostagem());
                    feed.setDescricao(postagem.getDescricao());

                    curtida.setFeed(feed);
                    curtida.setUsuario(usuario);
                    curtida.setQtdCurtidas(qtdCurtidas);

                    // adicionar evento para curtir uma postagem
                    botaoCurtida.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            // salva dados do usuario que curtiu uma postagem no firebase
                            curtida.salvar();
                            textQtdCurtidaAmigo.setText(curtida.getQtdCurtidas() + ""); //"curtidas"

                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            curtida.removerCurtida();
                            textQtdCurtidaAmigo.setText(curtida.getQtdCurtidas() + ""); //"curtidas"
                        }
                    });





                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });




            botaoComentarios.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(VisualizarPostagemActivity.this, ComentariosActivity.class);
                    i.putExtra("idPostagem", postagem.getIdPostagem() );
                    startActivity(i);
                }
            });


        }



    }






    private void inicializarComponentes(){
        imagePerfilVisualizarPostagem = findViewById(R.id.imagePerfilVisualizarPostagem);
        textNomeVisualizarPostagem = findViewById(R.id.textNomeVisualizarPostagem);
        imageVisualizarFotoPostada = findViewById(R.id.imageVisualizarFotoPostada);
        descricao = findViewById(R.id.textVisualizarDescricaoPostagem);
        textQtdCurtidaAmigo = findViewById(R.id.textQtdCurtidaAmigo);

        botaoCurtida = findViewById(R.id.botaoCurtidaPostagemAmigo);
        botaoComentarios = findViewById(R.id.imageComentariosPostagemAmigo);

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;

    }

}