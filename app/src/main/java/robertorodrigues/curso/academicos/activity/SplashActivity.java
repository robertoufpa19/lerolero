package robertorodrigues.curso.academicos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import java.util.ArrayList;

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

       /* Intent intent = getIntent();
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
        } */



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
            // Atualizar a interface do usuário para refletir a imagem que está sendo compartilhada
           // Toast.makeText(this, "imagem recuperada", Toast.LENGTH_SHORT).show();
            intent.putExtra("compartilharImagem", imageUri);
            //falta enviar para o fragmento de conversas
            Intent intentConversas = new Intent(SplashActivity.this, ConversasActivity.class);
            startActivity(intentConversas);
        }
    }

    public void handleSendPdf(Intent intent) {
        Uri pdfURI  = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        if (pdfURI  != null) {
            // Atualizar a interface do usuário para refletir a imagem que está sendo compartilhada
           // Toast.makeText(this, "pdf recuperada", Toast.LENGTH_SHORT).show();
            intent.putExtra("compartilharPdf", pdfURI);
            //falta enviar para o fragmento de conversas
            Intent intentConversas = new Intent(SplashActivity.this, ConversasActivity.class);
            startActivity(intentConversas);
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