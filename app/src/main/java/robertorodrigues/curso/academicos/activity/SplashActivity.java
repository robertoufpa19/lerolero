package robertorodrigues.curso.academicos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
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
               receberArquivoCompartilhado();
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
             if (type.startsWith("image/*")) {
                handleSendImage(intent); // Lidar com uma única imagem sendo enviada
            }else if (type.startsWith("application/pdf")) {
                handleSendPdf(intent); // Lidar com um único pdf sendo enviada
            }
        } else {
            // Lida com outras intenções, como iniciar na tela inicial
            abrirAutenticacao();
        }


    }


    @SuppressLint("MissingInflatedId")
    public void receberArquivoCompartilhado() {
        Intent intent = getIntent();
        String action = intent.getAction();// abrir app normalmente ou a partir de compartilhamento de arquivos
        String type = intent.getType(); // buscar o tipo de arquivo (imagem, pdf, etc...)

        if (action.equals(Intent.ACTION_SEND) && type != null) {
            if (type.startsWith("image/")) {
                Uri imagemURI = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imagemURI != null) {
                    Intent intentConversas = new Intent(SplashActivity.this, ConversasActivity.class);
                    intentConversas.putExtra("compartilharImagem", imagemURI);
                    startActivity(intentConversas);
                    Log.d("imagem ", imagemURI.toString());
                } else {
                    Log.d("null ", "imagem nullo");
                }

            } else if (type.startsWith("application/pdf")) {
                Uri pdfURI = intent.getParcelableExtra(Intent.EXTRA_STREAM);

                if (pdfURI != null) {
                    Intent intentConversas = new Intent(SplashActivity.this, ConversasActivity.class);
                    intentConversas.putExtra("compartilharPDF", pdfURI);
                    startActivity(intentConversas);
                    Log.d("pdf ", pdfURI.toString());
                } else {
                    Log.d("null ", "pdf nullo");
                }

                // formatos de arquivo bin
            }else if(type.startsWith("*/*")){

                Uri imagemURI = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imagemURI != null) {
                    Intent intentConversas = new Intent(SplashActivity.this, ConversasActivity.class);
                    intentConversas.putExtra("compartilharImagem", imagemURI);
                    startActivity(intentConversas);
                    Log.d("imagem ", imagemURI.toString());
                } else {
                    Log.d("null ", "imagem nullo");
                }
            }
        } else {
            abrirAutenticacao();
        }
    }


    public void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            textLeroLero.setVisibility(View.GONE);
            // abrir fragment
           /* FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putParcelable("compartilharImagem", imageUri);
            ConversasFragment conversasFragment = new ConversasFragment();
            conversasFragment.setArguments(bundle);
            transaction.replace(R.id.constraintLeroLero, conversasFragment).commit(); */

            // abri activity conversas teste
            Intent intentConversas = new Intent(this, ConversasActivity.class);
            intentConversas.putExtra("compartilharImagem", imageUri);
            startActivity(intentConversas);
            Toast.makeText(this, "imagem", Toast.LENGTH_SHORT).show();


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

        }
    }

    public void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Atualizar a interface do usuário para refletir o texto que está sendo compartilhado
            // Toast.makeText(this, "texto recuperado", Toast.LENGTH_SHORT).show();

        }
    }

}