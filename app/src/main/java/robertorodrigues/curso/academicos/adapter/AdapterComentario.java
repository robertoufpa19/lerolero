package robertorodrigues.curso.academicos.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import robertorodrigues.curso.academicos.model.Comentario;

public class AdapterComentario extends RecyclerView.Adapter<AdapterComentario.MyViewHolder> {


    private List<Comentario> comentarios;  //listaComentarios
    private Context context;

    public AdapterComentario(List<Comentario> comentarios, Context context) {
        this.comentarios = comentarios;
        this.context = context;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_comentarios, parent, false );
        return new AdapterComentario.MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

       // Conversa conversa = conversas.get( position );
        Comentario comentario = comentarios.get(position);

        String usuarioNome = comentario.getNomeUsuario();
        String usuarioFoto = comentario.getFotoUsuario();


        if(usuarioNome != null){
            holder.nome.setText( usuarioNome);
            holder.comentario.setText( comentario.getComentario());

            //configura foto de usuarios no comentario

            // recuperar foto de  perfil amigo
            DatabaseReference usuarioRef =  ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("usuarios")
                    .child(comentario.getIdUsuario())
                    .child("fotoUsuario");
            usuarioRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String fotoPerfilAmigo =  snapshot.getValue().toString();
                    if(snapshot.exists()){
                        if(fotoPerfilAmigo != null){
                            Picasso.get()
                                    .load(Uri.parse(fotoPerfilAmigo))
                                    .into(holder.foto);
                        }else{
                            holder.foto.setImageResource(R.drawable.perfil);
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

           /* if ( usuarioFoto != null ){
                Uri uri = Uri.parse( usuarioFoto );
                Picasso.get().load( uri ).into( holder.foto);
            }else {
                holder.foto.setImageResource(R.drawable.perfil);
            }*/

        }

    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView foto;
        TextView nome, comentario;

        public MyViewHolder(View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imagePerfilUsuarioComentario);
            comentario = itemView.findViewById(R.id.textComentarioUsuario);
            nome = itemView.findViewById(R.id.textNomePerfilUsuarioComentario);


        }
    }

}
