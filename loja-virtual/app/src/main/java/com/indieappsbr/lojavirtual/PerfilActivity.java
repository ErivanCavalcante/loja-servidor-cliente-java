package com.indieappsbr.lojavirtual;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.indieappsbr.lojavirtual.logica.Usuario;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_perfil)
public class PerfilActivity extends AppCompatActivity {

    @ViewById(R.id.txtDinheiro)
    TextView txtDinheiro;

    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent it = getIntent();
        if(it != null)
        {
            usuario = new Usuario();

            usuario.id = it.getIntExtra("id", 1);
            usuario.tipo = it.getIntExtra("tipo", 1);
            usuario.dinheiro = it.getIntExtra("dinheiro", 5000);
        }
    }

    @AfterViews
    void configurar()
    {
        txtDinheiro.setText("Dinehiro: " + usuario.dinheiro);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
