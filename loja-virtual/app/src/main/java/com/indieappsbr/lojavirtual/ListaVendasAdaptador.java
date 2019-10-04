package com.indieappsbr.lojavirtual;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import com.indieappsbr.lojavirtual.logica.Venda;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

//Representa um item da lista
public class ListaVendasAdaptador extends ListViewAdapter<Venda>
{

	public ListaVendasAdaptador(Context ct, int idResource, ArrayList<Venda> objLista) 
	{
		super(ct, idResource, objLista);
	}

	@Override
	public void onLayoutInflate(View container, int pos) 
	{
		Venda r = lista.get(pos);
		
		//TextView txtMarca = (TextView)container.findViewById(R.id.txtMarca);
		TextView txtData = (TextView)container.findViewById(R.id.txtData);
		
		//txtMarca.setText(r.marca);
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String dataFormatada = df.format(r.data);
		
		txtData.setText(dataFormatada);
	}

}
