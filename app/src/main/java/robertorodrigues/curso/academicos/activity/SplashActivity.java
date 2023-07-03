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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // getSupportActionBar().hide(); // esconder toolbar
        constraintLeroLero = findViewById(R.id.constraintLeroLero);

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



}