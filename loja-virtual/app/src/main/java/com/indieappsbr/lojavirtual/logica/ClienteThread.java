package com.indieappsbr.lojavirtual.logica;

import android.os.Looper;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import android.os.Handler;
import android.util.Log;

public class ClienteThread extends Thread
{
    public final static int LOGAR = 0;
    public final static int NOVO_USUARIO = 1;
    public final static int LISTAR_ROUPAS = 2;
    public final static int LISTAR_ROUPAS_MAIS_VENDIDAS = 3;
    public final static int LISTAR_ROUPAS_MAIS_BARATAS = 4;
    public final static int COMPRAR_ROUPA = 5;
    public final static int DEVOLVER_ROUPA = 6;
    public final static int LISTAR_COMPRAS = 7;
    public final static int SAIR = 8;

    //Cliente socket
    Socket sq;

    //Entrada e saida de dados
    DataInputStream in;
    DataOutputStream out;

    //Resposta para o cliente
    BancoDadosApi res;

    //Controla a saida do loop
    boolean running = true;

    //Lista de argumentos enviados ao servidor
    ArrayList<String> hash = new ArrayList<>();

    Gson gson = new Gson();

    int op = -1;
    String json;

    String ip;

    public void setarIp(String ip)
    {
        this.ip = ip;
    }

    public void registarCallback(BancoDadosApi res)
    {
        synchronized (this)
        {
            this.res = res;
        }
    }

    @Override
    public void run()
    {
        if(!inicarConexao())
            return;

        while(estaRodando())
        {
            try
            {
                //retorna um codigo depois um texto com o json
                op = in.readInt();

                //Nessas opçoes nao eh preciso fazer mais nada
                if(op == COMPRAR_ROUPA || op == DEVOLVER_ROUPA)
                        continue;

                json = in.readUTF();

                try
                {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (this) {
                                res.resposta(op, json);
                            }
                        }
                    });
                }
                catch (NullPointerException ex)
                {

                }
            }
            catch (final IOException ex)
            {
                Log.d("Trhead Client", "Fim da thread");
                //Testa se deve sair do loop
                if (!estaRodando())
                    break;
                else {
                    try {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                synchronized (this) {
                                    res.erro(op, ex.getMessage());
                                }
                            }
                        });
                    }
                    catch (NullPointerException e)
                    {

                    }
                }
            }

            Log.d("Trhead Client", "Fim da thread");
        }
    }

    boolean inicarConexao()
    {
        //Cria o socket client
        try
        {
            sq = new Socket(ip, 4444);

            //sq.setSoTimeout(100000);

            //Cria as estruturas de entrada e saida
            if (sq != null)
            {
                in = new DataInputStream(sq.getInputStream());
                out = new DataOutputStream(sq.getOutputStream());
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    public void pegaLogin(String email, String senha) throws IOException
    {
        //Limpa a lista para adicionar os dados
        hash.clear();

        //Manda o tipo de operação
        hash.add(String.valueOf(LOGAR));
        //Manda as infos
        hash.add(email);
        hash.add(senha);

        //Cria o json com o valores passados
        String json = gson.toJson(hash);

        //Envia pro servidor
        out.writeUTF(json);
    }

    public void novoLogin(String email, String senha) throws IOException
    {
        //Limpa a lista para adicionar os dados
        hash.clear();

        //Manda o tipo de operação
        hash.add(String.valueOf(NOVO_USUARIO));

        //Manda as infos
        hash.add(email);
        hash.add(senha);

        //Cria o json com o valores passados
        String json = gson.toJson(hash);

        //Envia pro servidor
        out.writeUTF(json);
    }

    public void listarRoupas() throws IOException
    {
        //Limpa a lista para adicionar os dados
        hash.clear();

        //Manda o tipo de operação
        hash.add(String.valueOf(LISTAR_ROUPAS));

        //Cria o json com o valores passados
        String json = gson.toJson(hash);

        //Envia pro servidor
        out.writeUTF(json);
    }

    public void listarRoupasMaisVendidas() throws IOException
    {
        //Limpa a lista para adicionar os dados
        hash.clear();

        //Manda o tipo de operação
        hash.add(String.valueOf(LISTAR_ROUPAS_MAIS_VENDIDAS));

        //Cria o json com o valores passados
        String json = gson.toJson(hash);

        //Envia pro servidor
        out.writeUTF(json);
    }

    public void listarRoupasMaisBartas() throws IOException
    {
        //Limpa a lista para adicionar os dados
        hash.clear();

        //Manda o tipo de operação
        hash.add(String.valueOf(LISTAR_ROUPAS_MAIS_BARATAS));

        //Cria o json com o valores passados
        String json = gson.toJson(hash);

        //Envia pro servidor
        out.writeUTF(json);
    }

    public void comprarRoupa(Integer id, Integer idRoupa, String data) throws IOException
    {
        //Limpa a lista para adicionar os dados
        hash.clear();

        //Manda o tipo de operação
        hash.add(String.valueOf(COMPRAR_ROUPA));

        //Manda as infos
        hash.add(String.valueOf(id));
        hash.add(String.valueOf(idRoupa));
        hash.add(data);

        //Cria o json com o valores passados
        String json = gson.toJson(hash);

        //Envia pro servidor
        out.writeUTF(json);
    }

    public void devolverRoupa(Integer id) throws IOException
    {
        //Limpa a lista para adicionar os dados
        hash.clear();

        //Manda o tipo de operação
        hash.add(String.valueOf(DEVOLVER_ROUPA));

        hash.add(String.valueOf(id));

        //Cria o json com o valores passados
        String json = gson.toJson(hash);

        //Envia pro servidor
        out.writeUTF(json);
    }

    public void listarCompras(Integer id) throws IOException
    {
        //Limpa a lista para adicionar os dados
        hash.clear();

        //Manda o tipo de operação
        hash.add(String.valueOf(LISTAR_COMPRAS));

        hash.add(String.valueOf(id));

        //Cria o json com o valores passados
        String json = gson.toJson(hash);

        //Envia pro servidor
        out.writeUTF(json);
    }

    public void fechar()
    {
        //Limpa a lista para adicionar os dados
        hash.clear();

        //Manda o tipo de operação
        hash.add(String.valueOf(SAIR));

        //Cria o json com o valores passados
        String json = gson.toJson(hash);

        //Envia pro servidor
        try {
            out.writeUTF(json);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        /************NEcessario sincronizar aqui*************/

        synchronized (this) {
            //Fechar o thread
            running = false;
        }

        //Fecha td aqui

        //Fecha as conecoes
        try
        {
            in.close();
            out.close();
            sq.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Log.d("Trhead Client", "Sinal para a thread parar");
    }

    boolean estaRodando()
    {
        synchronized (this) {
            return running;
        }
    }
}
