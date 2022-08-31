package robertorodrigues.curso.academicos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText campoLoginEmail, campoLoginSenha;
    private Button buttonEntrar;
    private ProgressBar progressBarLogin;
    private Usuario usuario;
    private FirebaseAuth autenticacao;


    /// autenticacao do dispositivo com a conta do google
    private LinearLayout buttonAcessoGoogle;
    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient; // Cliente de login do Google
    private static final String TAG = "GoogleActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        verificarUsuarioLogado();
        inicializarComponentes();


        // login usuario
        progressBarLogin.setVisibility(View.GONE);
        buttonEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String campoEmail = campoLoginEmail.getText().toString();
                String campoSenha = campoLoginSenha.getText().toString();

                if(!campoEmail.isEmpty()){
                    if(!campoSenha.isEmpty()){

                        usuario = new Usuario();
                        usuario.setEmailUsuario(campoEmail);
                        usuario.setSenhaUsuario(campoSenha);
                        validarLogin(usuario);


                    }else{
                        exibirMensagem("Preencha o campo Senha");
                    }
                }else{
                    exibirMensagem("Preencha o campo Email");
                }

            }
        });

        buttonAcessoGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarLogin.setVisibility(View.VISIBLE);
                dadosUsuario();
            }
        });

    }

    // abrir tela principal se o usuario estiver logado
    public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        if(autenticacao.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class)); // abrir a tela principal
            finish();
        }
    }

    public void validarLogin(Usuario usuario){
        progressBarLogin.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        autenticacao.signInWithEmailAndPassword(
                usuario.getEmailUsuario(),
                usuario.getSenhaUsuario()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                 if(task.isSuccessful()){
                     progressBarLogin.setVisibility(View.GONE);
                     startActivity(new Intent(getApplicationContext(), MainActivity.class));
                     finish();

                 }else{

                     exibirMensagem("Erro ao fazer Login!");
                     progressBarLogin.setVisibility(View.GONE);
                 }
            }
        });

    }


    public void abrirCadastro(View view){
        Intent i = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(i);
    }


     /// metodos de login com a conta do google inicio

    public void dadosUsuario(){

        // Configurar o Login do Google para autenticar dispositivo

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id2))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        entrar();  // abrir layouts de contas disponiveis no dispositivo


    }

    private void entrar() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // verifica se o usuário fez login (não nulo) e atualiza a interface de acordo.
        FirebaseUser currentUser = autenticacao.getCurrentUser();
        updateUI(currentUser);

        if(currentUser != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

    }

    private void updateUI(FirebaseUser user) {

    }


    // [ inicio no resultado da atividade]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado retornado ao iniciar o Intent de GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // O login do Google foi bem-sucedido, autentique-se com o Firebase
                GoogleSignInAccount contaLoginGoogle = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + contaLoginGoogle.getId());
                firebaseAuthWithGoogle(contaLoginGoogle.getIdToken());



            } catch (ApiException e) {

                // Falha no login do Google, atualize a interface do usuário adequadamente
                Log.w(TAG, "Falha no login do Google", e);

                exibirMensagem("Erro ao fazer Login");
            }
        }


    }
    // [fim no resultado da atividade]

    // autenticação do firebase com o Google
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        autenticacao.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login bem-sucedido, atualiza a interface do usuário com as informações do usuário conectado
                            Log.d(TAG, "entrar com credencial: sucesso");
                            FirebaseUser user = autenticacao.getCurrentUser();
                            updateUI(user);
                            progressBarLogin.setVisibility(View.GONE);

                            // dados do usuario
                             String idUsuario = task.getResult().getUser().getUid();
                            String emailUsuario = user.getEmail();
                            String nomeUsuario = user.getDisplayName();
                            String fotoUsuario = String.valueOf(user.getPhotoUrl());

                            usuario = new Usuario();
                            usuario.setIdUsuario(idUsuario);
                            usuario.setEmailUsuario(emailUsuario);
                            usuario.setNomeUsuario(nomeUsuario);
                            usuario.setFotoUsuario(fotoUsuario);


                            // inicio cadastro do token usuario
                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            if (!task.isSuccessful()) {
                                                Log.w("Cadastro token", "Fetching FCM registration token failed", task.getException());
                                                return;
                                            }

                                            // Get new FCM registration token
                                            String token = task.getResult();
                                            usuario.setTokenUsuario(token);
                                            usuario.salvar();

                                            if(usuario != null){

                                                exibirMensagem("Sucesso ao fazer Login");
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                // startActivity(new Intent(this, HomeActivity.class));
                                                finish();
                                            }


                                        }
                                    });    // fim cadastro do token


                        } else {
                            // Se o login falhar, exibe uma mensagem para o usuário.
                            Log.w(TAG, "entrar com credencial: falha", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    // [TERMINAR autenticação com o google]


    /// metodos de login com a conta do google fim




    public void inicializarComponentes(){
        campoLoginEmail  = findViewById(R.id.editLoginEmail);
        campoLoginSenha  = findViewById(R.id.editLoginSenha);
        buttonEntrar     = findViewById(R.id.buttonEntrar);
        progressBarLogin = findViewById(R.id.progressLogin);
        buttonAcessoGoogle = findViewById(R.id.buttonAcessoGoogle);

        campoLoginEmail.requestFocus(); // foco no campo email
    }
    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }


}