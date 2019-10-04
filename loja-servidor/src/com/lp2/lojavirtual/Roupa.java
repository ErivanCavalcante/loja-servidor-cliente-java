package com.lp2.lojavirtual;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

//Estrutura de uma roupa
@DatabaseTable(tableName = "Roupas")
public class Roupa 
{
    @DatabaseField(id = true)
    public int id;
    
    @DatabaseField
    public String marca;
    
    @DatabaseField
    public String categoria;
    
    @DatabaseField
    public String cor;
    
    @DatabaseField(columnName = "quantidade")
    public int qtd;
    
    @DatabaseField
    public float preco;
}
