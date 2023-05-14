package robertorodrigues.curso.academicos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.fragment.FeedFragment;
import robertorodrigues.curso.academicos.fragment.LojasFragment;
import robertorodrigues.curso.academicos.fragment.PerfilFragment;
import robertorodrigues.curso.academicos.fragment.PesquisaFragment;
import robertorodrigues.curso.academicos.fragment.PostagemFragment;
import robertorodrigues.curso.academicos.helper.ConfiguracaoFirebase;
import robertorodrigues.curso.academicos.helper.UsuarioFirebase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;// email e senha
    private GoogleSignInClient mGoogleSignInClient; // Cliente de login do Google



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Lero Interaction ");
        setSupportActionBar(toolbar);

       // inicializar componentes
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        // abrir por padrao primeiro o feed de noticias
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();

        // configurar botao de navegacao
         configuraBotaoNavegacao();
         

    }

    // metodo responsavel por criar a BottonNavigation
    private void configuraBotaoNavegacao(){
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.botaoNavegacao);

          // configuracoes iniciais
         // nao esta funcionando, dando erro de de objeto null
       // bottomNavigationViewEx.enableAnimation(true);  // com animacao no botao
       // bottomNavigationViewEx.enableAnimation(false); // sem animacao no botao
        //bottomNavigationViewEx.enableItemShiftingMode(false);
      //  bottomNavigationViewEx.enableShiftingMode(false);
       // bottomNavigationViewEx.setTextVisibility(false); // esconde texto do botao

        //habilitar navegacao
         habilitarNavegacao(bottomNavigationViewEx);

        // configurar item selecionado inicialmente
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0); // seleciona o primeiro menu(Inicio)
        menuItem.setChecked(true);


    }

    // tratar evento de clique no bottom navigation view
    private void habilitarNavegacao(BottomNavigationViewEx viewEx){

        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


                switch (item.getItemId()){
                    case R.id.ic_inicio:
                        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();
                        return true;
                    case R.id.ic_pesquisa:
                        fragmentTransaction.replace(R.id.viewPager, new PesquisaFragment()).commit();
                        return true;
                    case R.id.ic_postagem:
                        fragmentTransaction.replace(R.id.viewPager, new PostagemFragment()).commit();
                        return true;
                    case R.id.ic_lojas:
                        fragmentTransaction.replace(R.id.viewPager, new LojasFragment()).commit();
                        return true;
                    case R.id.ic_perfil:
                        fragmentTransaction.replace(R.id.viewPager, new PerfilFragment()).commit();


                        return true;
                }

                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.menu_conversas:
                abrirConversas();
                break;

            case R.id.menu_meus_anuncios:
                startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
                break;

            case R.id.menu_sobre:
                Toast.makeText(this, "Sobre o App", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuSair:

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Sair");
                builder.setMessage("Tem certeza que deseja sair?");

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {

                            //recuperar dados do usuario google
                            FirebaseUser usuarioAtual = UsuarioFirebase.getUsuarioAtual();

                            if(usuarioAtual != null){
                                deslogarContaGoogle();
                            }else{

                                autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                                autenticacao.signOut();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));


                            }
                            //autenticacao.signOut();
                            //finish();

                        }catch (Exception  e){
                            e.printStackTrace();
                        }

                        invalidateOptionsMenu(); // invalidar os menus de 3 pontinhos ao deslogar usuario
                        finish();
                        abrirLogin();
                    }
                });
                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();


                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void deslogarContaGoogle() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id2))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                        autenticacao.signOut();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    }
                });
    }

   /* private void deslogarUsuario(){
        try {
            autenticacao.signOut();
            finish();

        }catch (Exception  e){
            e.printStackTrace();
        }
    } */

    @Override
    public void finish() {
        finishAffinity();
        super.finish();
    }

    private void abrirConversas(){
        startActivity(new Intent(MainActivity.this, ConversasActivity.class));
    }

    private void abrirLogin(){
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}