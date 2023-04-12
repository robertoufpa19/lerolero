package robertorodrigues.curso.academicos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.ArrayList;

import robertorodrigues.curso.academicos.R;
import robertorodrigues.curso.academicos.adapter.ViewPagerAdapter;
import robertorodrigues.curso.academicos.fragment.ConversasFragment;

public class ConversasActivity extends AppCompatActivity {


    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversas);
        inicializarComponentes();
        configTabsLayout();
        configCliques();


    }

    private void configTabsLayout(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ConversasFragment(), "Conversas");
       // viewPagerAdapter.addFragment(new ConversaFinalizadaFragment(), "Finalizadas");

        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setSaveEnabled(false);
        tabLayout.setElevation(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void inicializarComponentes(){
        TextView text_Toobar = findViewById(R.id.txt_Toobar);
        text_Toobar.setText("Chat");

        viewPager =  findViewById(R.id.view_Pager);
        tabLayout =  findViewById(R.id.tab_Layout);

    }

    // recuperar arquivo compartilhado
    private void configCliques(){
        // recuperar arquivo compartilhado - inicio
        Bundle bundleArquivo = getIntent().getExtras();
        if(bundleArquivo != null) {
            if (bundleArquivo.containsKey("compartilharImagem")) {
                findViewById(R.id.ib_Voltar).setOnClickListener(view1 -> finishAffinity());

            } else if (bundleArquivo.containsKey("compartilharPDF")) {
                findViewById(R.id.ib_Voltar).setOnClickListener(view1 -> finishAffinity());
            }

        }else{
            findViewById(R.id.ib_Voltar).setOnClickListener(view1 -> onBackPressed());

            Log.d("null ", "bundle nullo");
        }
    }

    public void finish() {
        // recuperar arquivo compartilhado - inicio
        Bundle bundleArquivo = getIntent().getExtras();
        if(bundleArquivo != null) {
            if (bundleArquivo.containsKey("compartilharImagem")) {
                finishAffinity();

            } else if (bundleArquivo.containsKey("compartilharPDF")) {
                finishAffinity();
            }

        }else{
            findViewById(R.id.ib_Voltar).setOnClickListener(view1 -> onBackPressed());

            Log.d("null ", "bundle nullo");
        }
        super.finish();
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