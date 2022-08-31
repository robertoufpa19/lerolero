package robertorodrigues.curso.academicos.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.activity.EditarPerfilActivity;
import robertorodrigues.curso.academicos.activity.VisualizarMinhaPostagemActivity;
import robertorodrigues.curso.academicos.activity.VisualizarPostagemActivity;
import robertorodrigues.curso.academicos.adapter.AdapterGrid;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;
import robertorodrigues.curso.academicos.model.Postagem;
import robertorodrigues.curso.academicos.model.Usuario;


/*
 * Created by Roberto de Oliveira Rodrigues
 */

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {

    private ProgressBar progressBar;
    private CircleImageView imagePerfil;
    private GridView gridViewperfil;
    private Button buttonEditarPerfil;
    private TextView textPublicacoes, textSeguidores, textSeguindo;

    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference firebaseRef;
    private Usuario usuarioLogado;

    private AdapterGrid adapterGrid;
    private DatabaseReference postagemUsuarioRef;
    private AlertDialog dialog;

    private List<Postagem> postagens;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);


         // configuracoes dos componentes
        gridViewperfil = view.findViewById(R.id.gridViewPerfil);
        progressBar = view.findViewById(R.id.progressBarPerfil);
        imagePerfil = view.findViewById(R.id.imagePerfil);
        textPublicacoes = view.findViewById(R.id.textPublicacoes);
        textSeguidores = view.findViewById(R.id.textSeguidores);
        textSeguindo = view.findViewById(R.id.textSeguindo);
        buttonEditarPerfil = view.findViewById(R.id.buttonEditarPerfil);

        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        // busca informações do NO usuarios no firebase
        usuariosRef = firebaseRef.child("usuarios");

       // recuperarDadosUsuario();

        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        // busca informações do NO postagens no firebase
        postagemUsuarioRef = firebaseRef
                .child("postagens")
                .child(usuarioLogado.getIdUsuario());



        // abrir edicao de perfil
        buttonEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(intent);
            }
        });

        inicializarImageLoader();
        carregarFotosPostagem();



        //abrir foto clicada
        gridViewperfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Postagem postagem = postagens.get(position);
                Intent i = new Intent(getContext(), VisualizarMinhaPostagemActivity.class);
                i.putExtra("minhaPostagemSelecionada", postagem);
                i.putExtra("usuarioLogado", usuarioLogado);
                startActivity(i);

            }
        });

        return view;
    }


    // carregar as imagens do gredView amigo
    // instancia a universalImageLoader
    public void inicializarImageLoader(){
        // carrega as imagens mais rapido
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getContext())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .build();

        ImageLoader.getInstance().init(config);

    }


    public void carregarFotosPostagem(){

        // recuperar as fotos postadas pelo usuario
        postagens = new ArrayList<>();
        postagemUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // configurar o tamanho do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels; // tamho real da tela do usuario
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewperfil.setColumnWidth(tamanhoImagem);


                List<String> urlFotos = new ArrayList<>();

                for( DataSnapshot ds: snapshot.getChildren()){
                    Postagem postagem = ds.getValue(Postagem.class);
                    postagens.add(postagem);
                    urlFotos.add(postagem.getFotoPostagem());
                }


               // int qtdPostagens = urlFotos.size();
               // textPublicacoes.setText(String.valueOf(qtdPostagens));

                // configurar adapter para recuperar fotos do gridView
                adapterGrid = new AdapterGrid(getContext(), R.layout.grid_postagem, urlFotos);
                gridViewperfil.setAdapter(adapterGrid);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    @Override
    public void onStart() {
        super.onStart();
        recuperarDadosUsuario();

    }





    private  void recuperarDadosUsuario(){

        // carregamento dados usuarios
        dialog = new SpotsDialog.Builder()
                .setContext(getContext())
                .setMessage("Carregando Dados")
                .setCancelable(false)
                .build();
        dialog.show();
        // recuperar id usuario
        FirebaseUser idUsuarioLogadoUid = UsuarioFirebase.getUsuarioAtual();
        String idUsuarioLogado = idUsuarioLogadoUid.getUid();

        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuarioLogado);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() != null){
                    Usuario usuario = snapshot.getValue(Usuario.class);

                    //converter os valores em Strings
                    String postagens = String.valueOf(usuario.getPostagens());
                    String seguindo = String.valueOf(usuario.getSeguindo());
                    String seguidores = String.valueOf(usuario.getSeguidores());

                    // configurar valores recuperados
                    textPublicacoes.setText(postagens);
                    textSeguindo.setText(seguindo);
                    textSeguidores.setText(seguidores);

                    // recuperar foto de perfil do usuario
                    FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
                    Uri url = usuarioPerfil.getPhotoUrl();
                    if(url != null){
                        Picasso.get()
                                .load(url)
                                .into(imagePerfil);
                    }else{
                        imagePerfil.setImageResource(R.drawable.perfil);
                    }

                    dialog.dismiss();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}