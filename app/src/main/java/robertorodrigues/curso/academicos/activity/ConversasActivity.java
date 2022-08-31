package robertorodrigues.curso.academicos.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

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