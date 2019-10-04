/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lp2.lojavirtual;

/**
 *
 * @author Vanessa
 */
public class Escritor 
{
    //MEtodo para escrever no log
    synchronized public void escrever(String str)
    {
        System.out.println(str);
    }
}
