package robertorodrigues.curso.academicos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.adapter.AdapterMiniaturas;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.RecyclerItemClickListener;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;
import robertorodrigues.curso.academicos.model.Postagem;
import robertorodrigues.curso.academicos.model.Usuario;

public class FiltroActivity extends AppCompatActivity {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private ImageView imageFotoEscolhida;
    private Bitmap imagem; // imagem original
    private Bitmap imagemFiltro; // imagem copia

    private List<ThumbnailItem> listaFiltros;
    private RecyclerView recyclerFiltros;
    private AdapterMiniaturas adapterMiniaturas;
    private TextInputEditText textDescricaoFiltro;

    private String idUsuarioLogado;
    private AlertDialog dialog;


    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioLogadoRef;
    private Usuario uLogado;

    private DataSnapshot seguidoresSnapshot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtro);

        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Filtros");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // configura botao voltar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_voltar_24); // conficurar um botao personalizado

        // configurações iniciais
        listaFiltros = new ArrayList<>();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        // busca informações do NO usuarios no firebase
        usuariosRef = firebaseRef.child("usuarios");
       // uLogado = UsuarioFirebase.getDadosUsuarioLogado();


        // inicializar componentes
        imageFotoEscolhida = findViewById(R.id.imageFotoEscolhida);
        recyclerFiltros = findViewById(R.id.recyclerFiltros);
        textDescricaoFiltro = findViewById(R.id.textDescricaoFiltro);


        recuperarDadosUsuarioLogado();
        recuperarDadosPostagem();



        // recuperar a imagem escolhida pelo usuario
         Bundle bundle = getIntent().getExtras();
         if(bundle != null){
             byte[] dadosImagem = bundle.getByteArray("fotoSelecionada");
                                                               // offset: 0 - recebe a imagem completa
             imagem = BitmapFactory.decodeByteArray(dadosImagem, 0, dadosImagem.length);
             imageFotoEscolhida.setImageBitmap(imagem);

             // imagem filtro sempre recebe a imagem original
             imagemFiltro = imagem.copy(imagem.getConfig(), true);


             // configurar recycler de filtros
             adapterMiniaturas = new AdapterMiniaturas(listaFiltros, getApplicationContext());
                                                                                             // exibir as imagems em horizontal
             RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
             recyclerFiltros.setLayoutManager(layoutManager);
             recyclerFiltros.setAdapter(adapterMiniaturas);

             // adicionar evento de clique no recyclerView
             recyclerFiltros.addOnItemTouchListener(
                     new RecyclerItemClickListener(
                             getApplicationContext(),
                             recyclerFiltros,
                             new RecyclerItemClickListener.OnItemClickListener() {
                                 @Override
                                 public void onItemClick(View view, int position) {

                                     ThumbnailItem item = listaFiltros.get(position);
                                     Filter filtro = item.filter;

                                     /// filtro na imagem(copia)
                                 imagemFiltro = imagem.copy(imagem.getConfig(), true);
                                 imageFotoEscolhida.setImageBitmap(filtro.processFilter(imagemFiltro));

                                 }

                                 @Override
                                 public void onLongItemClick(View view, int position) {

                                 }

                                 @Override
                                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                 }
                             }
                     ));

             //recuperar filtros
              recuperarFiltros();


         }

    }



    private void recuperarDadosPostagem(){

        // carregamento da imagem para a postagem
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando Dados Seguidores")
                .setCancelable(false)
                .build();
        dialog.show();

        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //recupera dados do usuario logado
                        uLogado = snapshot.getValue(Usuario.class); // recebe todas as informações do NO usuarios

                        // recuperar seguidores
                        // se tiver muitos seguidores pode demorar muito tempo para posta a foto
                        DatabaseReference seguidoresRef = firebaseRef
                                .child("seguidores")
                                .child(idUsuarioLogado);
                        seguidoresRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                seguidoresSnapshot = snapshot;


                                dialog.dismiss();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }


    private void recuperarDadosUsuarioLogado(){


        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //recupera dados do usuario logado
                        uLogado = snapshot.getValue(Usuario.class); // recebe todas as informações do NO usuarios

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }


    private void recuperarFiltros(){

        // limpar itens
        ThumbnailsManager.clearThumbs();
        listaFiltros.clear();

        // configurar filtro normal
        ThumbnailItem item = new ThumbnailItem();
        item.image = imagem;
        item.filterName = "Normal";
        ThumbnailsManager.addThumb(item);

        // listar todos os filtros
        List<Filter> filtros = FilterPack.getFilterPack(getApplicationContext());
        for(Filter filtro: filtros){

            ThumbnailItem itemFiltro = new ThumbnailItem();
            itemFiltro.image = imagem;
            itemFiltro.filter = filtro;
            itemFiltro.filterName = filtro.getName();  // nome do filtro

            ThumbnailsManager.addThumb(itemFiltro);
        }
                 /// adicionando as miniaturas de filtros
        listaFiltros.addAll(ThumbnailsManager.processThumbs(getApplicationContext()));
        adapterMiniaturas.notifyDataSetChanged();

    }


    private void publicarPostagem(){


        Postagem postagem = new Postagem();
        postagem.setIdUsuario(idUsuarioLogado);
        postagem.setNomeUsuario(uLogado.getNomeUsuario());
        postagem.setFotoUsuario(uLogado.getFotoUsuario());
        postagem.setDescricao(textDescricaoFiltro.getText().toString());

        // recuperar dados da imagem para o firebase
        // fazer upload da imagem para o firebase storage
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagemFiltro.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] dadosImagem = baos.toByteArray();

        // carregamento da imagem para a postagem
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Publicando")
                .setCancelable(false)
                .build();
        dialog.show();

        // salvar imagem no firebase storage
        StorageReference storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        final  StorageReference imagemRef = storageRef
                .child("imagens")
                .child("postagens")
                .child(postagem.getIdPostagem() + ".jpeg");

        UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                exibirMensagem("Erro ao salvar imagem!");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // recupera a url da imagem (versao atualizada do firebase)
                imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri url =   task.getResult();
                        postagem.setFotoPostagem(url.toString());
                        // atualizar quantidade de postagem
                        int quantidadeDePostagem = uLogado.getPostagens() + 1;
                        uLogado.setPostagens(quantidadeDePostagem);
                        uLogado.atualizarQtdPostagem();

                        // salvar postagem
                        if(postagem.salvarPostagem(seguidoresSnapshot)){ // se salvarPostagem   == true

                            exibirMensagem("Sucesso ao salvar Postagem!");
                            finish();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class)); // abrir a tela principal

                        }



                    }
                });

                dialog.dismiss(); // fecha o carregando
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_filtro, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_salvar_postagem:

                // salvar postagem
                 publicarPostagem();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;

    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}






