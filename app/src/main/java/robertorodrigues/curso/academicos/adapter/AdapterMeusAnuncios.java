package robertorodrigues.curso.academicos.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.model.Anuncio;


public class AdapterMeusAnuncios extends RecyclerView.Adapter<AdapterMeusAnuncios.MyViewHolder>{

    private List<Anuncio> anuncios;
    private Context context;

    public AdapterMeusAnuncios(List<Anuncio> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_meus_anuncios, parent, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Anuncio anuncio = anuncios.get(position);
        holder.titulo.setText(anuncio.getTitulo());
        holder.valor.setText(anuncio.getValor());
        holder.descricao.setText(anuncio.getDescricao());
        holder.nomeVendedor.setText(anuncio.getNomeVendedor());

        //verificar se existe avaliações
      /*  if(anuncio.getAvaliacao() != null){
            holder.avaliacao.setText(anuncio.getAvaliacao());
        }else{
            holder.avaliacao.setText("0");
        } */

        // pegar a primeira imagem da lista
        List<String> urlFotos = anuncio.getFotos();
        String urlCapa = urlFotos.get(0);// pegar a primeira imagem da lista
        Picasso.get().load(urlCapa).into(holder.foto);

        //configurar foto do vendedor
        // recuperar foto de  perfil amigo
        DatabaseReference usuarioRef =  ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child(anuncio.getIdUsuario())
                .child("fotoUsuario");
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String fotoPerfilAmigo =  snapshot.getValue().toString();
                if(snapshot.exists()){
                    if(fotoPerfilAmigo != null){
                        Picasso.get()
                                .load(Uri.parse(fotoPerfilAmigo))
                                .into(holder.fotoVendedor);
                    }else{
                        holder.fotoVendedor.setImageResource(R.drawable.perfil);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
      //  String  urlFotoUsuario = anuncio.getFotoVendedor();
       // Picasso.get().load(urlFotoUsuario).into(holder.fotoVendedor);

        // botao excluir
        holder.botaoExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Exluir");
                builder.setMessage("Tem certeza que deseja excluir?");

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        anuncio.removerAnuncio();

                        Toast.makeText(context.getApplicationContext(),
                                "Anúncio Excluido Com sucesso!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });


    }



    @Override
    public int getItemCount() {
        return anuncios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titulo;
        TextView valor;
        ImageView foto;
        TextView descricao;
      //  TextView avaliacao;
        TextView nomeVendedor;
        CircleImageView fotoVendedor;
        Button botaoExcluir;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textTituloMeuAnuncio);
            valor  = itemView.findViewById(R.id.textPrecoMeuAnuncio);
            foto = itemView.findViewById(R.id.imageMeuAnuncio);
            descricao = itemView.findViewById(R.id.textDescricaoMeuAnuncio);
          //  avaliacao = itemView.findViewById(R.id.textAvaliacoesMeuAnuncio);
            nomeVendedor = itemView.findViewById(R.id.textNomeUsuarioMeusAnuncio);
            fotoVendedor = itemView.findViewById(R.id.imagePerfilUsuarioMeusAnuncio);
            botaoExcluir = itemView.findViewById(R.id.buttonExcluir);


        }
    }



}
