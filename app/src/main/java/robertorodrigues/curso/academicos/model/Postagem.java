package robertorodrigues.curso.academicos.model;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;

/*
 * Created by Roberto de Oliveira Rodrigues
 */

public class Postagem implements Serializable {

    private String idPostagem;
    private String idUsuario;
    private String fotoUsuario;
    private String nomeUsuario;
    private String tokenUsuario;
    private String descricao;
    private String fotoPostagem;



    public Postagem() {
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference postagemRef = firebaseRef.child("postagens");
        String idPostagemRef = postagemRef.push().getKey();
        setIdPostagem(idPostagemRef);
    }


    /// metodo de espalhamento
    // cria dois NO de forma instântanea

    public boolean salvarPostagem(DataSnapshot seguidoresSnapshot){

        Map objeto = new HashMap();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        //referencia para o NO postagens
        String combinacaoId = "/"+ getIdUsuario() + "/"+ getIdPostagem();
        objeto.put("/postagens" + combinacaoId, this);
        /*
           postagens-No
              + idUsuario
                 + idPostagem
         */


        // referencia para o NO postagens no Feed
        for(DataSnapshot seguidores: seguidoresSnapshot.getChildren()){
                /*
           feed-No
              + idSeguidor
                 + idPostagem
                     dados de quem postou
         */
            String idSeguidor = seguidores.getKey();

            // monta o objeto para salvar
            HashMap<String, Object> dadosSeguidor = new HashMap<>();
            dadosSeguidor.put("fotoPostagem", getFotoPostagem());
            dadosSeguidor.put("descricao", getDescricao());
            dadosSeguidor.put("idPostagem", getIdPostagem());

            dadosSeguidor.put("idUsuario", usuarioLogado.getIdUsuario());
            dadosSeguidor.put("nomeUsuario", usuarioLogado.getNomeUsuario());
            dadosSeguidor.put("fotoUsuario", usuarioLogado.getFotoUsuario());
            dadosSeguidor.put("tokenUsuario", usuarioLogado.getTokenUsuario()); // nao recuperou o token

             // nao cria o NO feed se seguidores for null
            String idsAtualizado = "/"+ idSeguidor + "/"+ getIdPostagem();
            objeto.put("/feed" + idsAtualizado, dadosSeguidor);

        }

          // postagem publicas
         objeto.put("/postagem_publica" + "/"+ getIdPostagem(), this);
        //objeto.put("/destaque" + combinacaoId, this);

        firebaseRef.updateChildren(objeto);

        return true;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFotoPostagem() {
        return fotoPostagem;
    }

    public void setFotoPostagem(String fotoPostagem) {
        this.fotoPostagem = fotoPostagem;
    }


    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getTokenUsuario() {
        return tokenUsuario;
    }

    public void setTokenUsuario(String tokenUsuario) {
        this.tokenUsuario = tokenUsuario;
    }
}
