package robertorodrigues.curso.academicos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;

import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.fragment.ConversasFragment;

public class ConversasActivity extends AppCompatActivity {

    private ImageView imageVoltar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversas);

        imageVoltar = findViewById(R.id.imageVoltarConversas);

        imageVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // configurar toolbar
        //  Toolbar toolbar = findViewById(R.id.toolbar);
        //   toolbar.setTitle("Conversas");
        //  setSupportActionBar(toolbar);


        //Configurar abas
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(this)
                        .add("Mensagens", ConversasFragment.class)
                        .create()
        );

        final ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);



        // recuperar arquivo compartilhado - inicio
        Bundle bundleArquivo = getIntent().getExtras();

        if(bundleArquivo != null) {

            if (bundleArquivo.containsKey("compartilharImagem")) {
               // Uri imagemSelecionada = (Uri) getIntent().getParcelableExtra("compartilharImagem");
                Uri imagemSelecionada = (Uri) bundleArquivo.getParcelable("compartilharImagem");

                Toast.makeText(getApplicationContext(), "Recuperou imagem", Toast.LENGTH_SHORT).show();

            } else if (bundleArquivo.containsKey("compartilharPdf")) {
                //Uri pdfSelecionado = (Uri) getIntent().getParcelableExtra("compartilharPDF");
                Uri pdfSelecionado = (Uri) bundleArquivo.getParcelable("compartilharPDF");
                Toast.makeText(getApplicationContext(), "Recuperou PDF", Toast.LENGTH_SHORT).show();

            }else if (bundleArquivo.containsKey("compartilharImagens")) {
                ArrayList<Uri> imageUris = (ArrayList<Uri>) bundleArquivo.getParcelable("compartilharImagens");

                Toast.makeText(getApplicationContext(), "lista de imagens", Toast.LENGTH_SHORT).show();
            }

        }else{

            Toast.makeText(getApplicationContext(), "bundle nullo", Toast.LENGTH_SHORT).show();
        }
        // recuperar arquivo compartilhado - fim

    }

    // @Roberto
    @Override
    protected void onStart() {

        super.onStart();
    }

    // @Roberto
    @Override
    protected void onStop() {

        super.onStop();
    }
}