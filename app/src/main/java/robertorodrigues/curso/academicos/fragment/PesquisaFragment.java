package robertorodrigues.curso.academicos.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.activity.PerfilAmigoActivity;
import robertorodrigues.curso.academicos.adapter.AdapterPesquisa;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.RecyclerItemClickListener;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;
import robertorodrigues.curso.academicos.model.Usuario;

/*
 * Created by Roberto de Oliveira Rodrigues
 */
/**
 * A simple {@link Fragment} subclass.
 */
public class PesquisaFragment extends Fragment {

    private SearchView searchViewPesquisa;
    private RecyclerView recyclerPesquisa;
    private List<Usuario> listaUsuarios;
    private DatabaseReference usuarioRef;

    private AdapterPesquisa adapterPesquisa;

    private String idUsuarioLogado;


    public PesquisaFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pesquisa, container, false);

        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisaUsuarios);
        recyclerPesquisa = view.findViewById(R.id.recyclerPesquisa);


        // configurações iniciais
        listaUsuarios = new ArrayList<>();
        usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios");


        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        // configurar RecyclerView
        recyclerPesquisa.setHasFixedSize(true);
        recyclerPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterPesquisa =new AdapterPesquisa(listaUsuarios, getActivity());
        recyclerPesquisa.setAdapter(adapterPesquisa);
        // configurar evento de clique
        recyclerPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerPesquisa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Usuario usuarioSelecionado = listaUsuarios.get(position);
                        Intent i = new Intent(getActivity(), PerfilAmigoActivity.class);
                        i.putExtra("usuarioSelecionadoAmigo", usuarioSelecionado);
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


        // configurar searchviewPesquisa
        searchViewPesquisa.setQueryHint("Pesquisar Usuários");
        searchViewPesquisa.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // so aparece usuarios quando dando enter o precionado o botao de pesquisa
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               // vai listando os usuarios coonforme a letras de seus nomes
                String textoDigitado = newText.toLowerCase(); // buscar o ausuarios independente que seja com letra Maiscula ou minuscula
                pesquisarUsuario(textoDigitado);

                return true;
            }
        });


        return view;
    }


    private void pesquisarUsuario(String texto){

        //limpar lista
        listaUsuarios.clear();

        // pesquisa usuários caso tenha texto na pesquisa
        if(texto.length() > 0){

            Query query = usuarioRef.orderByChild("nomeUsuario")
                    .startAt(texto)
                    .endAt(texto + "\uf8ff"); // fazer pesquisa entre um texto e outro

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //limpar lista para nao duplicar usuarios
                    listaUsuarios.clear();
                    for(DataSnapshot ds : snapshot.getChildren()){

                        // verificar usuario logado e remover da lista de pesquisa
                         Usuario usuario = ds.getValue(Usuario.class);
                         if(idUsuarioLogado.equals(usuario.getIdUsuario())){
                             continue;  // volta para o for e buscar usuarios com id diferente do id do usuario logado
                         }

                        listaUsuarios.add(usuario);
                    }
                     adapterPesquisa.notifyDataSetChanged();
                  //  int total = listaUsuarios.size();
                   // exibirMensagem(" total usuarios: " + total);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void exibirMensagem(String texto){
        Toast.makeText(getActivity(), texto, Toast.LENGTH_SHORT).show();
    }
}