package com.lp2.lojavirtual;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BancoDados
{
    //Banco de dados
    //usar caminho relativo database.db
    static String DATABASE_URL = "jdbc:sqlite:database.db";
    
    ConnectionSource con = null;
    
    Dao<Usuario, Integer> daoUsuario;
    Dao<Roupa, Integer> daoRoupa;
    Dao<Venda, Integer> daoVenda;
    
    Gson gson = new Gson();
    
    int nLeitores, nQuerLer, nQuerEscrever = 0;
    
    public BancoDados() throws SQLException 
    {
        //Cria a conecao com o baynchronizednco de dados
        con = new JdbcConnectionSource(DATABASE_URL);
        
        if(con != null)
        {
            daoUsuario = DaoManager.createDao(con, Usuario.class);
            daoRoupa = DaoManager.createDao(con, Roupa.class);
            daoVenda = DaoManager.createDao(con, Venda.class);
        }
    }
    
    //Operacoes do servidor
    public String pegaLogin(String email, String senha)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("senha", senha);
        
        try 
        {
            synchronized (this)
            {
                //Incrementa o numero de threads q querem ler
                nQuerLer++;
                
                //Espera ate q nao tenha ninguem pra escrever
                while(nQuerEscrever != 0) wait();
                
                nQuerLer--;
                
                //Incrementa o numero de leitores
                nLeitores++;
                
                //Se tiver alguem na lista libera
                if(nQuerLer != 0) notify();
            }
            
            List<Usuario> u = daoUsuario.queryForFieldValues(map);
            
            synchronized (this)
            {
                nLeitores--;
                
                if(nLeitores == 0) notify();
            }
            
            //Retorna o json para o usuario
            // mudar o tipo para string por causa do json.
            return gson.toJson(u);   
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Erro ou esta vazio
        return new String();
    }

    public String novoLogin(String email, String senha)
    {
        Usuario u = new Usuario();
        
        u.email = email;
        u.senha = senha;
        
        try 
        {
            synchronized (this)
            {
                //Aumenta o numero das threads q querem escrever
                nQuerEscrever++;
                
                //Espera ate nao ter nenhuma thread lendo
                while(nLeitores != 0) wait();
                
                //Decrementa o numero das threads q querem escrever
                nQuerEscrever--;
                
                //Escreve a partir daqui
                daoUsuario.create(u);
                
                //Libera uma thread
                notify();
            }
            
            return gson.toJson(u);  
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (InterruptedException ex) 
        {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Erro ou esta vazio
        return new String();
    }

    public String listarRoupas()
    {
        try 
        {
            synchronized (this)
            {
                //Incrementa o numero de threads q querem ler
                nQuerLer++;
                
                //Espera ate q nao tenha ninguem pra escrever
                while(nQuerEscrever != 0) wait();
                
                nQuerLer--;
                
                //Incrementa o numero de leitores
                nLeitores++;
                
                //Se tiver alguem na lista libera
                if(nQuerLer != 0) notify();
            }
            
            List<Roupa> r = daoRoupa.queryForAll();
            
            synchronized (this)
            {
                nLeitores--;
                
                if(nLeitores == 0) notify();
            }
            
            //Retora p/ o cliente
            return gson.toJson(r);  
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Erro ou esta vazio
        return new String();
    }

    public String listarRoupasMaisVendidas()
    {
        try 
        {
            synchronized (this)
            {
                //Incrementa o numero de threads q querem ler
                nQuerLer++;
                
                //Espera ate q nao tenha ninguem pra escrever
                while(nQuerEscrever != 0) wait();
                
                nQuerLer--;
                
                //Incrementa o numero de leitores
                nLeitores++;
                
                //Se tiver alguem na lista libera
                if(nQuerLer != 0) notify();
            }
            
            List<Venda> v = daoVenda.queryBuilder().orderBy("quantidade", true).query();
            
            synchronized (this)
            {
                nLeitores--;
                
                if(nLeitores == 0) notify();
            }
            
            return gson.toJson(v);  
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Erro ou esta vazio
        return new String();
    }

    public String listarRoupasMaisBartas()
    {
        try 
        {
            synchronized (this)
            {
                //Incrementa o numero de threads q querem ler
                nQuerLer++;
                
                //Espera ate q nao tenha ninguem pra escrever
                while(nQuerEscrever != 0) wait();
                
                nQuerLer--;
                
                //Incrementa o numero de leitores
                nLeitores++;
                
                //Se tiver alguem na lista libera
                if(nQuerLer != 0) notify();
            }
            
            List<Roupa> r = daoRoupa.queryBuilder().orderBy("preco", true).query();
            
            synchronized (this)
            {
                nLeitores--;
                
                if(nLeitores == 0) notify();
            }
            
            return gson.toJson(r);  
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Erro ou esta vazio
        return new String();
    }

    public void comprarRoupa(Integer id, Integer idRoupa, String data)
    {
        Usuario u = null;
        Roupa r = null; 
        
        try {
            synchronized (this)
            {
                //Incrementa o numero de threads q querem ler
                nQuerLer++;
                
                //Espera ate q nao tenha ninguem pra escrever
                while(nQuerEscrever != 0) wait();
                
                nQuerLer--;
                
                //Incrementa o numero de leitores
                nLeitores++;
                
                //Se tiver alguem na lista libera
                if(nQuerLer != 0) notify();
            }
            
            u = daoUsuario.queryForId(id); 
            r = daoRoupa.queryForId(idRoupa);
            
            synchronized (this)
            {
                nLeitores--;
                
                if(nLeitores == 0) notify();
            }
        } 
        catch (SQLException ex) {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(u == null || r == null)
            return;
        
        if(u.dinheiro < r.preco)
            return;
        
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        
        Venda v = new Venda();
        
        v.id_user = id;
        v.id_roupa = idRoupa;
        v.quantidade = 1;
        
        try 
        {            
            v.data = fm.parse(data);
            
            u.dinheiro -= r.preco;
            
            synchronized (this)
            {
                //Aumenta o numero das threads q querem escrever
                nQuerEscrever++;
                
                //Espera ate nao ter nenhuma thread lendo
                while(nLeitores != 0) wait();
                
                //Decrementa o numero das threads q querem escrever
                nQuerEscrever--;
                
                //Escreve a partir daqui
                daoVenda.create(v);
                daoUsuario.update(u);
                
                //Libera uma thread
                notify();
            }
        } 
        catch (ParseException | SQLException ex) 
        {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    public void devolverRoupa(Integer id)
    {
        try 
        {
            synchronized (this)
            {
                //Incrementa o numero de threads q querem ler
                nQuerLer++;
                
                //Espera ate q nao tenha ninguem pra escrever
                while(nQuerEscrever != 0) wait();
                
                nQuerLer--;
                
                //Incrementa o numero de leitores
                nLeitores++;
                
                //Se tiver alguem na lista libera
                if(nQuerLer != 0) notify();
            }
            
            Venda v = daoVenda.queryForId(id);
            
            synchronized (this)
            {
                nLeitores--;
                
                if(nLeitores == 0) notify();
            }

            //Testa se achou o id
            if(v == null)
                return;

            //Diminui a qtd
            v.quantidade--;
            
            synchronized (this)
            {
                //Aumenta o numero das threads q querem escrever
                nQuerEscrever++;
                
                //Espera ate nao ter nenhuma thread lendo
                while(nLeitores != 0) wait();
                
                //Decrementa o numero das threads q querem escrever
                nQuerEscrever--;
                
                //Escreve a partir daqui
                if(v.quantidade <= 0)
                    daoVenda.delete(v);
                else
                    daoVenda.update(v);
                
                //Libera uma thread
                notify();
            }
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (InterruptedException ex) {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String listarCompras(Integer id)
    {
        try 
        {
            synchronized (this)
            {
                //Incrementa o numero de threads q querem ler
                nQuerLer++;
                
                //Espera ate q nao tenha ninguem pra escrever
                while(nQuerEscrever != 0) wait();
                
                nQuerLer--;
                
                //Incrementa o numero de leitores
                nLeitores++;
                
                //Se tiver alguem na lista libera
                if(nQuerLer != 0) notify();
            }
            
            List<Venda> v = daoVenda.queryForEq("id_user", id);
            
            synchronized (this)
            {
                nLeitores--;
                
                if(nLeitores == 0) notify();
            }
            
            return gson.toJson(v);  
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {   
            Logger.getLogger(BancoDados.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Erro ou esta vazio
        return new String();
    }
}
