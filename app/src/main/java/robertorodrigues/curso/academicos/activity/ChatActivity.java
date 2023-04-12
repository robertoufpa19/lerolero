package robertorodrigues.curso.academicos.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ortiz.touchview.TouchImageView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.adapter.MensagensAdapter;
import robertorodrigues.curso.academicos.api.NotificacaoService;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;
import robertorodrigues.curso.academicos.model.Conversa;
import robertorodrigues.curso.academicos.model.DataAtual;
import robertorodrigues.curso.academicos.model.HoraAtual;
import robertorodrigues.curso.academicos.model.Mensagem;
import robertorodrigues.curso.academicos.model.Notificacao;
import robertorodrigues.curso.academicos.model.NotificacaoDados;
import robertorodrigues.curso.academicos.model.Usuario;

public class ChatActivity extends AppCompatActivity {

    private TextView textViewNome;
    private ImageView circleImageViewFoto;


    private Usuario usuarioDestinatario;
    private Usuario usuarioRemetente;

    private String idUsuarioRemetente;
    private String  idUsuarioDestinatario;

    private EditText editMensagem;

    // recuperar mensagens
    private DatabaseReference database;
    private StorageReference storage;
    private DatabaseReference mensagensRef;
    private ChildEventListener childEventListenerMensagens;

    private List<Mensagem> mensagens = new ArrayList<>();
    private MensagensAdapter adapter;

    private RecyclerView recyclerMensagens;
    // variares de clique
    private ImageView imageCamera;
    private static final int SELECAO_CAMERA = 100;
    private ImageView imageGaleria;
    private static final int SELECAO_GALERIA = 200;
    private ImageView imageArquivo;
    private static final int SELECAO_ARQUIVO = 300;

    private String baseUrl;
    private Retrofit retrofit;

    private String nomeUsuario, fotoUsuario;
    private DatabaseReference usuarioRef;

    // notificação
    private String token;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //  getSupportActionBar().hide();

        // configuracoes iniciais
        textViewNome = findViewById(R.id.textViewNomeChat);
        circleImageViewFoto = findViewById(R.id.circleImageFotoChat);
        editMensagem = findViewById(R.id.editMensagem);
        recyclerMensagens = findViewById(R.id.recyclerMensagens);
        imageCamera = findViewById(R.id.imageCamera); // recupera foto tirada na hora para enviar no chat
        imageGaleria      = findViewById(R.id.imageGaleria);// recupera foto da galeria para enviar no chat
        imageArquivo      = findViewById(R.id.imageArquivo);// recupera pdf da galeria para enviar no chat


        //recupera dados do usuario remetente id
        idUsuarioRemetente = UsuarioFirebase.getIdUsuario();
        usuarioRemetente =  UsuarioFirebase.getDadosUsuarioLogado();

        // anuncioUsuarioSelecionado = UsuarioFirebase.getDadosUsuarioLogado();
        // usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase();


        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            if(bundle.containsKey("usuarioSelecionadoAmigo")){

                usuarioDestinatario  = (Usuario) bundle.getSerializable("usuarioSelecionadoAmigo");
                nomeUsuario = usuarioDestinatario.getNomeUsuario();
                // idUsuarioDestinatario = UsuarioFirebase.getIdUsuario();
                fotoUsuario = usuarioDestinatario.getFotoUsuario();

                textViewNome.setText(nomeUsuario);
                if(fotoUsuario != null){
                    Picasso.get()
                            .load(fotoUsuario)
                            .into(circleImageViewFoto);
                }else{
                    circleImageViewFoto.setImageResource(R.drawable.perfil);
                }


                idUsuarioDestinatario = usuarioDestinatario.getIdUsuario();

                recuperarTokenDestinatario();

            }else  if(bundle.containsKey("chat")){

                usuarioDestinatario  = (Usuario) bundle.getSerializable("chat");

                nomeUsuario = usuarioDestinatario.getNomeUsuario();
                // idUsuarioDestinatario = UsuarioFirebase.getIdUsuario();
                fotoUsuario = usuarioDestinatario.getFotoUsuario();

                textViewNome.setText(nomeUsuario);
                if(fotoUsuario != null){
                    Picasso.get()
                            .load( Uri.parse(fotoUsuario))
                            .into(circleImageViewFoto);
                }else{
                    circleImageViewFoto.setImageResource(R.drawable.perfil);
                }


                idUsuarioDestinatario = usuarioDestinatario.getIdUsuario();

                recuperarTokenDestinatario();

                // compartilhar arquivos
            }else if (bundle.containsKey("compartilharImagem") ) {
                // recuperar dados da empresa que mandou mensagem  ou usuario
                Conversa conversaSelecionada = (Conversa) getIntent().getParcelableExtra("compartilharImagem");
                idUsuarioDestinatario = conversaSelecionada.getIdDestinatario();
                nomeUsuario = conversaSelecionada.getNomeUsuario();
                fotoUsuario = conversaSelecionada.getFotoUsuario();
                textViewNome.setText(nomeUsuario);
                if (fotoUsuario.equals("")) {
                    Log.d("null ", "foto vazia");

                } else {
                    Picasso.get()
                            .load(fotoUsuario)
                            .into(circleImageViewFoto);
                }
                // salvar dados usuario remetente
                nomeUsuario = conversaSelecionada.getNomeUsuario();
                idUsuarioRemetente = conversaSelecionada.getIdRemetente();

                DatabaseReference databaseReferenceEmpresa = usuarioRef
                        .child("usuario")
                        .child(idUsuarioRemetente)
                        .child("urlImagem");
                databaseReferenceEmpresa.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String fotoUsuarioRef = snapshot.getValue().toString();
                        fotoUsuario = fotoUsuarioRef;

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                recuperarTokenDestinatario();

                ViewGroup viewGroup = findViewById(android.R.id.content);
                ImageView  buttonNao,buttonSim;
                TouchImageView imagemCompartilhada;
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View viewLayout = LayoutInflater.from(this)
                        .inflate(R.layout.dialog_layout_compartilhar_arquivo, viewGroup, false);
                builder.setView(viewLayout);
                builder.setCancelable(true);
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        finish();
                    }
                });

                buttonSim = viewLayout.findViewById(R.id.buttonCompartilharDialog);
                buttonNao = viewLayout.findViewById(R.id.buttonNaoCompatilharDialog);
                imagemCompartilhada = viewLayout.findViewById(R.id.compartilharArquivoImage);
                final AlertDialog dialogLayout = builder.create();

                dialogLayout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Picasso.get()
                        .load(conversaSelecionada.getArquivoCompartilhado())
                        .into(imagemCompartilhada);
                buttonSim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Bitmap imagem = null;
                        try {
                            imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), conversaSelecionada.getArquivoCompartilhado());


                            if (imagem != null) {
                                //  imageGaleria.setImageBitmap(imagem);
                                //  imageGaleria.setImageBitmap(imagem);
                                final ProgressDialog progressDialogImage = new ProgressDialog(ChatActivity.this);
                                progressDialogImage.setTitle("Enviando...");
                                progressDialogImage.show();
                                // recuperar dados da imagem para o firebase
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                                byte[] dadosImagem = baos.toByteArray();
                                // cria nome que não se repete
                                String nomeImagem = UUID.randomUUID().toString();
                                // configurar referencia do firebase
                                final StorageReference imageRef = storage.child("imagens")
                                        .child("fotos")
                                        .child(idUsuarioRemetente)
                                        .child(nomeImagem);
                                UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("Erro", "Erro ao fazer upload");

                                        //Toast.makeText(ChatActivity.this, "Erro ao fazer upload da imagem!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // String dowloadUrl = taskSnapshot.getDownloadUrl().toString();
                                        //teste para nova versão
                                        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                String dowloadUrl = task.getResult().toString();
                                                Mensagem mensagem = new Mensagem();
                                                mensagem.setIdUsuario(idUsuarioRemetente);
                                                //mensagem.setMensagem("imagem.jpeg");
                                                mensagem.setImagem(dowloadUrl);
                                                mensagem.setHoraConversa(HoraAtual.horaAtual());
                                                mensagem.setDataConversa(DataAtual.dataAtual());
                                                //Salvar mensagem para o remetente
                                                salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                                                //Salvar mensagem para o destinatario(falta salvar pro destinatario)
                                                salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                                                //Salvar conversa para o remetente
                                                // tem que recuperar usuario exibicao
                                                salvarConversa( idUsuarioRemetente, idUsuarioDestinatario, usuarioDestinatario,  mensagem, false);
                                                //Salvar conversa para o destinatario
                                                salvarConversa(  idUsuarioDestinatario, idUsuarioRemetente, usuarioRemetente,  mensagem, true);

                                                enviarNotificacao();
                                                progressDialogImage.dismiss();
                                                dialogLayout.dismiss();
                                                finish();
                                            }
                                        });
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                                        double progresso = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                        progressDialogImage.setMessage("Enviando: " + (int) progresso + "" + "%");
                                    }
                                });

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

                buttonNao.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogLayout.dismiss();
                        finish();
                    }
                });

                dialogLayout.show();


            } else if (bundle.containsKey("compartilharPDF")) {

                // recuperar dados da empresa que mandou mensagem  ou usuario
                Conversa conversaSelecionada = (Conversa) getIntent().getParcelableExtra("compartilharPDF");
                idUsuarioDestinatario = conversaSelecionada.getIdDestinatario();
                nomeUsuario = conversaSelecionada.getNomeUsuario();
                fotoUsuario = conversaSelecionada.getFotoUsuario();
                textViewNome.setText(nomeUsuario);
                if (fotoUsuario.equals("")) {
                    Log.d("null ", "foto vazia");

                } else {
                    Picasso.get()
                            .load(fotoUsuario)
                            .into(circleImageViewFoto);
                }
                // salvar dados usuario remetente
                nomeUsuario = conversaSelecionada.getNomeUsuario();
                idUsuarioRemetente = conversaSelecionada.getIdRemetente();

                DatabaseReference databaseReferenceEmpresa = usuarioRef
                        .child("usuario")
                        .child(idUsuarioRemetente)
                        .child("urlImagem");
                databaseReferenceEmpresa.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String fotoUsuarioRef = snapshot.getValue().toString();
                        fotoUsuario = fotoUsuarioRef;

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                recuperarTokenDestinatario();

                // compartilhar pdf
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Enviando...");
                progressDialog.show();

                //StorageReference reference = storage.child("/arquivos/"+System.currentTimeMillis()+".pdf");

                // cria nome que não se repete
                String nomePDF = UUID.randomUUID().toString();
                // configurar referencia do firebase
                final StorageReference documentoRef = storage.child("arquivos")
                        .child("pdf")
                        .child(idUsuarioRemetente)
                        .child(nomePDF + (".pdf"));

                documentoRef.putFile(conversaSelecionada.getArquivoCompartilhado()).addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isComplete()) ;
                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario(idUsuarioRemetente);
                    mensagem.setHoraConversa(HoraAtual.horaAtual());
                    mensagem.setDataConversa(DataAtual.dataAtual());
                    mensagem.setArquivo(nomePDF + (".pdf"));
                    //Salvar mensagem para o remetente
                    salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                    //Salvar mensagem para o destinatario
                    salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                    //Salvar conversa para o remetente
                    // tem que recuperar usuario exibicao
                    salvarConversa( idUsuarioRemetente, idUsuarioDestinatario, usuarioDestinatario,  mensagem, false);
                    //Salvar conversa para o destinatario
                    salvarConversa(  idUsuarioDestinatario, idUsuarioRemetente, usuarioRemetente,  mensagem, true);


                    enviarNotificacao();
                    progressDialog.dismiss();
                    finish();
                }).addOnProgressListener(snapshot -> {
                    double progresso = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("Enviando: " + (int) progresso + "");
                });


            }


        }


        //Configuração adapter
        adapter = new MensagensAdapter(mensagens, getApplicationContext() );

        //Configuração recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMensagens.setLayoutManager( layoutManager );
        recyclerMensagens.setHasFixedSize( true );
        recyclerMensagens.setAdapter( adapter );

        database = ConfiguracaoFirebase.getFirebaseDatabase();
        storage = ConfiguracaoFirebase.getFirebaseStorage();
        mensagensRef = database.child("mensagens")
                .child( idUsuarioRemetente )
                .child( idUsuarioDestinatario );

        // evento de click na camera do chat
        imageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if(intent.resolveActivity(getPackageManager())!= null){
                    startActivityForResult(intent, SELECAO_CAMERA);
                }
            }
        });
        // evento de click na galeria do chat

        imageGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager())!= null){
                    startActivityForResult(intent, SELECAO_GALERIA);
                }
            }
        });


        imageArquivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               Intent intent = new Intent();
               intent.setType("application/pdf");
               intent.setAction(Intent.ACTION_GET_CONTENT);
               startActivityForResult(Intent.createChooser(intent, "Seleção de arquivo"), 300);
            }
        });



        //Configuração da retrofit para enviar requisição ao firebase e então para ele enviar a notificação
        baseUrl = "https://fcm.googleapis.com/fcm/";
        retrofit = new Retrofit.Builder()
                .baseUrl( baseUrl )
                .addConverterFactory(GsonConverterFactory.create())
                .build();


    }


    public void enviarMensagem(View view) {

        Bundle bundleEnviarMensagem = getIntent().getExtras();

        if(bundleEnviarMensagem != null){
            String textoMensagem = editMensagem.getText().toString();
            if ( !textoMensagem.isEmpty() ){
                if(bundleEnviarMensagem.containsKey("usuarioSelecionadoAmigo")){ // se o usuario selecionar o botao enviar mensagens ao vendedor
                    usuarioDestinatario = (Usuario) bundleEnviarMensagem.getSerializable("usuarioSelecionadoAmigo");

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario( idUsuarioRemetente );
                    mensagem.setMensagem( textoMensagem );

                    //Salvar mensagem para o remetente
                    salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                    //Salvar mensagem para o destinatario(falta salvar pro destinatario)
                    salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                    //Salvar conversa para o remetente
                    salvarConversa( idUsuarioRemetente, idUsuarioDestinatario, usuarioDestinatario,  mensagem, false);


                    //Salvar conversa para o destinatario
                    salvarConversa(  idUsuarioDestinatario, idUsuarioRemetente,usuarioRemetente ,  mensagem, true);



                }else if(bundleEnviarMensagem.containsKey("chat")){ // se caso o usuario abra o menu de conversas
                    usuarioDestinatario = (Usuario) bundleEnviarMensagem.getSerializable("chat");


                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario( idUsuarioRemetente );
                    mensagem.setMensagem( textoMensagem );

                    //Salvar mensagem para o remetente
                    salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                    //Salvar mensagem para o destinatario(falta salvar pro destinatario)
                    salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);

                    //Salvar conversa para o remetente                  // tem que recuperar usuario exibicao
                    salvarConversa( idUsuarioRemetente, idUsuarioDestinatario, usuarioDestinatario,  mensagem, false);


                    //Salvar conversa para o destinatario
                    salvarConversa(  idUsuarioDestinatario, idUsuarioRemetente, usuarioRemetente,  mensagem, true);


                }

            }else{
                exibirMensagem("Digite Sua Mensagem!");
            }

        }

        enviarNotificacao();

    }

    private void salvarMensagem(String idRemetente, String idDestinatario, Mensagem msg){

        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference mensagemRef = database.child("mensagens");

        mensagemRef.child(idRemetente)
                .child(idDestinatario)
                .push()
                .setValue(msg);

        //Limpar texto
        editMensagem.setText("");

    }


    // salva conversa para a empresa
    private void salvarConversa(String idRemetente, String idDestinatario,Usuario usuarioExibição,  Mensagem msg, boolean novaMensagem){

        //Salvar conversa remetente
        Conversa conversaRemetente = new Conversa();
        conversaRemetente.setIdRemetente( idRemetente);
        conversaRemetente.setIdDestinatario( idDestinatario );
        conversaRemetente.setUltimaMensagem(msg.getMensagem());

        if(usuarioExibição.getFotoUsuario() == null){
            conversaRemetente.setFotoUsuario(null);
        }else{
            conversaRemetente.setFotoUsuario(usuarioExibição.getFotoUsuario());
        }

        conversaRemetente.setNomeUsuario(usuarioExibição.getNomeUsuario());
        conversaRemetente.setUsuarioExibicao(usuarioExibição);

        // salvar  mensagens novas
        conversaRemetente.setNovaMensagem(String.valueOf(novaMensagem));


        conversaRemetente.salvarConversa();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        recuperarTokenDestinatario();

        if(resultCode == RESULT_OK){
            Bitmap imagem = null;
            try {
                switch (requestCode){
                    case SELECAO_CAMERA:
                        imagem =(Bitmap) data.getExtras().get("data");
                        break;

                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);

                        break;

                    case SELECAO_ARQUIVO:

                        if (data != null && data.getData() != null){
                            enviarArquivo(data.getData());
                        }

                        break;
                }

                if(imagem != null){

                    //  imageGaleria.setImageBitmap(imagem);
                    final ProgressDialog progressDialogImage = new ProgressDialog(this);
                    progressDialogImage.setTitle("Enviando...");
                    progressDialogImage.show();

                    // recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // cria nome que não se repete
                    String nomeImagem = UUID.randomUUID().toString();

                    // configurar referencia do firebase
                    final StorageReference imageRef = storage.child("imagens")
                            .child("fotos")
                            .child(idUsuarioRemetente)
                            .child(nomeImagem);

                    UploadTask uploadTask = imageRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("Erro", "Erro ao fazer upload");

                            Toast.makeText(ChatActivity.this,
                                    "Erro ao fazer upload da imagem!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // String dowloadUrl = taskSnapshot.getDownloadUrl().toString();

                            //teste para nova versão
                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String dowloadUrl =  task.getResult().toString();

                                    Bundle bundleEnviarMensagem = getIntent().getExtras();


                                    if(bundleEnviarMensagem != null){

                                        if(bundleEnviarMensagem.containsKey("usuarioSelecionadoAmigo")){
                                            usuarioDestinatario  = (Usuario) bundleEnviarMensagem.getSerializable("usuarioSelecionadoAmigo");

                                            Mensagem mensagem = new Mensagem();
                                            mensagem.setIdUsuario( idUsuarioRemetente );
                                            mensagem.setMensagem("imagem.jpeg");
                                            mensagem.setImagem(dowloadUrl);

                                            //Salvar mensagem para o remetente
                                            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                                            //Salvar mensagem para o destinatario(falta salvar pro destinatario)
                                            salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);


                                            //Salvar conversa para o remetente
                                            salvarConversa( idUsuarioRemetente, idUsuarioDestinatario, usuarioDestinatario,  mensagem, false);

                                            //Salvar conversa para o destinatario
                                            salvarConversa(  idUsuarioDestinatario, idUsuarioRemetente,usuarioRemetente ,  mensagem, true);

                                            enviarNotificacao();
                                            progressDialogImage.dismiss();


                                        } else if(bundleEnviarMensagem.containsKey("chat")){
                                            usuarioDestinatario  = (Usuario) bundleEnviarMensagem.getSerializable("chat");


                                            Mensagem mensagem = new Mensagem();
                                            mensagem.setIdUsuario( idUsuarioRemetente );
                                            mensagem.setMensagem("imagem.jpeg");
                                            mensagem.setImagem(dowloadUrl);

                                            //Salvar mensagem para o remetente
                                            salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                                            //Salvar mensagem para o destinatario(falta salvar pro destinatario)
                                            salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);


                                            //Salvar conversa para o remetente
                                            // tem que recuperar usuario exibicao
                                            salvarConversa( idUsuarioRemetente, idUsuarioDestinatario, usuarioDestinatario,  mensagem, false);

                                            //Salvar conversa para o destinatario
                                            salvarConversa(  idUsuarioDestinatario, idUsuarioRemetente, usuarioRemetente,  mensagem, true);

                                            enviarNotificacao();
                                            progressDialogImage.dismiss();

                                        }





                                    }


                                }
                            });



                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progresso = (100.0* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                            progressDialogImage.setMessage("Enviando: "+ (int)progresso + "" + "%");
                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    private void enviarArquivo(Uri data) {
         final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Enviando...");
        progressDialog.show();

        //StorageReference reference = storage.child("/arquivos/"+System.currentTimeMillis()+".pdf");

        // cria nome que não se repete
        String nomePDF = UUID.randomUUID().toString();
        // configurar referencia do firebase
        final StorageReference documentoRef = storage.child("arquivos")
                .child("pdf")
                .child(idUsuarioRemetente)
                .child(nomePDF+(".pdf"));

        documentoRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                       Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                       while (!uriTask.isComplete());
                       String dowloadUrl = uriTask.getResult().toString();

                        Bundle bundleEnviarMensagem = getIntent().getExtras();

                        if(bundleEnviarMensagem != null) {
                            if (bundleEnviarMensagem.containsKey("usuarioSelecionadoAmigo")) {
                                usuarioDestinatario = (Usuario) bundleEnviarMensagem.getSerializable("usuarioSelecionadoAmigo");

                                Mensagem mensagem = new Mensagem();
                                mensagem.setIdUsuario( idUsuarioRemetente );
                                mensagem.setArquivo(nomePDF+(".pdf"));

                                //Salvar mensagem para o remetente
                                salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                                //Salvar mensagem para o destinatario(falta salvar pro destinatario)
                                salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);


                                //Salvar conversa para o remetente
                                // tem que recuperar usuario exibicao
                                salvarConversa( idUsuarioRemetente, idUsuarioDestinatario, usuarioDestinatario,  mensagem, false);

                                //Salvar conversa para o destinatario
                                salvarConversa(  idUsuarioDestinatario, idUsuarioRemetente, usuarioRemetente,  mensagem, true);

                                enviarNotificacao();

                            }else if(bundleEnviarMensagem.containsKey("chat")) {
                                usuarioDestinatario = (Usuario) bundleEnviarMensagem.getSerializable("chat");

                                Mensagem mensagem = new Mensagem();
                                mensagem.setIdUsuario( idUsuarioRemetente );
                                mensagem.setArquivo(nomePDF+(".pdf"));

                                //Salvar mensagem para o remetente
                                salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);

                                //Salvar mensagem para o destinatario(falta salvar pro destinatario)
                                salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);


                                //Salvar conversa para o remetente
                                // tem que recuperar usuario exibicao
                                salvarConversa( idUsuarioRemetente, idUsuarioDestinatario, usuarioDestinatario,  mensagem, false);

                                //Salvar conversa para o destinatario
                                salvarConversa(  idUsuarioDestinatario, idUsuarioRemetente, usuarioRemetente,  mensagem, true);

                                enviarNotificacao();

                               }
                            }




                       progressDialog.dismiss();

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        double progresso = (100.0* snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                        progressDialog.setMessage("Enviando: "+ (int)progresso+"%");
                    }
                });

    }





    @Override
    protected void onStart() {
        super.onStart();
        recuperarMensagens();
        recuperarTokenDestinatario();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mensagensRef.removeEventListener( childEventListenerMensagens );
    }

    private void recuperarMensagens(){

        mensagens.clear(); //teste
        recuperarTokenDestinatario();

        childEventListenerMensagens = mensagensRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Mensagem mensagem = dataSnapshot.getValue( Mensagem.class );
                mensagens.add( mensagem );
                adapter.notifyDataSetChanged();
                // da o foco na ultima mensagem enviada
                recyclerMensagens.scrollToPosition(mensagens.size() -1 );

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

    public  void  recuperarTokenDestinatario(){

        Bundle bundleToken = getIntent().getExtras();
        if(bundleToken  != null){
            if(bundleToken.containsKey("usuarioSelecionadoAmigo")){


                usuarioDestinatario = (Usuario) bundleToken.getSerializable("chat");
                // token = usuarioDestinatario.getTokenUsuario();
                // recuperar token do NO usuarios
                usuarioRef =  ConfiguracaoFirebase.getFirebaseDatabase()
                        .child("usuarios")
                        .child(idUsuarioDestinatario)
                        .child("tokenUsuario");
                usuarioRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String tokenUsuario =  snapshot.getValue().toString();
                        token = tokenUsuario;

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }else if(bundleToken.containsKey("chat")){


                usuarioDestinatario = (Usuario) bundleToken.getSerializable("chat");
                // token = usuarioDestinatario.getTokenUsuario();
                // recuperar token do NO usuarios
                usuarioRef =  ConfiguracaoFirebase.getFirebaseDatabase()
                        .child("usuarios")
                        .child(idUsuarioDestinatario)
                        .child("tokenUsuario");
                usuarioRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String tokenUsuario =  snapshot.getValue().toString();
                        token = tokenUsuario;

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        }
    }


    public void enviarNotificacao(){

        Bundle bundleNotificacao = getIntent().getExtras();
        if(bundleNotificacao.containsKey("usuarioSelecionadoAmigo")){
            usuarioDestinatario  = (Usuario) bundleNotificacao.getSerializable("usuarioSelecionadoAmigo");
            token = usuarioDestinatario.getTokenUsuario();

            String tokenDestinatario = token;
            String to = "";// para quem vou enviar a menssagem
            to = tokenDestinatario ;

            //Monta objeto notificação
            Notificacao notificacao = new Notificacao("Lero Lero", "Nova Mensagem de" + usuarioRemetente.getNomeUsuario());
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

        }else if(bundleNotificacao.containsKey("chat")){

            usuarioDestinatario  = (Usuario) bundleNotificacao.getSerializable("chat");
            // token = usuarioDestinatario.getTokenUsuario();
            // recuperar token do NO usuarios
            usuarioRef =  ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("usuarios")
                    .child(idUsuarioDestinatario)
                    .child("tokenUsuario");
            usuarioRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String tokenUsuario =  snapshot.getValue().toString();
                    token = tokenUsuario;

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            String tokenDestinatario = token;
            String to = "";// para quem vou enviar a menssagem
            to = tokenDestinatario ;

            //Monta objeto notificação
            Notificacao notificacao = new Notificacao("Lero Lero", "Nova Mensagem de" + usuarioRemetente.getNomeUsuario());
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

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }


}