package robertorodrigues.curso.academicos.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;

public class Comentario  implements Serializable {


    private String idComentario;
    private String idPostagem;
    private String idUsuario;
    private String nomeUsuario;
    private String fotoUsuario;
    private String comentario;

    public int qtdComentarios = 0;


    public Comentario() {
    }

    public boolean salvar(){

        DatabaseReference comentariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("comentarios")
                .child(getIdPostagem());

               //idComentario
        String chaveComentario = comentariosRef.push().getKey();
        setIdComentario(chaveComentario);
        comentariosRef.child(getIdComentario()).setValue(this);

      //  atualizarQtdComentarios(1); // incrementa comentarios

        return true;
    }



    // atualizar a quantidade de curtidas em postagem
    public  void atualizarQtdComentarios(int valor){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference postagensComentariosRef = firebaseRef
                .child("comentarios")
                .child(getIdPostagem())
                .child("qtdComentarios");

        setQtdComentarios(getQtdComentarios() + valor); // incrementa comentarios
        postagensComentariosRef.setValue(getQtdComentarios());



    }


    public String getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(String idComentario) {
        this.idComentario = idComentario;
    }

    public String getIdPostagem() {
        return idPostagem;
    }

    public void setIdPostagem(String idPostagem) {
        this.idPostagem = idPostagem;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }


    public int getQtdComentarios() {
        return qtdComentarios;
    }

    public void setQtdComentarios(int qtdComentarios) {
        this.qtdComentarios = qtdComentarios;
    }
}
