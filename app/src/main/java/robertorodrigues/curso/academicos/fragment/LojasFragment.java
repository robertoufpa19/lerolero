package robertorodrigues.curso.academicos.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.activity.ConversasActivity;
import robertorodrigues.curso.academicos.activity.DetalhesProdutoActivity;
import robertorodrigues.curso.academicos.activity.EditarPerfilActivity;
import robertorodrigues.curso.academicos.activity.LoginActivity;
import robertorodrigues.curso.academicos.activity.MeusAnunciosActivity;
import robertorodrigues.curso.academicos.activity.SplashActivity;
import robertorodrigues.curso.academicos.adapter.AdapterAnuncios;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.RecyclerItemClickListener;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;
import robertorodrigues.curso.academicos.model.Anuncio;
import robertorodrigues.curso.academicos.model.Usuario;


public class LojasFragment extends Fragment {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerAnunciosPublicos;
    private Button buttonRegiao, buttonCategoria, buttonLimparFiltro;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios = new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;
    private AlertDialog dialog;
    private String filtroEstado = "";
    private String filtroCategoria = "";
    private boolean filtrandoPorEstado = false;

    private Usuario usuario;

    private String idUsuarioLogado;
    private DatabaseReference firebaseRef;

  private TextView  textSemAnuncio;


    public LojasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lojas, container, false);


        inicializarComponentes(view);
        //configuracoes iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        // criar condição para verificar se usuario esta logado
        if(autenticacao.getCurrentUser() != null) { //logado
            idUsuarioLogado = UsuarioFirebase.getIdUsuario();
            firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
            recuperarDadosUsuario();

        }else{
            exibirMensagem("Você precisa está logado para utilizar o App");
            abrirLogin();
        }


        // configurar recyclerView
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(listaAnuncios, getContext());
        recyclerAnunciosPublicos.setAdapter(adapterAnuncios);



        //evento de clique
        recyclerAnunciosPublicos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getContext(),
                        recyclerAnunciosPublicos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                // criar condição para verificar se usuario esta logado
                                if(autenticacao.getCurrentUser() != null){ //logado

                                    //condicao para verificar se o usuario configurou seus dados(perfil)
                                    if(usuario.getFotoUsuario() != null ){ //teste

                                        List<Anuncio> listaAnunciosAtualizado = adapterAnuncios.getAnuncios();

                                        Anuncio anuncioSelecionado = listaAnunciosAtualizado.get(position); // seleciona anuncio que foi buscado de forma correta seja na pesquisa ou não
                                        Intent i = new Intent(getContext(), DetalhesProdutoActivity.class);
                                        i.putExtra("anuncioSelecionado", anuncioSelecionado);
                                        startActivity(i);

                                    }else{
                                        exibirMensagem("Configure seu Perfil, antes de ver produtos");
                                        abrirConfiguracoes();
                                    }



                                }else{ //deslogado
                                    exibirMensagem("Você precisa está logado para fazer um pedido!");

                                }



                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );

        buttonLimparFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStart(); // volta aos dados do inicio mesmo depois de aplicar filtros
                recuperarAnunciosPublicos();
            }
        });

        buttonRegiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtrarPorEstado(view);
            }
        });

        buttonCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filtrarPorCategoria(view);
            }
        });



        return  view;

    }




    // botao Regiao
    public void filtrarPorEstado(View view){

        // abilitar filtros se o usuario estiver logado
        if(autenticacao.getCurrentUser() != null) { //logado

            AlertDialog.Builder dialogEstado = new AlertDialog.Builder(getContext());
            dialogEstado.setTitle("Selecione a região desejado");


            // configurar spinner de estado
            View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
            // configuracoes iniciais
            Spinner spinnerEstado = viewSpinner.findViewById(R.id.spinnerFiltro);

            // spinner estado
            String[] estados = getResources().getStringArray(R.array.regiao);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    getContext(), android.R.layout.simple_spinner_item,
                    estados
            );

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerEstado.setAdapter(adapter);
            dialogEstado.setView(viewSpinner);


            dialogEstado.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    filtroEstado = spinnerEstado.getSelectedItem().toString();
                    recuperarAnunciosPorEstados();
                    filtrandoPorEstado = true;

                }
            });

            dialogEstado.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = dialogEstado.create();
            dialog.show();
        }



    }

    // botao categoria
    public void filtrarPorCategoria(View view){


        // abilitar filtros se o usuario estiver logado
        if(autenticacao.getCurrentUser() != null) { //logado


            if(filtrandoPorEstado == true){

                AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(getContext());
                dialogCategoria.setTitle("Selecione a categoria desejado");


                // configurar spinner de categoria
                View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
                // configuracoes iniciais
                Spinner spinnerCategoria = viewSpinner.findViewById(R.id.spinnerFiltro);

                // spinner categoria
                String[] categoria = getResources().getStringArray(R.array.categorias);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                       getContext(), android.R.layout.simple_spinner_item,
                        categoria
                );

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategoria.setAdapter(adapter);
                dialogCategoria.setView(viewSpinner);


                dialogCategoria.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                        recuperarAnunciosPorCategoria();

                    }
                });

                dialogCategoria.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = dialogCategoria.create();
                dialog.show();

            }else{
                exibirMensagem("Escolha primeiro uma Região");
            }


        }



    }


    public void recuperarAnunciosPorEstados(){
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("anuncios")
                .child(filtroEstado); // recupera o texto(sigla do estado) selecionado pelo usuario


        dialog = new SpotsDialog.Builder()
                .setContext(getContext())
                .setMessage("Recuperando Anuncios Por Estado!")
                .setCancelable(false)
                .build();
        dialog.show();

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listaAnuncios.clear();
          if(snapshot.exists()){
              if(snapshot.getValue() != null){
                  for(DataSnapshot categorias: snapshot.getChildren()){ // pecorre cada categoria de um estado
                      for(DataSnapshot anuncios: categorias.getChildren()){ // percorre anuncios(id) de cada categoria
                          Anuncio anuncio = anuncios.getValue(Anuncio.class);
                          listaAnuncios.add(anuncio);

                      }
                  }

                  Collections.reverse(listaAnuncios);
                  adapterAnuncios.notifyDataSetChanged();
                  dialog.dismiss();
              }else{
                  textSemAnuncio.setVisibility(View.VISIBLE);
                  dialog.dismiss();
              }
          }else{
              textSemAnuncio.setVisibility(View.VISIBLE);
              dialog.dismiss();
          }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void recuperarAnunciosPorCategoria(){
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("anuncios")
                .child(filtroEstado)
                .child(filtroCategoria); // recupera o texto(categoria) selecionado pelo usuario


        dialog = new SpotsDialog.Builder()
                .setContext(getContext())
                .setMessage("Recuperando Anuncios Por Categoria!")
                .setCancelable(false)
                .build();
        dialog.show();

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listaAnuncios.clear();

                if(snapshot.exists()){
                    textSemAnuncio.setVisibility(View.GONE);
                    if(snapshot.getValue() != null){
                        for(DataSnapshot anuncios: snapshot.getChildren()){ // pecorre cada categoria de um estado
                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaAnuncios.add(anuncio);

                        }

                        Collections.reverse(listaAnuncios);
                        adapterAnuncios.notifyDataSetChanged();
                        dialog.dismiss();
                    }else{
                        textSemAnuncio.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }

                }else{
                    textSemAnuncio.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public void recuperarAnunciosPublicos(){

        anunciosPublicosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("anuncios");

        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                listaAnuncios.clear();

                if(snapshot.exists()){
                    if(snapshot.getValue() != null){ // se tiver anuncios
                        textSemAnuncio.setVisibility(View.GONE);
                        for(DataSnapshot estados: snapshot.getChildren()){ // percorre cada no de estados e seus filhos(categoria e anuncios)
                            for(DataSnapshot categorias: estados.getChildren()){ // pecorre cada categoria de um estado
                                for(DataSnapshot anuncios: categorias.getChildren()){ // percorre anuncios(id) de cada categoria
                                    Anuncio anuncio = anuncios.getValue(Anuncio.class);
                                    listaAnuncios.add(anuncio);

                                }
                            }
                        }
                        Collections.reverse(listaAnuncios); // exibicao reversa dos anuncios
                        adapterAnuncios.notifyDataSetChanged();

                    }else if(snapshot.getValue() == null){ // senao tiver anuncios
                        textSemAnuncio.setVisibility(View.VISIBLE);
                    }
                }else{
                    textSemAnuncio.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // nao esta recuperando dados do usuario
    private void recuperarDadosUsuario(){
        dialog = new SpotsDialog.Builder()
                .setContext(getContext())
                .setMessage("Carregando dados ")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuarioLogado);
        // recupera dados uma unica vez
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    dialog.dismiss();
                    usuario = snapshot.getValue(Usuario.class);
                    recuperarAnunciosPublicos();


                }else{
                    dialog.dismiss();
                    abrirConfiguracoes();
                    exibirMensagem("Configure seu perfil");


                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_conversas:
                //entra na pagina de cadastro
                startActivity(new Intent(getContext(), ConversasActivity.class));
                break;
            case R.id.menuSair:

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Sair");
                builder.setMessage("Tem certeza que deseja sair?");

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            autenticacao.signOut();


                        }catch (Exception  e){
                            e.printStackTrace();
                        }
                        abrirLogin();
                    }
                });
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();


                break;


            case R.id.menu_sobre:

                //startActivity(new Intent(getContext(), SobreActivity.class));

                break;

            case R.id.menu_meus_anuncios:
                startActivity(new Intent(getContext(), MeusAnunciosActivity.class));
                break;



        }

        return super.onOptionsItemSelected(item);


    }



    private void inicializarComponentes(View view){
        buttonRegiao = view.findViewById(R.id.buttonRegiao);
        buttonCategoria = view.findViewById(R.id.buttonCategoria);
        buttonLimparFiltro = view.findViewById(R.id.buttonLimparFiltro);
        recyclerAnunciosPublicos = view.findViewById(R.id.recyclerAnunciosPublicos);
        textSemAnuncio = view.findViewById(R.id.textSemAnuncio);
    }

    private void exibirMensagem(String texto){
        Toast.makeText(getContext(), texto, Toast.LENGTH_SHORT).show();
    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(getContext(), EditarPerfilActivity.class));
    }
    private void abrirLogin(){
        startActivity(new Intent(getContext(), LoginActivity.class));
    }





    private void abrirSlide(){
        startActivity(new Intent(getContext(), SplashActivity.class));

    }


    /// finaliza no botão voltar do celular


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}