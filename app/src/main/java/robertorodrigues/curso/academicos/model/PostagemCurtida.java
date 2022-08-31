package robertorodrigues.curso.academicos.model;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;

public class PostagemCurtida {

    public int qtdCurtidas = 0;
    public Feed feed;
    public Usuario usuario;

    public PostagemCurtida() {
    }


    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        // objeto usuario
        HashMap<String, Object> dadosUsuario = new HashMap<>();
        dadosUsuario.put("nomeUsuario", usuario.getNomeUsuario());
        dadosUsuario.put("fotoUsuario", usuario.getFotoUsuario());
        dadosUsuario.put("idUsuario", usuario.getIdUsuario());


        DatabaseReference postagensCurtidasRef = firebaseRef
                .child("postagens_curtidas")
                .child(feed.getIdPostagem())
                .child(usuario.getIdUsuario());
        postagensCurtidasRef.setValue(dadosUsuario);

        // atualizar quantidade de curtidas com incremento de +1
        atualizarQtdCurtidas(1);

    }

    // atualizar a quantidade de curtidas em postagem
    public  void atualizarQtdCurtidas(int valor){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference postagensCurtidasRef = firebaseRef
                .child("postagens_curtidas")
                .child(feed.getIdPostagem())
                .child("qtdCurtidas");
        setQtdCurtidas(getQtdCurtidas() + valor);  // incrementa curtidas
        postagensCurtidasRef.setValue(getQtdCurtidas());


    }


    public void removerCurtida(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference postagensCurtidasRef = firebaseRef
                .child("postagens_curtidas")
                .child(feed.getIdPostagem())
                .child(usuario.getIdUsuario());
        postagensCurtidasRef.removeValue();

        // atualizar quantidade de curtidas com decremento de -1
        atualizarQtdCurtidas(-1);
    }


    public int getQtdCurtidas() {
        return qtdCurtidas;
    }

    public void setQtdCurtidas(int qtdCurtidas) {
        this.qtdCurtidas = qtdCurtidas;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
