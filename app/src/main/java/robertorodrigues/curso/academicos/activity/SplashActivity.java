package robertorodrigues.curso.academicos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.fragment.ConversasFragment;

public class SplashActivity extends AppCompatActivity {

    private ConstraintLayout constraintLeroLero;
    private TextView textLeroLero;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // getSupportActionBar().hide(); // esconder toolbar
        constraintLeroLero = findViewById(R.id.constraintLeroLero);
        textLeroLero = findViewById(R.id.textLeroLero);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //abrirAutenticacao();
                verificarCompartilhamentoArquivo();
            }
        }, 4000); // 3 segundos


    }

    private void abrirAutenticacao(){
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    public void verificarCompartilhamentoArquivo(){

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Lidar com o texto que está sendo enviado
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Lidar com uma única imagem sendo enviada
            }else if (type.startsWith("application/pdf")) {
                handleSendPdf(intent); // Lidar com um único pdf sendo enviada
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Manipula várias imagens sendo enviadas
            }
        } else {
            // Lida com outras intenções, como iniciar na tela inicial
            abrirAutenticacao();
        }


    }

   public void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Atualizar a interface do usuário para refletir o texto que está sendo compartilhado
            Toast.makeText(this, "texto recuperado", Toast.LENGTH_SHORT).show();
            intent.putExtra("compartilharTexto", sharedText);
            //falta enviar para o fragmento de conversas
            Intent intentConversas = new Intent(SplashActivity.this, ConversasActivity.class);
            startActivity(intentConversas);
        }
    }

    public void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            textLeroLero.setVisibility(View.GONE);
            // abrir fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putParcelable("compartilharImagem", imageUri);
            ConversasFragment conversasFragment = new ConversasFragment();
            conversasFragment.setArguments(bundle);
            transaction.replace(R.id.constraintLeroLero, conversasFragment).commit();



        }
    }

    public void handleSendPdf(Intent intent) {
        Uri pdfUri  = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (pdfUri  != null) {
            textLeroLero.setVisibility(View.GONE);
            // abrir fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putParcelable("compartilharPdf", pdfUri);
            ConversasFragment conversasFragment = new ConversasFragment();
            conversasFragment.setArguments(bundle);
            transaction.replace(R.id.constraintLeroLero, conversasFragment).commit();


        }
    }

    public void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            // Atualize a interface do usuário para refletir várias imagens sendo compartilhadas
            //Toast.makeText(this, "multiplas imagens", Toast.LENGTH_SHORT).show();
            intent.putExtra("compartilharImagens", imageUris);
            //falta enviar para o fragmento de conversas
            Intent intentConversas = new Intent(SplashActivity.this, ConversasActivity.class);
            startActivity(intentConversas);
        }
    }

}