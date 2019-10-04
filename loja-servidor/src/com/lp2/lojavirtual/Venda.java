package com.lp2.lojavirtual;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;

//Estrutura de uma venda
@DatabaseTable(tableName = "Vendas")
public class Venda 
{
    @DatabaseField(id = true)
    public int id;
    
    @DatabaseField
    public int id_user;
    
    @DatabaseField
    public int id_roupa;
    
    @DatabaseField
    public int quantidade;
    
    @DatabaseField(dataType = DataType.DATE_STRING,format = "yyyy-MM-dd")
    public Date data;
    
    //@DatabaseField(columnName = "id_roupa", foreign = true)
    //public Roupa roupa;
}
