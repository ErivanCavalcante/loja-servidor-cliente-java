package com.indieappsbr.lojavirtual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.indieappsbr.lojavirtual.logica.BancoDadosApi;
import com.indieappsbr.lojavirtual.logica.ClienteThread;
import com.indieappsbr.lojavirtual.logica.Venda;
import com.indieappsbr.lojavirtual.logica.Usuario;

import android.app.Activity;
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
import android.view.animation.CycleInterpolator;
import android.widget.ListView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.view_listar_roupa)
public class MinhasComprasActivity extends AppCompatActivity implements BancoDadosApi
{
	@ViewById(R.id.listRoupas)
	ListView listaRoupas;
	
	ArrayList<Venda> arrayVendas = new ArrayList<>();
	
	ListaVendasAdaptador adp;
	
	Usuario usuario;
	
	ProgressDialog dialogo;

	//Pegar a application
    ClienteThread t;

	Gson gson = new Gson();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		adp = new ListaVendasAdaptador(this, R.layout.view_item_lista_vendas, arrayVendas);

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
			usuario.dinheiro = it.getFloatExtra("dinheiro", 5000);
		}
	}

	@AfterViews
	void configurar()
	{
        t = MyApplication.pegarCliente();

        if(t == null)
            Toast.makeText(this, "A thread nao foi criada", Toast.LENGTH_LONG).show();
        else
            t.registarCallback(this);

		listaRoupas.setAdapter(adp);

		carregaVendas();
	}

	@ItemClick(R.id.listRoupas)
	void clickListaRoupas(final int pos)
	{
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Confirmar Ação")
		.setMessage("Deseja relamente Devolver?")
		.setPositiveButton("Sim", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface p1, int p2)
			{
				devolverRoupa(pos);
				carregaVendas();
			}
		})
		.setNegativeButton("Não", null)
		.show();
	}
	
	private void carregaVendas() 
	{
        try {
            t.listarCompras(usuario.id);
        }
        catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

		dialogo.show();
	}

	void devolverRoupa(int idVenda)
	{
        try {
            t.devolverRoupa(idVenda);
        }
        catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_vendas, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int id = item.getItemId();
		switch( id ) 
		{
			case R.id.menu_atualizar:
				carregaVendas();
				return true;
			case R.id.menu_lista_roupas:
				vaiParaListaRoupas();
				return true;
			case R.id.menu_sair:
				desejaSair();
				return true;
		}
		
		return super.onOptionsItemSelected(item);
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
	
	void vaiParaLogin()
	{
		Intent it = new Intent(this, LoginActivity_.class);
		startActivity(it);
		finish();
	}
	
	void vaiParaListaRoupas()
	{
		Intent it = new Intent(this, PrincipalActivity_.class);
		it.putExtra("id", usuario.id);
		it.putExtra("tipo", usuario.tipo);
		it.putExtra("dinheiro", usuario.dinheiro);
		startActivity(it);
		finish();
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
        if(op == ClienteThread.DEVOLVER_ROUPA)
        {
            Toast.makeText(MinhasComprasActivity.this, "Devolucao realizada com sucesso.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            dialogo.dismiss();

            Venda lista[] = gson.fromJson(json, Venda[].class);
            Log.d("Debug", "size = " + lista.length);
            arrayVendas.clear();

            for(Venda v : lista)
                arrayVendas.add(v);

            adp.notifyDataSetChanged();

            Log.d("Debug", "Sucesso");
        }
    }

    @Override
    public void erro(int op, String erro)
	{
        dialogo.dismiss();

        Toast.makeText(MinhasComprasActivity.this, erro, Toast.LENGTH_LONG).show();

    }
}
