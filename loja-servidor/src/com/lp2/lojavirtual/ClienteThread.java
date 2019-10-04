package com.lp2.lojavirtual;

import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteThread extends Thread
{
    //Socket da conexao
    Socket s;

    //Entrada e saida de dados
    DataInputStream in;
    DataOutputStream out;

    //Protocolo das operacoes
    //intanciar no servidor trhead e passar o protocolo 
    //por referencia
    BancoDados bd;
    
    //Id da thread atual
    int id = 0;
    
    boolean sair = false;
    
    //Objeto para converter o json
    Gson gson = new Gson();
    
    //Hahs com os dados vindos do cliente
    String hash[];
    
    //Operacao
    int op = -1;
    
    final static int LOGAR = 0;
    final static int NOVO_USUARIO = 1;
    final static int LISTAR_ROUPAS = 2;
    final static int LISTAR_ROUPAS_MAIS_VENDIDAS = 3;
    final static int LISTAR_ROUPAS_MAIS_BARATAS = 4;
    final static int COMPRAR_ROUPA = 5;
    final static int DEVOLVER_ROUPA = 6;
    final static int LISTAR_COMPRAS = 7;
    final static int SAIR = 8;
    
    ServidorThread servidor;
    
    Escritor es;
    
    ClienteThread(Socket _s, int _id, ServidorThread st, BancoDados _bd, Escritor es) throws IOException
    {
        //Pega as variavies
        s = _s;
        id = _id;
        bd = _bd;
        servidor = st;
        this.es = es;
        
        if(s != null)
        {
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());
        }
    }

    @Override
    public void run()
    {
        String json = null;
        
        while(estaRodando())
        {
            try 
            {
                //LE o json
                json = in.readUTF();
            }
            catch (IOException ex)
            {
                //Testa s tem q sair antes d continuar
                if(!estaRodando())
                    break;
            }
            
            //Testa se a string eh valida
            if(json.isEmpty())
                continue;

            //Converte o json em um objeto
            hash = gson.fromJson(json, String[].class);

            //Testa se a conversao foi bem sucessida
            if(hash.length == 0)
                continue;

            op = Integer.parseInt(hash[0]);

            //Json de saida
            String saida;
               
            try
            {
                //Utiliza o protocolo de acordo com a operacao
                if (op == LOGAR) 
                {
                    String nome = hash[1];
                    String senha = hash[2];

                    saida = bd.pegaLogin(nome, senha);

                    out.writeInt(op);
                    out.writeUTF(saida);

                    out.flush();
                } 
                else if (op == NOVO_USUARIO) 
                {
                    String nome = hash[1];
                    String senha = hash[2];

                    saida = bd.novoLogin(nome, senha);

                    out.writeInt(op);
                    out.writeUTF(saida);

                    out.flush();
                } 
                else if (op == LISTAR_ROUPAS) 
                {
                    saida = bd.listarRoupas();

                    out.writeInt(op);
                    out.writeUTF(saida);

                    out.flush();
                } 
                else if (op == LISTAR_ROUPAS_MAIS_VENDIDAS) 
                {
                    saida = bd.listarRoupasMaisVendidas();

                    out.writeInt(op);
                    out.writeUTF(saida);

                    out.flush();
                } 
                else if (op == LISTAR_ROUPAS_MAIS_BARATAS) 
                {
                    saida = bd.listarRoupasMaisBartas();

                    out.writeInt(op);
                    out.writeUTF(saida);

                    out.flush();
                } 
                else if (op == COMPRAR_ROUPA) 
                {
                    int _id = Integer.parseInt(hash[1]);
                    int idRoupa = Integer.parseInt(hash[2]);
                    String data = hash[3];

                    bd.comprarRoupa(_id, idRoupa, data);

                    out.writeInt(op);

                    out.flush();
                } 
                else if (op == DEVOLVER_ROUPA) 
                {
                    int _id = Integer.parseInt(hash[1]);

                    bd.devolverRoupa(_id);

                    out.writeInt(op);

                    out.flush();
                } 
                else if (op == LISTAR_COMPRAS) 
                {
                    int _id = Integer.parseInt(hash[1]);

                    saida = bd.listarCompras(_id);

                    out.writeInt(op);
                    out.writeUTF(saida);

                    out.flush();
                } 
                else if (op == SAIR)
                {
                    fecharCliente();

                    break;
                }
            }
            catch (IOException ex)
            {
                //Testa s tem q sair antes d continuar
                if(!estaRodando())
                    break;
            }
        }
        
        //Variavel compatilhada, necessario sincronizar
        if(op == SAIR)
            servidor.removerCliente(id);

        es.escrever("Cliente id = " + id + " saiu...");
    }
    
    boolean estaRodando()
    {
        synchronized (this) {
            return !sair;
        }
    }
    
    public void fecharCliente()
    {
        synchronized (this) {
            sair = true;
        }
 
        try 
        {
            //Fecha td
            in.close();
            out.close();
            s.close();
        } 
        catch(IOException ex) 
        {
            Logger.getLogger(ClienteThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        es.escrever("Ordem para fechar o cliente.....");
    }
}
