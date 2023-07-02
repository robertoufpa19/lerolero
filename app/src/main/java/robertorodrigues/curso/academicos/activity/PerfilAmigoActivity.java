package robertorodrigues.curso.academicos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.adapter.AdapterGrid;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;
import robertorodrigues.curso.academicos.model.Postagem;
import robertorodrigues.curso.academicos.model.Usuario;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Usuario usuarioLogado;
    private Button buttonSeguirPerfil,  botaoEnviarMensagem;

    private CircleImageView imagePerfilAmigo;

    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference postagemUsuarioRef;
    private ValueEventListener valueEventListenerPerfilAmigo;
    private TextView textPublicacoesAmigo, textSeguidoresAmigo, textSeguindoAmigo;

    private String idUsuarioLogado;

    private GridView gridViewPerfilAmigo;
    private AdapterGrid adapterGrid;
    private List<Postagem> postagens;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // configura botao voltar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_fechar_24); // conficurar um botao personalizado

        // incializar componentes
        inicializarComponentes();

        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
         // busca informações do NO usuarios no firebase
        usuariosRef = firebaseRef.child("usuarios");
        // busca informações do NO seguidores no firebase
        seguidoresRef = firebaseRef.child("seguidores");

        idUsuarioLogado = UsuarioFirebase.getIdUsuario();


        // recuperar dados do usuário selecionado
        Bundle bundle = getIntent().getExtras();
          if(bundle != null){

              if(bundle.containsKey("usuarioSelecionadoAmigo")){

                  usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionadoAmigo");


                  // busca informações do NO postagens no firebase
                  postagemUsuarioRef = firebaseRef
                          .child("postagens")
                          .child(usuarioSelecionado.getIdUsuario());


                  // configurar nome do usuario amigo na toolbra
                  getSupportActionBar().setTitle(usuarioSelecionado.getNomeUsuario());
                  // recuperar foto de perfil amigo
                  String url = usuarioSelecionado.getFotoUsuario();
                  if(url != null){
                      Picasso.get().load(url).into(imagePerfilAmigo);
                  }else{
                      imagePerfilAmigo.setImageResource(R.drawable.perfil);
                  }
                   if(idUsuarioLogado.equals(usuarioSelecionado.getIdUsuario())){
                       botaoEnviarMensagem.setVisibility(View.GONE);
                       buttonSeguirPerfil.setVisibility(View.GONE);
                   }else{
                       botaoEnviarMensagem.setVisibility(View.VISIBLE);
                       buttonSeguirPerfil.setVisibility(View.VISIBLE);
                       botaoEnviarMensagem.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {

                               Intent intent = new Intent( PerfilAmigoActivity.this, ChatActivity.class);
                               intent.putExtra("usuarioSelecionadoAmigo", usuarioSelecionado);
                               startActivity(intent);

                           }
                       });
                   }


              }else if(bundle.containsKey("comentarioUsuarioSelecionado")){

                  usuarioSelecionado = (Usuario) bundle.getSerializable("comentarioUsuarioSelecionado");


                  // busca informações do NO postagens no firebase
                  postagemUsuarioRef = firebaseRef
                          .child("postagens")
                          .child(usuarioSelecionado.getIdUsuario());


                  // configurar nome do usuario amigo na toolbra
                  getSupportActionBar().setTitle(usuarioSelecionado.getNomeUsuario());
                  // recuperar foto de perfil amigo
                  String url = usuarioSelecionado.getFotoUsuario();
                  if(url != null){
                      Picasso.get().load(url).into(imagePerfilAmigo);
                  }else{
                      imagePerfilAmigo.setImageResource(R.drawable.perfil);
                  }

              }



          }

          inicializarImageLoader();

          carregarFotosPostagem(); // postagens de amigos

          //abrir foto clicada
        gridViewPerfilAmigo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

              Postagem postagem = postagens.get(position);
              Intent i = new Intent(getApplicationContext(), VisualizarPostagemActivity.class);
               i.putExtra("postagemSelecionadaAmigo", postagem);
               i.putExtra("usuarioSelecionadoAmigo", usuarioSelecionado);
               startActivity(i);

            }
        });


    }

    // metodo para verificar se ja estou seguindo um usuario(amigo)
    private void verificaSeguindoAmigo(){

        DatabaseReference seguidorRef = seguidoresRef
                .child(usuarioSelecionado.getIdUsuario())
                .child(idUsuarioLogado);
        seguidorRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){ // verifica se existe dados
                            // não está desabilitando o botão seguir
                            // já está seguindo
                            habilitarBotaoSeguir(true);

                        }else{
                            // não está seguindo
                            habilitarBotaoSeguir(false);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );

    }

    private void habilitarBotaoSeguir(boolean seguindoUsuario){



        if(seguindoUsuario){ //  seguindoUsuario == true
            buttonSeguirPerfil.setText("Seguindo");
            // buscar informçãodo firebase para verificar se esta seguindo o usuario
        }else {
            buttonSeguirPerfil.setText("Seguir");
            // adicionar evento de clique para seguir usuário
            buttonSeguirPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // salvar seguidor
                       salvarSeguidor( usuarioLogado, usuarioSelecionado);
                       exibirMensagem("Seguindo " + usuarioSelecionado.getNomeUsuario());


                }
            });
        }
    }

    private void salvarSeguidor(Usuario uLogado, Usuario uAmigo){

        /*
        // estrutura no banco de dados no NO seguidores
        * seguidores-NO
        *  id_usuario_selecionado(amigo)
        *     id_usuario_logado
        *        dados usuario logado
        */

        // recuperar somente algumas informações do amigo a ser seguido
        HashMap<String, Object> dadosUsuarioLogado = new HashMap<>();
            dadosUsuarioLogado.put("idUsuario", uLogado.getIdUsuario());
            dadosUsuarioLogado.put("nomeUsuario", uLogado.getNomeUsuario());
            dadosUsuarioLogado.put("fotoUsuario", uLogado.getFotoUsuario());
            dadosUsuarioLogado.put("tokenUsuario", uLogado.getTokenUsuario());

             // cria o NO seguidores
        DatabaseReference seguidorRef =  seguidoresRef
                .child(uAmigo.getIdUsuario())
                .child(uLogado.getIdUsuario());
        seguidorRef.setValue(dadosUsuarioLogado);

        // alterar botão para seguindo
        buttonSeguirPerfil.setText("Seguindo");
        buttonSeguirPerfil.setOnClickListener(null); // usuario nao pode mais selecionar o botao seguir apos seguir um amigo


        // incrementar seguindo do usuario logado
           int seguindo = uLogado.getSeguindo() + 1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo", seguindo);

        DatabaseReference usuarioSeguindo = usuariosRef
                .child(uLogado.getIdUsuario());
        usuarioSeguindo.updateChildren(dadosSeguindo); // atualizar NO "usuarios" incrementando o atributo "seguindo"

        // incrementar seguidores do amigo selecionado
        int seguidores = uAmigo.getSeguidores() + 1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores", seguidores);

        DatabaseReference usuarioSeguidores = usuariosRef
                .child(uAmigo.getIdUsuario());
        usuarioSeguidores.updateChildren(dadosSeguidores); // atualizar NO "usuarios" incrementando o atributo "seguidores"


    }

    // carregar as imagens do gredView amigo
    // instancia a universalImageLoader
    public void inicializarImageLoader(){
          // carrega as imagens mais rapido
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .build();

        ImageLoader.getInstance().init(config);

    }


    public void carregarFotosPostagem(){

        // recuperar as fotos postadas pelo usuario amigo
         postagens = new ArrayList<>();
        postagemUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // configurar o tamanho do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels; // tamho real da tela do usuario
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewPerfilAmigo.setColumnWidth(tamanhoImagem);


                List<String> urlFotos = new ArrayList<>();

                for( DataSnapshot ds: snapshot.getChildren()){
                    Postagem postagem = ds.getValue(Postagem.class);
                    postagens.add(postagem);
                    urlFotos.add(postagem.getFotoPostagem());
                }


               // int qtdPostagens = urlFotos.size();
               // textPublicacoesAmigo.setText(String.valueOf(qtdPostagens));

                // configurar adapter para recuperar fotos do gridView
                 adapterGrid = new AdapterGrid(getApplicationContext(), R.layout.grid_postagem, urlFotos);
                 gridViewPerfilAmigo.setAdapter(adapterGrid);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void recuperarDadosUsuarioLogado(){
        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //recupera dados do usuario logado
                        usuarioLogado = snapshot.getValue(Usuario.class); // recebe todas as informações do NO usuarios

                        verificaSeguindoAmigo();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }


    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosPerfilAmigo();
        recuperarDadosUsuarioLogado();
    }

    @Override
    protected void onStop() {
        super.onStop();
  usuarioAmigoRef.removeEventListener(valueEventListenerPerfilAmigo);

    }

    private void recuperarDadosPerfilAmigo(){

        usuarioAmigoRef = usuariosRef.child(usuarioSelecionado.getIdUsuario());

         valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
                 new ValueEventListener() {
                     @Override
                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                         // recuperar dados e exibir na tela
                         Usuario usuario = snapshot.getValue(Usuario.class);
                         //converter os valores em Strings
                         String postagens = String.valueOf(usuario.getPostagens());
                         String seguindo = String.valueOf(usuario.getSeguindo());
                         String seguidores = String.valueOf(usuario.getSeguidores());

                         // configurar valores recuperados
                         textPublicacoesAmigo.setText(postagens);
                         textSeguindoAmigo.setText(seguindo);
                         textSeguidoresAmigo.setText(seguidores);

                     }

                     @Override
                     public void onCancelled(@NonNull DatabaseError error) {

                     }
                 }
         );


    }

    private void inicializarComponentes(){

        buttonSeguirPerfil = findViewById(R.id.buttonSeguirPerfil);
        buttonSeguirPerfil.setText("Carregando...");
        imagePerfilAmigo = findViewById(R.id.imagePerfilAmigo);
        textPublicacoesAmigo = findViewById(R.id.textPublicacoesAmigo);
        textSeguidoresAmigo = findViewById(R.id.textSeguidoresAmigo);
        textSeguindoAmigo = findViewById(R.id.textSeguindoAmigo);
        gridViewPerfilAmigo = findViewById(R.id.gridViewPerfilAmigo);

        botaoEnviarMensagem = findViewById(R.id.botaoEnviarMensagemAmigo);

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

     // ao clicar no icone X a activiy será finalizada
    @Override
    public boolean onSupportNavigateUp() {
         finish();
        return false;
    }
}