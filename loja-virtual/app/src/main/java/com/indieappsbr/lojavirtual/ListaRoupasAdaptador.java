package com.indieappsbr.lojavirtual;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.indieappsbr.lojavirtual.logica.Roupa;

//Representa um item da lista
public class ListaRoupasAdaptador extends ListViewAdapter<Roupa>
{
	public ListaRoupasAdaptador(Context ct, int idResource, ArrayList<Roupa> objLista) 
	{
		super(ct, idResource, objLista);
	}

	@Override
	public void onLayoutInflate(View container, int pos) 
	{
		Roupa r = lista.get(pos);
		
		TextView txtMarca = (TextView)container.findViewById(R.id.txtMarca);
		TextView txtTipo = (TextView)container.findViewById(R.id.txtTipo);
		TextView txtCor = (TextView)container.findViewById(R.id.txtCor);
		TextView txtPreco = (TextView)container.findViewById(R.id.txtPreco);
		
		txtMarca.setText(r.marca);
		txtTipo.setText(r.categoria);
		txtCor.setText(r.cor);
		txtPreco.setText("" + r.preco);
	}
	
}
