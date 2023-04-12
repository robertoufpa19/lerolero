package robertorodrigues.curso.academicos.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.activity.ChatActivity;
import robertorodrigues.curso.academicos.adapter.ConversasAdapter;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.RecyclerItemClickListener;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;
import robertorodrigues.curso.academicos.model.Conversa;
import robertorodrigues.curso.academicos.model.Usuario;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */

public class ConversasFragment extends Fragment {

    private RecyclerView recyclerViewConversas;
    private List<Conversa> listaConversas = new ArrayList<>();
    private ConversasAdapter adapter;
    private DatabaseReference database;
    private DatabaseReference conversasRef;
    private ChildEventListener childEventListenerConversas;
    private Context con;


    private String idUsuarioLogado;



    public ConversasFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_conversas, container, false);

        recyclerViewConversas = view.findViewById(R.id.recyclerListaConversas); // lista de conversas

        idUsuarioLogado = UsuarioFirebase.getIdUsuario();


        //Configurar adapter
        adapter = new ConversasAdapter(listaConversas, getActivity());

        //Configurar recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewConversas.setLayoutManager( layoutManager );
        recyclerViewConversas.setHasFixedSize(true);
        recyclerViewConversas.setAdapter( adapter );

        //Configurar evento de clique nas conversas
        recyclerViewConversas.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewConversas,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                List<Conversa> listaConversasAtualizada = adapter.getConversas();
                                Conversa conversaSelecionada = listaConversasAtualizada .get(position); // seleciona conversa que foi buscada de forma correta

                                // enviar informacoes de um fragment para uma activity
                                Usuario usuario = new Usuario();

                                String idUsuario = conversaSelecionada.getUsuarioExibicao().getIdUsuario();
                                String nomeUsuario = conversaSelecionada.getNomeUsuario();
                                String fotoUsuario = conversaSelecionada.getFotoUsuario();
                                String token = conversaSelecionada.getTokenUsuario();


                            /* Recebendo os valores no objeto para enviar para
                               a próxima tela através do método putExtra() */

                                usuario.setIdUsuario(idUsuario);
                                usuario.setNomeUsuario(nomeUsuario);
                                usuario.setFotoUsuario(fotoUsuario);
                                usuario.setTokenUsuario(token);

                                    // recuperar conversa para compartilhar imagem
                                if(conversaSelecionada != null){

                                    // recuperar arquivo compartilhado - inicio
                                    Bundle bundleArquivo = getActivity().getIntent().getExtras();
                                    if(bundleArquivo != null) {
                                        if (bundleArquivo.containsKey("compartilharImagem")) {
                                            Uri imagemSelecionada = (Uri) getActivity().getIntent().getParcelableExtra("compartilharImagem");
                                            conversaSelecionada.setArquivoCompartilhado(imagemSelecionada);
                                            Intent intentConversas = new Intent(getContext(), ChatActivity.class);
                                            intentConversas.putExtra("compartilharImagem", usuario);
                                            intentConversas.putExtra("compartilharImagem", conversaSelecionada);
                                            startActivity(intentConversas);
                                            Log.d("imagem ", imagemSelecionada.toString());


                                        } else if (bundleArquivo.containsKey("compartilharPDF")) {
                                            Uri pdfSelecionado = (Uri) getActivity().getIntent().getParcelableExtra("compartilharPDF");
                                            conversaSelecionada.setArquivoCompartilhado(pdfSelecionado);
                                            Intent intentConversas = new Intent(getContext(), ChatActivity.class);
                                            intentConversas.putExtra("compartilharPDF",   usuario);
                                            intentConversas.putExtra("compartilharPDF", conversaSelecionada);
                                            startActivity(intentConversas);
                                            Log.d("pdf ", pdfSelecionado.toString());
                                        }

                                    }else{
                                        Intent i = new Intent(getActivity(), ChatActivity.class);
                                        i.putExtra("chat",  usuario); // usuario exibicao
                                        startActivity( i );
                                    }

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


        //Configura conversas ref
        String identificadorUsuario = UsuarioFirebase.getIdUsuario();
        database = ConfiguracaoFirebase.getFirebaseDatabase();
        conversasRef = database.child("conversas")
                .child( identificadorUsuario );




        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        super.onStart();
        recuperarConversas();
    }

    @Override
    public void onStop() {
        super.onStop();
        conversasRef.removeEventListener( childEventListenerConversas);
    }

    public void recuperarConversas(){

        listaConversas.clear();

        childEventListenerConversas = conversasRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                // Recuperar conversas
                Conversa conversa = dataSnapshot.getValue( Conversa.class );
                listaConversas.add(conversa);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onAttach(Context context) {
        this.con = context;
        super.onAttach(con);
    }
}