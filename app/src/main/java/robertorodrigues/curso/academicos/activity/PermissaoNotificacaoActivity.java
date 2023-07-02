package robertorodrigues.curso.academicos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;

import robertorodrigues.curso.academicos.R;

public class PermissaoNotificacaoActivity extends AppCompatActivity {

    private Button buttonAbrirConfiguracoes;
    private ImageView imageFechar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissao_notificacao);

        inicializarComponentes();

        imageFechar.setOnClickListener(v -> finishAffinity());

        buttonAbrirConfiguracoes.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            finish();
        });
    }

    private void inicializarComponentes() {
        buttonAbrirConfiguracoes = findViewById(R.id.buttonAbrirConfiguracoes);
        imageFechar = findViewById(R.id.imageFecharTela);
    }
}