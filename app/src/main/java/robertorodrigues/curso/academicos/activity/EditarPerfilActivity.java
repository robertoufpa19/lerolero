package robertorodrigues.curso.academicos.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.Permissoes;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;
import robertorodrigues.curso.academicos.model.Usuario;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView imageEditarPerfil;
    private TextView textAlterarFoto;
    private TextInputEditText editarNomePerfil, editarEmailPerfil;
    private Button buttonSalvarAlteracoesPerfil;

    private Usuario usuarioLogado;

    private static  final int SELECAO_GALERIA = 200;
    private String urlImagemSelecionada = null;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private AlertDialog dialog;
    private StorageReference storageReference;


    private String[] permissoesNecessarias = new String[]{

            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar Perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // configura botao voltar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_fechar_24); // conficurar um botao personalizado

        // validar permissões
        Permissoes.validarPermissoes(permissoesNecessarias, this, 1);

          inicializarComponentes();

          //configurações iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();


          // recuperar dados do usuario
       /* FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        editarNomePerfil.setText(usuarioPerfil.getDisplayName());
        editarEmailPerfil.setText(usuarioPerfil.getEmail());

        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null){
            Picasso.get()
                    .load(url)
                    .into(imageEditarPerfil);
        }else{
            imageEditarPerfil.setImageResource(R.drawable.perfil);
        } */

        recuperarDadosUsuario();



        //salvar alterações do usuário
        buttonSalvarAlteracoesPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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
                                usuarioLogado.setTokenUsuario(token);
                                String nomeAtualizado = editarNomePerfil.getText().toString();
                                // atualizar nome no perfil
                                UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);
                                // atualizar nome no banco de dados
                                usuarioLogado.setNomeUsuario(nomeAtualizado);
                                usuarioLogado.atualizar(); // atualizar dados do perfil
                                // falta atualizar o nome no feed

                                // String foto = urlImagemSelecionada;
              /*  if(url != null){
                 UsuarioFirebase.atualizarFotoUsuario(url);
                }else{
                   exibirMensagem("Configure uma foto de perfil!");
                } */
                                if(urlImagemSelecionada != null){
                                    UsuarioFirebase.atualizarFotoUsuario(Uri.parse(urlImagemSelecionada));
                                }else{
                                    exibirMensagem("Configure uma foto de perfil!");
                                }

                                exibirMensagem("Dados atualizado!");
                                finish(); // inalizar a activity

                            }
                        });    // fim re-cadastro do token

            }
        });



        // selecionar foto de perfil da galeria
        imageEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                if(i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);

                }

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
                    editarNomePerfil.setText(usuario.getNomeUsuario());
                    editarEmailPerfil.setText(usuario.getEmailUsuario());

                    //recuperar imagem de perfil da empresa
                    urlImagemSelecionada = usuario.getFotoUsuario();
                    if (  urlImagemSelecionada != null ){ // urlImagemSelecionada != ""
                        Picasso.get()
                                .load(urlImagemSelecionada)
                                .into(imageEditarPerfil);
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // selecionar foto de perfil da galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){
                    case  SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem);
                        break;
                }

                if(imagem != null){
                    imageEditarPerfil.setImageBitmap(imagem);

                    // fazer upload da imagem para o firebase storage
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // fazer upload da imagem antes de preencher outros campos
                    dialog = new SpotsDialog.Builder()
                            .setContext(this)
                            .setMessage("Carregando dados")
                            .setCancelable(false)
                            .build();
                    dialog.show();

                    final StorageReference imageRef = storageReference
                            .child("imagens")
                            .child("usuarios")
                            .child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            exibirMensagem("Erro ao fazer upload da imagem!");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // recupera a url da imagem (versao atualizada do firebase)
                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url =   task.getResult();
                                    urlImagemSelecionada = url.toString();
                                    atualizarFotoUsuario(Uri.parse(urlImagemSelecionada));
                                    // falta atualizar a foto no feed

                                }
                            });

                            dialog.dismiss(); // fecha o carregando

                           exibirMensagem("Clique no botão Salvar Alterações!");
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void atualizarFotoUsuario(Uri url){
        UsuarioFirebase.atualizarFotoUsuario(url);
          // atualiza foto no perfil
        boolean retorno = UsuarioFirebase.atualizarFotoUsuario(url);
        if(retorno){
            // atualizar foto no Firebase banco
            usuarioLogado.setFotoUsuario(url.toString());
            usuarioLogado.atualizar();
            // exibirMensagem("foto alterada!");
        }

    }

    // evento de clique no botão voltar (X)
    @Override
    public boolean onSupportNavigateUp() {
             finish();
        return false;
    }

    public void inicializarComponentes(){
        imageEditarPerfil = findViewById(R.id.imageEditarPerfil);
        textAlterarFoto = findViewById(R.id.textAlterarFoto);
        editarNomePerfil = findViewById(R.id.editNomePerfil);
        editarEmailPerfil = findViewById(R.id.editEmailPerfil);
        buttonSalvarAlteracoesPerfil = findViewById(R.id.buttonSalvarAlteracaoPerfil);
        // evento de clique no campo email
        editarEmailPerfil.setFocusable(false); // nao vai poder selecionar esse campo de email
        editarEmailPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exibirMensagem("Você não pode alterar o email de cadastro!");
            }
        });
    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}