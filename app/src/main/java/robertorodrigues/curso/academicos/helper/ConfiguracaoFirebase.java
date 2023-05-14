package robertorodrigues.curso.academicos.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/*
 * Created by Roberto de Oliveira Rodrigues
 */

public class ConfiguracaoFirebase {


    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth referenciaAutenticacao;
    private static StorageReference referenciaStorage;


    // retorna a instancia do  FirebaseDatabase

    public static String getIdUsuario(){

        FirebaseAuth autenticacao = getFirebaseAutenticacao();
        return autenticacao.getCurrentUser().getUid();
    }

    public  static DatabaseReference getFirebaseDatabase(){
        if(referenciaFirebase == null){
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }
        return  referenciaFirebase;
    }
    // retorna a instancia do  FirebaseAuth

    public  static FirebaseAuth getFirebaseAutenticacao(){
        if(referenciaAutenticacao == null){
            referenciaAutenticacao = FirebaseAuth.getInstance();
        }
        return  referenciaAutenticacao;
    }
    // retorna a instancia do  FirebaseStorage
    public  static StorageReference getFirebaseStorage(){

        if(referenciaStorage == null){
            referenciaStorage = FirebaseStorage.getInstance().getReference();
        }

        return  referenciaStorage;


    }

}
