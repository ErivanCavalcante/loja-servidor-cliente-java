package com.indieappsbr.lojavirtual;

//import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ListViewAdapter<T> extends BaseAdapter
{
	//WeakReference<Context> context;
	LayoutInflater inf;
	int idRes;
	protected ArrayList<T> lista;
	
	public ListViewAdapter(Context ct, int idResource, ArrayList<T> objLista) 
	{
		inf = LayoutInflater.from(ct);
		idRes = idResource;
		lista = objLista;
	}
	
	@Override
	final public int getCount() 
	{
		return lista.size();
	}

	@Override
	final public Object getItem(int pos) 
	{
		return lista.get(pos);
	}

	@Override
	final public long getItemId(int pos) 
	{
		return pos;
	}

	@Override
	final public View getView(int pos, View view, ViewGroup arg2) 
	{
		View lay;
		
		if(view == null)
		{
			lay = inf.inflate(idRes, null);
		}
		else
		{
			lay = view;
		}
		
		onLayoutInflate(lay, pos);
		
		return lay;
	}
	
	abstract public void onLayoutInflate( View container, int pos );
}
