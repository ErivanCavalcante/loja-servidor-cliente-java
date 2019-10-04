package com.indieappsbr.lojavirtual;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.indieappsbr.lojavirtual.logica.BancoDadosApi;
import com.indieappsbr.lojavirtual.logica.ClienteThread;
import com.indieappsbr.lojavirtual.logica.Roupa;
import com.indieappsbr.lojavirtual.logica.Usuario;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.view_listar_roupa)
public class PrincipalActivity extends AppCompatActivity implements BancoDadosApi
{
	@ViewById(R.id.listRoupas)
	ListView listaRoupas;
	
	ArrayList<Roupa> arrayRoupas = new ArrayList<Roupa>();
	
	ListaRoupasAdaptador adp;
	
	Usuario usuario;
	
	ProgressDialog dialogo;

	//Pegar a application
	ClienteThread t;

	Gson gson = new Gson();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		adp = new ListaRoupasAdaptador(this, R.layout.view_item_lista_roupa, arrayRoupas);
		
		dialogo = new ProgressDialog(this);
		dialogo.setCancelable(false);
		dialogo.setMessage("Aguarde...");
		dialogo.setIndeterminate(true);
		
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
		t = MyApplication.pegarCliente();

		if(t == null)
			Toast.makeText(this, "A thread não foi criada", Toast.LENGTH_LONG).show();
		else
			t.registarCallback(this);

		listaRoupas.setAdapter(adp);

		carregaRoupas();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.principal, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int id = item.getItemId();
		switch( id ) 
		{
			case R.id.menu_atualizar:
				carregaRoupas();
				return true;
			case R.id.menu_perfil:
				carregarPerfil();
				return true;
			case R.id.menu_minhasCompras:
				vaiParaMinhasCompras();
				return true;
			case R.id.menu_maisVendidos:
				carregaRoupasMaisVendidas();
				return true;
			case R.id.menu_maisBaratos:
				carregaRoupasMaisBaratas();
				return true;
			case R.id.menu_sair:
				desejaSair();
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	private void carregarPerfil()
	{
        PerfilActivity_.intent(this).start();
	}

	@Override
    public void onBackPressed()
    {
        try {
            t.fechar();
            t.join();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
			e.printStackTrace();
		}

		finish();
    }
	
	@ItemClick(R.id.listRoupas)
	void clickListaRoupas(final int pos)
	{
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Confirmar Ação")
		.setMessage("Deseja relamente Comprar?")
		.setPositiveButton("Sim", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface p1, int p2)
			{
				compraRoupa(arrayRoupas.get(pos).id);
			}
		})
		.setNegativeButton("Não", null)
		.show();
	}
	
	void vaiParaLogin()
	{
		LoginActivity_.intent(this).start();
		finish();
	}
	
	void vaiParaMinhasCompras()
	{
		Intent it = new Intent(this, MinhasComprasActivity_.class);
		it.putExtra("id", usuario.id);
		it.putExtra("tipo", usuario.tipo);
		it.putExtra("dinheiro", usuario.dinheiro);
		startActivity(it);
		finish();
	}

	void carregaRoupas()
	{
        try {
            t.listarRoupas();
        }
        catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        dialogo.show();
	}
	
	void carregaRoupasMaisVendidas()
	{
        try {
            t.listarRoupasMaisVendidas();
        }
        catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        dialogo.show();
	}
	
	void carregaRoupasMaisBaratas()
	{
        try {
            t.listarRoupasMaisBartas();
        }
        catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

		dialogo.show();
	}

	void compraRoupa(int idRoupa)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String dataFormatada = df.format(new Date(System.currentTimeMillis()));

        try {
            t.comprarRoupa(usuario.id, idRoupa, dataFormatada);
        }
        catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
	}
	
	void zerarLogin()
	{
		SharedPreferences p = getSharedPreferences("login", 0);
		SharedPreferences.Editor edit = p.edit();
		
		edit.putString("email", "");
		edit.putString("senha", "");
		
		//Grava os dados
		edit.commit();
	}
	
	void desejaSair()
	{
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Confirmar Ação")
		.setMessage("Deseja Relamente Sair?")
		.setPositiveButton("Sim", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface p1, int p2)
			{
				zerarLogin();
				vaiParaLogin();
			}
		})
		.setNegativeButton("Não", null)
		.show();
	}

    @Override
    public void resposta(int op, String json)
    {
	    if(op == ClienteThread.COMPRAR_ROUPA)
        {
            Toast.makeText(PrincipalActivity.this, "Compra realizada com sucesso.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            dialogo.dismiss();

            Roupa lista[] = gson.fromJson(json, Roupa[].class);

            Log.d("Debug", "size = " + lista.length);
            Log.d("Debug", "json = " + json);
            arrayRoupas.clear();

            for(Roupa r : lista)
                arrayRoupas.add(r);

            adp.notifyDataSetChanged();

            Log.d("Debug", "Sucesso");
        }
    }

    @Override
    public void erro(int op, String erro)
    {
        dialogo.dismiss();

        Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
    }
}
