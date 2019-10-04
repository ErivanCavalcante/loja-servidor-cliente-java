/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lp2.lojavirtual;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Erivan
 */
class ServidorThread extends Thread 
{
    //Pool de thread
    //Criar uma lista para armazenar todos os threads para acesso depois.
    Executor ex;
    
    //Socket do servidor
    ServerSocket server;
    //Lista de tds as threads ativas
    //<Id, Thread>
    public Map<Integer, ClienteThread> clientes;
    //Proximo id cadastrado
    int idCount = 0;
    
    boolean running = true;
    
    BancoDados bd;
    
    Escritor es;
    
    final Object lock = new Object();

    public ServidorThread(int porta, Escritor es) throws IOException, SQLException
    {
        this.es = es;
        
        //Cria os objetos
        ex = Executors.newCachedThreadPool();
        server = new ServerSocket(porta);
        clientes = new HashMap<>();
        bd = new BancoDados();
    }

    @Override
    public void run() 
    {
        //Roda ate ser interrmpido
        while (estaRodando()) 
        {
            //Precia sincronizar os buffers d entrada e saida de texto
            es.escrever("Servidor ouvindo...");
            
            Socket s = null;
                
            try 
            {
                //Espera novos clientes
                s = server.accept();
            }
            catch (IOException e)
            {
                //Quando for chamada a funcao sair nao adiciona novos clientes
                if(!estaRodando())
                    break;
            }
            
            //Cria a nova thread
            ClienteThread t = null;
            try 
            {
                t = new ClienteThread(s, ++idCount, this, bd, es);
            } 
            catch (IOException e) 
            {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, e);
                continue;
            }

            //Adiciona na lista de threads
            //Necessario sincronizar aqui........
            adicionarCliente(idCount, t);

            //Adiciona o novo cliente no poll de threads
            ex.execute(t);

            es.escrever("Servidor aceitou um novo cliente id = " + idCount);
            
        }
        
        //Fecha todas os clientes
        es.escrever("Numero de clientes = " + clientes.size());
        for(ClienteThread c : clientes.values())
        {
            try 
            {
                es.escrever("Inicio remover cliente");
                c.fecharCliente();
                c.join();
                es.escrever("Fim remover cliente");
            } 
            catch(InterruptedException e) 
            {
                Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        clientes.clear();
          
        es.escrever("Servidor fechado...");
    }    
    
    boolean estaRodando()
    {
        synchronized (this) {
            return running;
        }
    }
    
    public void fecharServidor()
    {
        synchronized (this) {
            running = false;    
        }
    
        try 
        {
            server.close();
        } 
        catch (IOException e) 
        {
            Logger.getLogger(ServidorThread.class.getName()).log(Level.SEVERE, null, e);
        }
 
        es.escrever("Servidor comando para fechar");
    }
    
    void adicionarCliente(int id, ClienteThread c)
    {
        synchronized (lock) {
            clientes.put(idCount, c);
        }
    }
    
    public synchronized void removerCliente(int id)
    {
        synchronized (lock) {
            clientes.remove(id);
        }
    }
}
