package robertorodrigues.curso.academicos.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.model.Usuario;

public class AdapterPesquisa extends RecyclerView.Adapter<AdapterPesquisa.MyViewHolder> {

       private List<Usuario> listaUsuarios;
       private Context context;

    public AdapterPesquisa(List<Usuario> l, Context c) {
        this.listaUsuarios = l;
        this.context = c;
    }

    @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_pesquisa_usuario, parent, false);

           return new MyViewHolder(itemLista);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Usuario usuario = listaUsuarios.get(position);

        holder.nome.setText(usuario.getNomeUsuario());

        if(usuario.getFotoUsuario() != null){
            Uri uri = Uri.parse(usuario.getFotoUsuario());
            Picasso.get()
                    .load(uri)
                    .into(holder.foto);
        }else{
            holder.foto.setImageResource(R.drawable.perfil);
        }

        }

        @Override
        public int getItemCount() {
            return listaUsuarios.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

              CircleImageView foto;
              TextView nome;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                foto = itemView.findViewById(R.id.imageFotoPesquisa);
                nome = itemView.findViewById(R.id.textNomePesquisa);
            }
        }
}
