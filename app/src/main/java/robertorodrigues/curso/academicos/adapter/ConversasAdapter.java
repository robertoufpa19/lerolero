package robertorodrigues.curso.academicos.adapter;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.model.Conversa;
import robertorodrigues.curso.academicos.model.Usuario;

public class ConversasAdapter extends RecyclerView.Adapter<ConversasAdapter.MyViewHolder> {

    private List<Conversa> conversas;
    private Context context;

    public ConversasAdapter(List<Conversa> lista, Context c) {
        this.conversas = lista;
        this.context = c;
    }

    public List<Conversa> getConversas(){
        return this.conversas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_contatos, parent, false );
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Conversa conversa = conversas.get( position );

        String usuarioNome = conversa.getUsuarioExibicao().getNomeUsuario();
        String usuarioFoto = conversa.getUsuarioExibicao().getFotoUsuario();
        String ultimaMsg = conversa.getUltimaMensagem();


        if(usuarioNome != null){
            holder.nome.setText( usuarioNome);

            if(ultimaMsg != null){
                holder.ultimaMensagem.setText( conversa.getUltimaMensagem() );
            }else{
                holder.ultimaMensagem.setText("documento.pdf");
            }

            //configura foto de usuarios na conversa
            if ( usuarioFoto != null ){
                Picasso.get().load( Uri.parse(usuarioFoto) ).into( holder.foto);
            }else{
                holder.foto.setImageResource(R.drawable.perfil);
            }



            //  condição para verificar novas mensagens
            if(conversa.getNovaMensagem().equals("true")){
                holder.novaMensagem.setVisibility( View.VISIBLE);

            }else{
                holder.novaMensagem.setVisibility( View.GONE);

            }

        }




    }

    @Override
    public int getItemCount() {
        return conversas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView foto;
        TextView nome, ultimaMensagem, novaMensagem;

        public MyViewHolder(View itemView) {
            super(itemView);

            foto = itemView.findViewById(R.id.imageViewFotoContatos);
            ultimaMensagem = itemView.findViewById(R.id.textEmailContato);
            nome = itemView.findViewById(R.id.textNomeContato);
            novaMensagem = itemView.findViewById(R.id.textQtdNovasMensagens);

        }
    }


}
