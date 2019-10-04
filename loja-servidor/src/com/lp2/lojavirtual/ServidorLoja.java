package com.lp2.lojavirtual;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorLoja
{
    public static void main(String []args)
    {
        boolean bSair = false;
        Escritor es = new Escritor();
   
        try 
        {
            //Sobe o sevidor
            ServidorThread servidor = new ServidorThread(4444, es);
            
            //Inicia o servidor
            servidor.start();
           
            //Scanner para pegar entradas do teclado
            Scanner s = new Scanner(System.in);
            
            //Roda ate mandar parar
            //Retirar exeção criando uma funão booleana para sair.
            while (bSair != true) 
            {
                //aqui ele captura a entrada do teclado, 
                //não achei a implementação no codigo então eu fiz
                String sMensage = s.nextLine();
                //Se for digitado sair finaliza o servidor
                if(sMensage.equalsIgnoreCase("sair"))
                    bSair = true;    
            }

            //Fecha todas as threads
            servidor.fecharServidor();//aqui que gera a exceção 
            servidor.join();
            
            es.escrever("Fim de main");
        }
        catch (IOException | InterruptedException | SQLException ex)
        {
            Logger.getLogger(ServidorLoja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}