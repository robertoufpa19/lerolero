package robertorodrigues.curso.academicos.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.activity.ChatActivity;
import robertorodrigues.curso.academicos.activity.EditarPerfilActivity;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;

/**

 * create an instance of this fragment.
 */
public class PerfilAmigoFragment extends Fragment {


    private ProgressBar progressBarAmigo;
    private CircleImageView imagePerfilAmigo;
    private GridView gridViewperfilAmigo;
    private Button buttonSeguirPerfil;
    private TextView textPublicacoesAmigo, textSeguidoresAmigo, textSeguindoAmigo;

    private AlertDialog dialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilAmigoFragment() {
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
        View view = inflater.inflate(R.layout.fragment_perfil_amigo, container, false);


        // configuracoes dos componentes
        gridViewperfilAmigo = view.findViewById(R.id.gridViewPerfilAmigo);
        progressBarAmigo = view.findViewById(R.id.progressBarPerfilAmigo);
        imagePerfilAmigo = view.findViewById(R.id.imagePerfilAmigo);
        textPublicacoesAmigo = view.findViewById(R.id.textPublicacoesAmigo);
        textSeguidoresAmigo = view.findViewById(R.id.textSeguidoresAmigo);
        textSeguindoAmigo = view.findViewById(R.id.textSeguindoAmigo);
        buttonSeguirPerfil = view.findViewById(R.id.buttonSeguirPerfil);


        // recuperar foto de perfil do usuario amigo
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null){
            Picasso.get()
                    .load(url)
                    .into(imagePerfilAmigo);
        }else{
            imagePerfilAmigo.setImageResource(R.drawable.perfil);
        }

        // seguir perfil amigo
        buttonSeguirPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return view;
    }


    private void exibirMensagem(String texto){
        Toast.makeText(getActivity(), texto, Toast.LENGTH_SHORT).show();
    }
}