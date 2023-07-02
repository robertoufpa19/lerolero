package robertorodrigues.curso.academicos.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.activity.ComentariosActivity;
import robertorodrigues.curso.academicos.activity.PerfilAmigoActivity;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;
import robertorodrigues.curso.academicos.model.Comentario;
import robertorodrigues.curso.academicos.model.Feed;
import robertorodrigues.curso.academicos.model.PostagemCurtida;
import robertorodrigues.curso.academicos.model.Usuario;

public class AdapterFeed extends RecyclerView.Adapter<AdapterFeed.MyViewHolder> {


    private List<Feed> listaFeed;
    private Context context;

    public AdapterFeed(List<Feed> listaFeed, Context context) {
        this.listaFeed = listaFeed;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_feed, parent, false);

        return new AdapterFeed.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Feed feed = listaFeed.get(position);
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        // carregar dados do feed

        //Uri uriFotoUsuario = Uri.parse(feed.getFotoUsuario());


        holder.nome.setText(feed.getNomeUsuario());

        Uri uriFotoPostagem = Uri.parse(feed.getFotoPostagem());

        if(feed.getDescricao().equals("") || feed.getDescricao().equals(null)){
            holder.descricao.setVisibility(View.GONE);
        }else{
            holder.descricao.setText(feed.getDescricao());
        }


         // foto perfil usuario
        if(feed.getFotoUsuario() != null){
            Picasso.get()
                    .load(Uri.parse(feed.getFotoUsuario()))
                    .into(holder.fotoPerfil);
        }else{
            holder.fotoPerfil.setImageResource(R.drawable.perfil);
        }
        // foto postada usuario
        Picasso.get()
                .load(uriFotoPostagem)
                .into(holder.fotoPostagem);


        // adicionar evento de clique no botao comentarios do feed
        holder.botaoComentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, ComentariosActivity.class);
                i.putExtra("idPostagem", feed.getIdPostagem() );
                context.startActivity(i);
            }
        });



        // adicionar evento de clique na imagemPerfil ou nome para abrir perfil do amigo
        holder.perfilAmigoDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // enviar informacoes de uma activity para outra
                Usuario usuario = new Usuario();

                String idUsuario = feed.getIdUsuario();
                String nomeUsuario = feed.getNomeUsuario();
                String fotoUsuario = feed.getFotoUsuario();

                usuario.setIdUsuario(idUsuario);
                usuario.setNomeUsuario(nomeUsuario);
                usuario.setFotoUsuario(fotoUsuario);
                // usuario.setTokenUsuario(token);

                // recuperar token
                DatabaseReference usuarioRef =  ConfiguracaoFirebase.getFirebaseDatabase()
                        .child("usuarios")
                        .child(idUsuario)
                        .child("tokenUsuario");
                usuarioRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String tokenUsuario =  snapshot.getValue().toString();
                        usuario.setTokenUsuario(tokenUsuario);
                        Intent i = new Intent(context, PerfilAmigoActivity.class);
                        i.putExtra("usuarioSelecionadoAmigo", usuario );
                        context.startActivity(i);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });


        // recuperar dados da postagem curtida
        DatabaseReference curtidasRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("postagens_curtidas")
                .child(feed.getIdPostagem());
        curtidasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int qtdCurtidas = 0;
                if(snapshot.hasChild("qtdCurtidas")){ // verifica se existe essa variavel no firebase
                    PostagemCurtida postagemCurtida = snapshot.getValue(PostagemCurtida.class);
                    qtdCurtidas = postagemCurtida.getQtdCurtidas();

                }

                // verificar se o botão curtida já foi clicado
                if(snapshot.hasChild(usuarioLogado.getIdUsuario())){
                    holder.botaoCurtida.setLiked(true);
                }else{
                    holder.botaoCurtida.setLiked(false);
                }


                // monta objeto postagem curtida
                PostagemCurtida curtida = new PostagemCurtida();
                   curtida.setFeed(feed);
                   curtida.setUsuario(usuarioLogado);
                   curtida.setQtdCurtidas(qtdCurtidas);


                // adicionar evento para curtir uma postagem
                holder.botaoCurtida.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                       // salva dados do usuario que curtiu uma postagem no firebase
                        curtida.salvar();
                        holder.qtdCurtida.setText(curtida.getQtdCurtidas() + ""); //"curtidas"

                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        curtida.removerCurtida();
                        holder.qtdCurtida.setText(curtida.getQtdCurtidas() + ""); //"curtidas"
                    }
                });
                // recupera quantidade de curtidas
                holder.qtdCurtida.setText(curtida.getQtdCurtidas() + "");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    @Override
    public int getItemCount() {
        return listaFeed.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{


        TextView nome, descricao, qtdCurtida;
        CircleImageView fotoPerfil;
        ImageView fotoPostagem, imageComentarioFeed;
        LikeButton botaoCurtida;
        ImageView botaoComentarios;
        LinearLayout perfilAmigoDados;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

         fotoPerfil = itemView.findViewById(R.id.imagePerfilFeed);
         nome = itemView.findViewById(R.id.textNomePerfilFeed);
         perfilAmigoDados = itemView.findViewById(R.id.perfilAmigoDados);

         descricao = itemView.findViewById(R.id.textDescricaoPostagemFeed);

         qtdCurtida = itemView.findViewById(R.id.textQtdCurtidaFeed);


         fotoPostagem = itemView.findViewById(R.id.imagePostadaFeed);

         imageComentarioFeed = itemView.findViewById(R.id.imageComentarioFeed);
         botaoCurtida = itemView.findViewById(R.id.botaoCurtidaFeed);
         botaoComentarios = itemView.findViewById(R.id.imageComentarioFeed);



        }
    }



}
