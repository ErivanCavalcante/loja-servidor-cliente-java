package com.indieappsbr.lojavirtual;

import android.app.Application;

import com.indieappsbr.lojavirtual.logica.ClienteThread;

import java.io.IOException;

public class MyApplication extends Application
{
    //Cliente thread
    static ClienteThread cliente;

    @Override
    public void onCreate()
    {
        super.onCreate();

        MyApplication.cliente = new ClienteThread();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();

        try {
            MyApplication.cliente.fechar();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    //Retorna a thread cliente
    public static ClienteThread pegarCliente()
    {
        return MyApplication.cliente;
    }
}
