package robertorodrigues.curso.academicos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;
import robertorodrigues.curso.academicos.model.Anuncio;
import robertorodrigues.curso.academicos.model.Usuario;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private TextView titulo, descricao, estado, preco, nomeUsuarioDestalhesProduto;
    private Anuncio anuncioSelecionado;
    private Usuario usuarioSelecionadoAnunciante;
    private ImageView imageUsuarioDetalhesProduto;
    private Button buttonIniciarConversa;

    private String idUsuarioLogado,  fotoPerfilVendedor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);

        // configurar toolbar
        //  getSupportActionBar().setTitle("Detalhes do Produto");
        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Detalhes do Produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // configura botao voltar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_fechar_24); // conficurar um botao personalizado
        // inicializar componentes
        inicializarComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        // recuperar anuncio para a exibicao
        anuncioSelecionado = (Anuncio) getIntent().getSerializableExtra("anuncioSelecionado");

        if(anuncioSelecionado != null){
            titulo.setText(anuncioSelecionado.getTitulo());
            descricao.setText(anuncioSelecionado.getDescricao());
            estado.setText(anuncioSelecionado.getEstado());
            preco.setText(anuncioSelecionado.getValor());
            nomeUsuarioDestalhesProduto.setText(anuncioSelecionado.getNomeVendedor());

            //configurar foto do vendedor
            DatabaseReference usuarioRef =  ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("usuarios")
                    .child(anuncioSelecionado.getIdUsuario())
                    .child("fotoUsuario");
            usuarioRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String fotoPerfilVendedorRef =  snapshot.getValue().toString();
                    fotoPerfilVendedor = fotoPerfilVendedorRef;
                    if(snapshot.exists()){
                        if(fotoPerfilVendedorRef != null){
                            Picasso.get()
                                    .load(Uri.parse(fotoPerfilVendedorRef))
                                    .into(imageUsuarioDetalhesProduto);
                        }else{
                            imageUsuarioDetalhesProduto.setImageResource(R.drawable.perfil);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

          //  String  urlFotoUsuario = anuncioSelecionado.getFotoVendedor();
          //  Picasso.get().load(urlFotoUsuario).into(imageUsuarioDetalhesProduto);


            ImageListener imageListener =  new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = anuncioSelecionado.getFotos().get(position);
                    Picasso.get().load(urlString).into(imageView);
                }
            };

            carouselView.setPageCount(anuncioSelecionado.getFotos().size()); // pega a quantidades de fotos
            carouselView.setImageListener(imageListener);
        }
        /// iniciar uma conversa


        abrirchat();


    }

    public void visualizarTelefone(View view){
        // abrir chamada
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", anuncioSelecionado.getTelefone(), null));
        startActivity(i);
    }


    public void abrirchat(){

        // condicao para o usuario nao enviar mensagem para si mesmo
        if(anuncioSelecionado.getIdUsuario().equals(idUsuarioLogado)){
           // exibirMensagem("Aguarde outros usuarios entrar em contato!");
            buttonIniciarConversa.setVisibility(View.GONE);
        }else{

            // adicionar evento de clique no botao de mensagens
            buttonIniciarConversa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // startActivity(new Intent(DetalhesProdutoActivity.this, ChatActivity.class));

                    //  Intent i = new Intent(DetalhesProdutoActivity.this, ChatActivity.class);
                    //  i.putExtra("anuncioSelecionado", anuncioSelecionado);
                    //  startActivity(i);


                    // enviar informacoes de uma activity para outra
                    Anuncio anuncio = new Anuncio();

                    String idUsuario = anuncioSelecionado.getIdUsuario();
                    String nomeVendedor = anuncioSelecionado.getNomeVendedor();
                   // String fotoVendedor = anuncioSelecionado.getFotoVendedor();
                    Usuario usuarioExibicao = anuncioSelecionado.getUsuarioExibicao();
                    String token = anuncioSelecionado.getUsuarioExibicao().getTokenUsuario();


                /* Recebendo os valores no objeto para enviar para
                   a próxima tela através do método putExtra() */
                    anuncio.setIdAnuncio(anuncio.getIdAnuncio());
                    anuncio.setIdUsuario(idUsuario);
                    anuncio.setNomeVendedor(nomeVendedor);
                    anuncio.setFotoVendedor(fotoPerfilVendedor);
                    anuncio.setUsuarioExibicao(usuarioExibicao);
                    anuncio.setTokenVendedor(token);


                    Intent intent = new Intent(DetalhesProdutoActivity.this, ChatActivity.class);

                    //Passando os valores
                    intent.putExtra("chat", usuarioExibicao);

                    startActivity(intent);

                }
            });

        }

    }

    public void inicializarComponentes(){
        carouselView = findViewById(R.id.carouselView);
        titulo = findViewById(R.id.textTituloDetalhe);
        descricao = findViewById(R.id.textDescricaoDetalhe);
        estado = findViewById(R.id.textEstadoDetalhe);
        preco = findViewById(R.id.textPrecoDetalhe);
        imageUsuarioDetalhesProduto = findViewById(R.id.imageUsuarioDetalhesProduto);
        nomeUsuarioDestalhesProduto = findViewById(R.id.nomeUsuarioDestalhesProduto);
        buttonIniciarConversa = findViewById(R.id.buttonAbrirChat);

    }
    // ao clicar no icone X a activiy será finalizada
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}