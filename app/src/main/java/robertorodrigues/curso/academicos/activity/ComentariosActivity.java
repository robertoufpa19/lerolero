package robertorodrigues.curso.academicos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.adapter.AdapterComentario;
import robertorodrigues.curso.academicos.api.NotificacaoService;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.RecyclerItemClickListener;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;
import robertorodrigues.curso.academicos.model.Comentario;
import robertorodrigues.curso.academicos.model.Notificacao;
import robertorodrigues.curso.academicos.model.NotificacaoDados;
import robertorodrigues.curso.academicos.model.Usuario;

public class ComentariosActivity extends AppCompatActivity {

    private EditText editComentario;

    private String idPostagem;
    private Usuario usuario;
    private RecyclerView recyclerComentarios;
    private AdapterComentario adapterComentario;
    private List<Comentario> listaComentarios = new ArrayList<>();

    private DatabaseReference firebaseRef;
    private DatabaseReference comentariosRef;
    private ValueEventListener valueEventListenerComentarios;

    // notificação
    private String token;
    private Retrofit retrofit;
    private String baseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Comentários");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // configura botao voltar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_24); // conficurar um botao personalizado

       // iniciar componentes
        editComentario = findViewById(R.id.editComentario);
        recyclerComentarios = findViewById(R.id.recyclerComentarios);

        // configurações iniciais
        usuario = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        //Configurar adapter
        adapterComentario = new AdapterComentario(listaComentarios, getApplicationContext());

        // configurar recyclerview
        recyclerComentarios.setHasFixedSize(true);
        recyclerComentarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerComentarios.setAdapter(adapterComentario);

     // evento de clique nos comentarios
        recyclerComentarios.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerComentarios,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Comentario comentarioSelecionada = listaComentarios.get(position);

                        // enviar informacoes de uma activity para outra
                        Usuario usuario = new Usuario();

                        String idUsuario = comentarioSelecionada.getIdUsuario();
                        String nomeUsuario = comentarioSelecionada.getNomeUsuario();
                        String fotoUsuario = comentarioSelecionada.getFotoUsuario();

                        usuario.setIdUsuario(idUsuario);
                        usuario.setNomeUsuario(nomeUsuario);
                        usuario.setFotoUsuario(fotoUsuario);
                        // usuario.setTokenUsuario(token);

                        Intent i = new Intent(ComentariosActivity.this, PerfilAmigoActivity.class);
                        i.putExtra("comentarioUsuarioSelecionado",  usuario);
                        startActivity(i);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }

        ));


        // recuperar id da postagem
         Bundle bundle = getIntent().getExtras();
         if(bundle != null){
             idPostagem = bundle.getString("idPostagem");

         }

        //Configuração da retrofit para enviar requisição ao firebase e então para ele enviar a notificação
        baseUrl = "https://fcm.googleapis.com/fcm/";
        retrofit = new Retrofit.Builder()
                .baseUrl( baseUrl )
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }


    private void recuperarComentarios(){

         comentariosRef = firebaseRef.child("comentarios")
                 .child(idPostagem);

        valueEventListenerComentarios = comentariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listaComentarios.clear();

                for (DataSnapshot ds: snapshot.getChildren()){
                    listaComentarios.add(ds.getValue(Comentario.class));

                }
                // ordenar por postagens recente
               // Collections.reverse(listaComentarios);
                adapterComentario.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void salvarComentario(View view){

        String textoComentario = editComentario.getText().toString();

        if(textoComentario != null && !textoComentario.equals("")){

            Comentario comentario = new Comentario();
            comentario.setIdPostagem(idPostagem);
            comentario.setIdUsuario(usuario.getIdUsuario());
            comentario.setNomeUsuario(usuario.getNomeUsuario());
            comentario.setFotoUsuario(usuario.getFotoUsuario());
            comentario.setComentario(textoComentario);

            if(comentario.salvar()){
                enviarNotificacao();
               // exibirMensagem("Comentário salvo com sucesso!");
            }else{
                exibirMensagem("Erro ao salvar comentário!");
            }


        }else{
            exibirMensagem("Digite seu comentário!");
        }
        // limpar campo apos um comentario
        editComentario.setText("");
    }


    public void enviarNotificacao(){

        Bundle bundleNotificacao = getIntent().getExtras();
        if(bundleNotificacao.containsKey("idPostagem")){

           // empresaSelecionada  = (Empresa) bundleNotificacao.getSerializable("idPostagem");
            token = usuario.getTokenUsuario();


            String tokenDestinatario = token;
            String to = "";// para quem vou enviar a menssagem
            to = tokenDestinatario ;

            //Monta objeto notificação
            Notificacao notificacao = new Notificacao("Lero Lero", "Novo Comentario " + usuario.getNomeUsuario());
            NotificacaoDados notificacaoDados = new NotificacaoDados(to, notificacao );

            NotificacaoService service = retrofit.create(NotificacaoService.class);
            Call<NotificacaoDados> call = service.salvarNotificacao( notificacaoDados );

            call.enqueue(new Callback<NotificacaoDados>() {
                @Override
                public void onResponse(Call<NotificacaoDados> call, Response<NotificacaoDados> response) {
                    if( response.isSuccessful() ){

                        //teste para verificar se enviou a notificação
                           /*  Toast.makeText(getApplicationContext(),
                                     "codigo: " + response.code(),
                                     Toast.LENGTH_LONG ).show();

                            */

                    }
                }

                @Override
                public void onFailure(Call<NotificacaoDados> call, Throwable t) {

                }
            });
        }
    }


    private  void recuperarDadosUsuario(){
        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(usuario.getIdUsuario());
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() != null){
                     usuario = snapshot.getValue(Usuario.class);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        recuperarComentarios();
        recuperarDadosUsuario();
    }

    @Override
    protected void onStop() {
        super.onStop();

        comentariosRef.removeEventListener(valueEventListenerComentarios);
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