package robertorodrigues.curso.academicos.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.activity.CadastrarAnuncioActivity;
import robertorodrigues.curso.academicos.activity.FiltroActivity;
import robertorodrigues.curso.academicos.helper.Permissoes;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;

/*
 * Created by Roberto de Oliveira Rodrigues
 */

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostagemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostagemFragment extends Fragment {


    private CircleImageView imagemPerfilPostagem;
    private TextView nomePerfilPostagem;

    private ImageView imageCameraPostagem, imageGaleriaPostagem;

   // private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;

   private  Uri localImagemSelecionada;
    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostagemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostagemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostagemFragment newInstance(String param1, String param2) {
        PostagemFragment fragment = new PostagemFragment();
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
        View view= inflater.inflate(R.layout.fragment_postagem, container, false);


        // configurar componentes
        nomePerfilPostagem = view.findViewById(R.id.textPostagemNome1);
        imagemPerfilPostagem = view.findViewById(R.id.imagePostagemPerfil1);
        imageCameraPostagem = view.findViewById(R.id.imageCameraPostagem1);
        imageGaleriaPostagem =  view.findViewById(R.id.imageGaleriaPostagem1);

        Permissoes.validarPermissoes(permissoes, getActivity(), 1);


        // recuperar dados do usuario inicio
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        nomePerfilPostagem.setText(usuarioPerfil.getDisplayName());


        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null){
            Picasso.get()
                    .load(url)
                    .into(imagemPerfilPostagem);
        }else{
            imagemPerfilPostagem.setImageResource(R.drawable.perfil);
        }
    // recuperar dados do usuario fim

        // adicionar clique na camera
      /*  imageCameraPostagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                  if(i.resolveActivity(getActivity().getPackageManager()) != null){
                      startActivityForResult(i, SELECAO_CAMERA );

                  }

            }
        });*/
        // adicionar clique na galeria
        imageGaleriaPostagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, SELECAO_GALERIA);

              /*  Intent intent = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getActivity().getPackageManager())!= null){
                    startActivityForResult(intent, SELECAO_GALERIA);
                }*/

            }
        });

        return  view;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

          if(resultCode == getActivity().RESULT_OK){

              Bitmap imagem = null;

              try {


                  // validar tipo de selecão de imagem(camera ou galeria)
                  switch ( requestCode){
                     /* case SELECAO_CAMERA:
                          imagem =  (Bitmap) data.getExtras().get("data");
                          break;*/

                      case SELECAO_GALERIA:

                          if (data != null && data.getData() != null) {
                              localImagemSelecionada = data.getData();
                              imagem = MediaStore.Images.Media.getBitmap( getActivity().getContentResolver(), localImagemSelecionada);

                          }


                          break;
                  }

                  // validar imagem selecionada
                  if(imagem != null){
                      // converter imagem em byte array e comprimir

                      ByteArrayOutputStream baos = new ByteArrayOutputStream();

                      Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), localImagemSelecionada);
                      imageBitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                      byte[] dadosImagem = baos.toByteArray();


                      // enviar imagem escolhida para aplicação de filtro
                       Intent i = new Intent(getActivity(), FiltroActivity.class);
                       i.putExtra("fotoSelecionada", dadosImagem);
                       startActivity(i);
                  }


              }catch (Exception e){
                  e.printStackTrace();
              }


          }


    }



    @Override
    public void onStart() {
        super.onStart();

    }
}