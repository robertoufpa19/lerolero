package robertorodrigues.curso.academicos.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;


   /*
   * Created by Roberto de Oliveira Rodrigues
    */

public class Usuario implements Serializable {

    private String idUsuario;
    private String nomeUsuario;
    private String emailUsuario;
    private String senhaUsuario;
    private String fotoUsuario;
    private String tokenUsuario;

    private int seguidores = 0;
    private int seguindo   = 0;
    private int postagens  = 0;

    public Usuario() {
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(getIdUsuario());
        usuarioRef.setValue(this);
    }

    public void atualizarQtdPostagem(){

        String identificadorUsuario = UsuarioFirebase.getIdUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuariosRef = database.child("usuarios")
                .child(identificadorUsuario);

        HashMap<String, Object> dados = new HashMap<>();
        dados.put("postagens"    , getPostagens());

        usuariosRef.updateChildren(dados);
    }


    public void atualizar(){

        String identificadorUsuario = UsuarioFirebase.getIdUsuario();
        DatabaseReference database = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuariosRef = database.child("usuarios")
                .child(identificadorUsuario);
        Map<String, Object> valoresUsuario =  converterParaMap();
        usuariosRef.updateChildren(valoresUsuario);
    }

    @Exclude
    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();

        // atualizar somente nome e foto
      //  usuarioMap.put("idUsuario"   , getIdUsuario());
        usuarioMap.put("nomeUsuario" , getNomeUsuario());
        usuarioMap.put("fotoUsuario" , getFotoUsuario());
      //  usuarioMap.put("emailUsuario", getEmailUsuario());
      //  usuarioMap.put("tokenUsuario", getTokenUsuario());
      //  usuarioMap.put("seguidores"  , getSeguidores());
       // usuarioMap.put("seguindo"    , getSeguindo());
      //  usuarioMap.put("postagens"    , getPostagens());


        return  usuarioMap;

    }

    public int getSeguidores() {
        return seguidores;
    }

    public void setSeguidores(int seguidores) {
        this.seguidores = seguidores;
    }

    public int getSeguindo() {
        return seguindo;
    }

    public void setSeguindo(int seguindo) {
        this.seguindo = seguindo;
    }

    public int getPostagens() {
        return postagens;
    }

    public void setPostagens(int postagens) {
        this.postagens = postagens;
    }

    public String getTokenUsuario() {
        return tokenUsuario;
    }

    public void setTokenUsuario(String tokenUsuario) {
        this.tokenUsuario = tokenUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario.toLowerCase(); // converte o nome para letra minuscula;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }
    // esconde a senha do banco de dados
     @Exclude
    public String getSenhaUsuario() {
        return senhaUsuario;
    }

    public void setSenhaUsuario(String senhaUsuario) {
        this.senhaUsuario = senhaUsuario;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }


}
