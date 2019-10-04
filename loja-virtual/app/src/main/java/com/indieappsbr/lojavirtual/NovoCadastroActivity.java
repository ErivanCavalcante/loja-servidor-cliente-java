package com.indieappsbr.lojavirtual;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.indieappsbr.lojavirtual.logica.BancoDadosApi;
import com.indieappsbr.lojavirtual.logica.ClienteThread;
import com.indieappsbr.lojavirtual.logica.Usuario;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.view_cadastrar)
public class NovoCadastroActivity extends AppCompatActivity implements BancoDadosApi
{
	@ViewById(R.id.edtEmail)
	EditText edtEmail;
	
	@ViewById(R.id.edtSenha)
	EditText edtSenha;
	
	Usuario usuario;
	
	ProgressDialog dialogo;

	//Pegar a application
	ClienteThread t;

	Gson gson = new Gson();
	
	@AfterViews
	protected void configuracaoInicial()
	{
        t = MyApplication.pegarCliente();

        if(t == null)
            Toast.makeText(this, "A thread nao foi criada", Toast.LENGTH_LONG).show();
        else
            t.registarCallback(this);

		dialogo = new ProgressDialog(this);
		dialogo.setCancelable(false);
		dialogo.setMessage("Aguarde...");
		dialogo.setIndeterminate(true);
		
		//Se tiver algum login entra direto
		carregaLoginPrefe();
	}
	
	@Override
	public void onBackPressed() 
	{
		LoginActivity_.intent(this).start();
		finish();
	}
	
	public void respostaBtnOk( View v )
	{
		if(!validarCamposTexto())
			return;
		
		adicionarBancoDados();
	}
	
	boolean validarCamposTexto()
	{
		if(edtEmail.getText().length() == 0 || edtSenha.getText().length() == 0)
		{
			Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	void vaiParaListaPrincipal()
	{
		Intent it = new Intent( this, PrincipalActivity_.class );
		it.putExtra("id", usuario.id);
		it.putExtra("tipo", usuario.tipo);
		it.putExtra("dinheiro", usuario.dinheiro);
		startActivity(it);
		finish();
	}
	
	void adicionarBancoDados()
	{
		try
		{
			t.novoLogin(edtEmail.getText().toString(), edtSenha.getText().toString());
		}
		catch (IOException | NullPointerException e)
		{
			e.printStackTrace();
		}

		dialogo.show();
	}

	void salvaLogin(String email, String senha)
	{
		SharedPreferences p = getSharedPreferences("login", 0);
		SharedPreferences.Editor edit = p.edit();
		
		edit.putString("email", email);
		edit.putString("senha", senha);
		
		//Grava os dados
		edit.commit();
	}
	
	void carregaLoginPrefe()
	{
		SharedPreferences p = getSharedPreferences("login", 0);
		
		edtEmail.setText(p.getString("email", ""));
		edtSenha.setText(p.getString("senha", ""));
		
		respostaBtnOk(null);
	}

	@Override
	public void resposta(int op, String json)
    {
		dialogo.dismiss();

		//Descerializa para uma lista de usuarios encontrados
		Usuario u[] = gson.fromJson(json, Usuario[].class);

		//Testa se achou algum login
		if( u.length != 0 )
		{
			usuario = u[0];
			Log.d("Retrofit", "id = " + usuario.id);
			vaiParaListaPrincipal();
		}
		//Nenhum login encontrado
		else
		{
			Toast.makeText(this, "Nenhum login encontrado.", Toast.LENGTH_LONG).show();
			return;
		}

		Log.d("Retrofit", "Sucessooooo...");
	}

	@Override
	public void erro(int op, String erro)
    {
		dialogo.dismiss();

		Toast.makeText(this, erro, Toast.LENGTH_LONG).show();
	}
}
