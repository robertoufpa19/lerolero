package robertorodrigues.curso.academicos.adapter;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;
import robertorodrigues.curso.academicos.model.Mensagem;

public class MensagensAdapter extends RecyclerView.Adapter<MensagensAdapter.MyViewHolder> {
    private List<Mensagem> mensagens;
    private Context context;

    private static final int TIPO_REMETENTE = 0;
    private static final int TIPO_DESTINATARIO = 1;

    private ImageView imagePerfilUsuario;

    public MensagensAdapter(List<Mensagem> lista, Context c ) {
        this.mensagens = lista;
        this.context = c;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = null;
        // condição para verificar qual item utilizar no envio da mensagem ou do recebimento dela
        if(viewType == TIPO_REMETENTE){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_remetente, parent,false);

        }else if(viewType == TIPO_DESTINATARIO){
            item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mensagem_destinatario, parent,false);
        }
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Mensagem mensagem = mensagens.get(position);



        String msg = mensagem.getMensagem();
        String imagem = mensagem.getImagem();
        String arquivo = mensagem.getArquivo();
        // se o usuario tiver uma imagem vamos exibir a imagem.Senão vamos exibir o texto

        if(imagem != null){


            //Carregar imagem
            String urlImagem = mensagem.getImagem();
            Picasso.get().load( urlImagem ).into( holder.imagem);

            String nome = mensagem.getNome();
            if(!nome.isEmpty()){
                holder.nome.setText(nome);
            }else {
                holder.nome.setVisibility(View.GONE);
            }

            // esconder texto
            holder.mensagem.setVisibility(View.GONE);
            // esconder arquivo
            holder.layoutMensagemArquivo.setVisibility(View.GONE);
            holder.imagem.setVisibility(View.VISIBLE);



        }else if(msg != null){

            holder.mensagem.setText(msg); // texto recuperado

            String nome = mensagem.getNome();
            if(!nome.isEmpty()){
                holder.nome.setText(nome);
            }else {
                holder.nome.setVisibility(View.GONE);
            }
            // esconder imagem
            holder.imagem.setVisibility(View.GONE);
            holder.layoutMensagemArquivo.setVisibility(View.GONE);

            // esconder imagem
            holder.imagem.setVisibility(View.GONE);
            // esconder arquivo
            holder.layoutMensagemArquivo.setVisibility(View.GONE);
            holder.mensagem.setVisibility(View.VISIBLE);

        }else if (arquivo != null){

            // esconder imagem
            holder.imagem.setVisibility(View.GONE);
            // esconder texto
            holder.mensagem.setVisibility(View.GONE);

            holder.layoutMensagemArquivo.setVisibility(View.VISIBLE);
            holder.nomeArquivo.setText(arquivo);

            String nome = mensagem.getNome();
            if(!nome.isEmpty()){
                holder.nome.setText(nome);
            }else {
                holder.nome.setVisibility(View.GONE);
            }

            holder.mensagem.setVisibility(View.GONE);
            holder.imagem.setVisibility(View.GONE);


            holder.layoutMensagemArquivo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    StorageReference storageReference = ConfiguracaoFirebase.getFirebaseStorage();
                    StorageReference documentoRef = storageReference.child("arquivos")
                            .child("pdf")
                            .child(mensagem.getIdUsuario())
                            .child(mensagem.getArquivo());


                    documentoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();

                            //falta baixar arquivo
                            baixarArquivo(context, mensagem.getArquivo(), ".pdf", DIRECTORY_DOWNLOADS, url );
                            Toast.makeText(context, "baixando...", Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, "sucesso ao fazer Download!", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(context, "falha ao baixar arquivo", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }


    }


    public long baixarArquivo(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {

        DownloadManager downloadmanager = (DownloadManager) context.
                getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        return downloadmanager.enqueue(request);
    }

    @Override
    public int getItemCount() {
        return mensagens.size();
    }

    @Override
    public int getItemViewType(int position) {

        Mensagem mensagem = mensagens.get(position); // recupera a mensagem para o priemiro item que sera exibido na lista
        String idUsuario = UsuarioFirebase.getIdUsuario(); // recupera o id do usuario

        if(idUsuario.equals(mensagem.getIdUsuario())){
            return TIPO_REMETENTE;
        }
        return  TIPO_DESTINATARIO;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView mensagem, nomeArquivo;
        TextView nome;
        ImageView imagem;
        LinearLayout layoutMensagemArquivo;

        public MyViewHolder(View itemView){
            super(itemView);
            mensagem = itemView.findViewById(R.id.textMensagemTexto);
            imagem = itemView.findViewById(R.id.imageMensagemFoto);
            nome = itemView.findViewById(R.id.textNomeExibicao);
            nomeArquivo = itemView.findViewById(R.id.textMensagemNomeArquivo);
            layoutMensagemArquivo = itemView.findViewById(R.id.layoutMensagemArquivo);

        }
    }
}
