package com.lp2.lojavirtual;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

//Estrutura de um usuarios
@DatabaseTable(tableName = "Login")
public class Usuario 
{
    @DatabaseField(id = true)
    public int id;
    
    @DatabaseField
    public String email;
    
    @DatabaseField
    public  String senha;
    
    @DatabaseField(columnName = "tipo_usuario")
    public int tipo;
    
    @DatabaseField
    public float dinheiro;
}
