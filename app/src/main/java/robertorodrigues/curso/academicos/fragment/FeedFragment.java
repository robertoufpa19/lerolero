package robertorodrigues.curso.academicos.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.activity.PermissaoNotificacaoActivity;
import robertorodrigues.curso.academicos.adapter.AdapterFeed;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;
import robertorodrigues.curso.academicos.model.Feed;
import robertorodrigues.curso.academicos.model.Usuario;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {

    private RecyclerView recyclerFeed;

    private AdapterFeed adapterFeed;

    private List<Feed> listaFeed = new ArrayList<>();

    private ValueEventListener valueEventListenerFeed;

    private DatabaseReference feedRef;

    private String idUsuarioLogado;

    private AlertDialog dialog;

    private ProgressBar progressBarFeed;
    private TextView textSemPostagem;

    private DatabaseReference firebaseRef;


    private String[] permissions = new String[] {
            Manifest.permission.POST_NOTIFICATIONS
    };

    private boolean permission_post_notification = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
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
       View view = inflater.inflate(R.layout.fragment_feed, container, false);


        // configurações iniciais
        verificarPermissaoNotificacao();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        feedRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("postagem_publica");
                //.child(idUsuarioLogado);

        recuperarDadosUsuario();

        // inicializar componentes
        adapterFeed = new AdapterFeed(listaFeed, getActivity());
        recyclerFeed = view.findViewById(R.id.recyclerFeed);
        recyclerFeed.setHasFixedSize(true);
        recyclerFeed.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerFeed.setAdapter(adapterFeed);
        progressBarFeed = view.findViewById(R.id.progressBarFeed);
        textSemPostagem = view.findViewById(R.id.textSemPostagem);


       return  view;
    }

          // recuperar feed
    private void listarFeed(){
         // limpar listar
        listaFeed.clear();

        // carregamento dados usuarios
        progressBarFeed.setVisibility(View.VISIBLE);
        valueEventListenerFeed = feedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot ds: snapshot.getChildren()){
                        listaFeed.add(ds.getValue(Feed.class));
                    }
                    // ordenar por postagens recente
                    Collections.reverse(listaFeed);
                    adapterFeed.notifyDataSetChanged();
                    progressBarFeed.setVisibility(View.GONE);
                }else{
                  textSemPostagem.setVisibility(View.VISIBLE);
                    progressBarFeed.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private  void recuperarDadosUsuario(){
        DatabaseReference usuarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuarioLogado);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.getValue() != null){
                    Usuario usuario = snapshot.getValue(Usuario.class);
                    // inicio re-cadastro do token usuario
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w("Cadastro token", "Fetching FCM registration token failed", task.getException());
                                        return;
                                    }
                                    // Get new FCM registration token
                                    String token = task.getResult();
                                    usuario.setTokenUsuario(token);
                                    usuario.atualizar(); // atualizar token toda vez que entrar no app

                                }
                            });    // fim re-cadastro do token

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        listarFeed();
    }


    @Override
    public void onStop() {
        super.onStop();
        feedRef.removeEventListener(valueEventListenerFeed);
    }


    public void verificarPermissaoNotificacao() {
        if (!permission_post_notification) {
            permissaoNotificacao();
        }else {
            Toast.makeText(getContext(), "Notificação permitida", Toast.LENGTH_SHORT).show();
        }
    }

    public void permissaoNotificacao() {
        if (ContextCompat.checkSelfPermission(getContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            permission_post_notification = true;
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    Log.d("Permission", "inside else first time dont allow");
                }else {
                    Log.d("Permission", "inside else 2nd time dont allow");
                }

                requestPermissionLauncherNotification.launch(permissions[0]);
            }
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncherNotification =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
                if (result) {
                    permission_post_notification = true;
                }else {
                    permission_post_notification = false;
                    showPermissionDialog();
                }
            });

    private void showPermissionDialog() {
        startActivity(new Intent(getContext(), PermissaoNotificacaoActivity.class));
    }

}