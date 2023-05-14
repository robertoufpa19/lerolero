package robertorodrigues.curso.academicos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.adapter.AdapterMeusAnuncios;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;
import robertorodrigues.curso.academicos.model.Anuncio;
import robertorodrigues.curso.academicos.model.Usuario;

public class MeusAnunciosActivity extends AppCompatActivity {

    private FloatingActionButton fabAdicionarAnuncio;
    private RecyclerView recyclerAnuncios;
    private List<Anuncio> anuncios = new ArrayList<>();
    // private AdapterAnuncios adapterAnuncios;
    private AdapterMeusAnuncios adapterMeusAnuncios;
    private AlertDialog dialog;

    private Usuario usuario;
    private String idUsuarioLogado;
    private DatabaseReference firebaseRef;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Meus Anúncios");
        setSupportActionBar(toolbar);

        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        //configuracoes iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        usuario = UsuarioFirebase.getDadosUsuarioLogado();

        inicializarComponentes();


        fabAdicionarAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //condicao para verificar se o usuario configurou seus dados(perfil)
                if(usuario.getFotoUsuario() != null && usuario.getNomeUsuario() != null){
                    //entra na pagina de cadastro de anuncios
                    startActivity(new Intent(getApplicationContext(), CadastrarAnuncioActivity.class));

                }else{
                    Toast.makeText(MeusAnunciosActivity.this, "Configure seu Perfil, antes de publicar produtos", Toast.LENGTH_SHORT).show();
                    abrirConfiguracoes();
                }

            }
        });

        // configurar recyclerView
        recyclerAnuncios.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnuncios.setHasFixedSize(true);
        adapterMeusAnuncios = new AdapterMeusAnuncios(anuncios, this);
        recyclerAnuncios.setAdapter(adapterMeusAnuncios);

        // recuperar anuncios para os usuarios
        recuperarDadosUsuario();



        // adicionar evento de clique no recycler view
        /*
        recyclerAnuncios.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerAnuncios,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                 // unico clique
                                  exibirMensagem("Dei dois clique no icone para excluir");
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                  // clique longo

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        ); */

    }


    private void recuperarAnuncios(){

        //  configuracoes iniciais
        DatabaseReference anunciosUsuarioRef = firebaseRef
                .child("meus_anuncios")
                .child(idUsuarioLogado);
        anunciosUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                anuncios.clear();

                if(snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        anuncios.add(ds.getValue(Anuncio.class));

                    }

                    Collections.reverse(anuncios); // exibicao reversa dos anuncios
                    adapterMeusAnuncios.notifyDataSetChanged();
                }else{
                    Toast.makeText(MeusAnunciosActivity.this, "anuncios não existe", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {



            }
        });
    }


    private void recuperarDadosUsuario(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuarioLogado);
        // recupera dados uma unica vez
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    if(snapshot.getValue() != null){
                        usuario = snapshot.getValue(Usuario.class);
                        dialog.dismiss();
                    }else{
                        Toast.makeText(MeusAnunciosActivity.this, "usuario nulo", Toast.LENGTH_SHORT).show();
                    }
                    recuperarAnuncios();
                }else{
                    recuperarAnuncios();
                    Toast.makeText(MeusAnunciosActivity.this, "usuario não existe", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void inicializarComponentes(){
        recyclerAnuncios = findViewById(R.id.recyclerAnuncios);
        fabAdicionarAnuncio = findViewById(R.id.fabAdicionarAnuncio);
    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(MeusAnunciosActivity.this, EditarPerfilActivity.class));
    }
}