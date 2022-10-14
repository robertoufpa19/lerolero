package robertorodrigues.curso.academicos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import robertorodrigues.curso.academicos.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // getSupportActionBar().hide(); // esconder toolbar

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

        String buscarAcao = intent.getAction();
        String buscarModelo = intent.getType();

        if(buscarAcao.equals(Intent.ACTION_SEND) && buscarModelo != null){
            if(buscarModelo.startsWith("image/*")){

                Uri imagemURI = intent.getParcelableExtra(Intent.EXTRA_STREAM);

                if(imagemURI != null){

                    intent.putExtra("compartilharImagem", imagemURI);
                    //falta enviar para o fragmento de conversas
                    Intent intentConversas = new Intent(SplashActivity.this, ConversasActivity.class);
                    startActivity(intentConversas);

                }else{
                    Toast.makeText(this, "nullo", Toast.LENGTH_SHORT).show();
                }

            }else if(buscarModelo.startsWith("application/pdf")){
                Uri pdfURI = intent.getParcelableExtra(Intent.EXTRA_STREAM);

                if(pdfURI != null){
                    intent.putExtra("compartilharPDF", pdfURI);
                    //falta enviar para o fragmento de conversas
                    Intent intentConversas = new Intent(SplashActivity.this, ConversasActivity.class);
                    startActivity(intentConversas);
                }else{
                    Toast.makeText(this, "nullo", Toast.LENGTH_SHORT).show();
                }

            }
        }else{
            abrirAutenticacao();
        }



    }

}